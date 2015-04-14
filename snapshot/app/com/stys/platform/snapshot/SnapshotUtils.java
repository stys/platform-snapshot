package com.stys.platform.snapshot;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

import play.Play;
import play.Logger;
import play.mvc.Http.Context;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class SnapshotUtils {
	
	/*
	 *  Escaped fragment - this is added to request by Google and Yandex crawlers
	 */
	private static final String ESCAPED_FRAGMENT = "_escaped_fragment_";
	
	/*
	 *  Key added to a snapshotting request, so that we can render something special 
	 */
	private static final String PLAY_SNAPSHOT = "_play_snapshot_";
	
	/*
	 * Configuration key for javascript timeout
	 */
	private static final String JAVASCRIPT_TIMEOUT_KEY = "platform.snapshot.waitForJavascriptMs";
	
	/*
	 * Configuration key for HtmlUnit browser version 
	 */
	private static final String BROWSER_VERSION_KEY = "platform.snapshot.browserVersion";
	
	/*
	 *  The request which includes "_escaped_fragment_=" requires snapshotting
	 */
	public static boolean snapshotRequired(Context ctx) {
		return ctx.request().method().equals("GET") && ctx.request().queryString().containsKey(ESCAPED_FRAGMENT);
	}
	
	/*
	 *  The request which includes "_play_snapshot_" is a request from snapshotter
	 */
	public static boolean snapshotRequested(Context ctx) {
		return ctx.request().method().equals("GET") && ctx.request().queryString().containsKey(PLAY_SNAPSHOT);
	}
	
	/*
	 *  Replaces "_escaped_fragment_=" with "#!" and adds our own "_play_snapshot_="  
	 */
	public static String getSnapshotUrl(Context ctx) throws Exception {
		// get query string 
		Map<String, String[]> qs = ctx.request().queryString();
		// get fragment from collection
		String escapedFragment = qs.get(ESCAPED_FRAGMENT)[0];
		// begin building modified url		
		StringBuilder url = new StringBuilder("http://");
		// append host and path from original request
		url.append(ctx.request().host());
		url.append(ctx.request().path());
		// begin query part
		url.append("?");
		// append back all original query parameters except removed fragment
		for(Entry<String, String[]> q : qs.entrySet()) {
			// skip _escaped_fragment_
			if( ESCAPED_FRAGMENT.equals(q.getKey())) {
				continue;
			}
			// encode current key
			String key = URLEncoder.encode(q.getKey(), "UTF-8");
			// put all values for current key	
			for( String val : q.getValue() ) {
				String value = URLEncoder.encode(val, "UTF-8");
				url.append(key).append("=").append(value).append("&");
			}
		}
		
		// It is sometimes useful to know that we are processing a snapshotting request. 
		// In such a case we can modify templates or do something else.
		// See for example http://help.yandex.com/webmaster/robot-workings/ajax-indexing.xml
		// In that case we need to remove <meta name="fragment" content="!"> when rendering
		// a snapshot, or otherwise the page will not get indexed.
		
		// Add our special query parameter "_play_snapshot_" to query parameters
		url.append(PLAY_SNAPSHOT);
		// finally append back escaped fragment after hash-bang
		url.append("#!").append(URLDecoder.decode(escapedFragment, "UTF-8"));
		// return complete url
		return url.toString();
	}
	
	// Do snapshotting with HtmlUnit
	public static String getSnapshot(String url) throws Exception {

		Logger.debug("Snapshotting " + url);
		
		// Load configuration settings
		int timeout = Play.application().configuration().getInt(JAVASCRIPT_TIMEOUT_KEY, 2000);
		Logger.debug("Timeout " + timeout);
		
		String version = Play.application().configuration().getString(BROWSER_VERSION_KEY);
	
		// create headless browser instance
		BrowserVersion browserVersion = BrowserVersion.FIREFOX_24;
		if( null != version ) {
			if( version.equals("CHROME") ) browserVersion = BrowserVersion.CHROME;
			if( version.equals("FIREFOX_24") ) browserVersion = BrowserVersion.FIREFOX_24;
			if( version.equals("INTERNET_EXPLORER_11") ) browserVersion = BrowserVersion.INTERNET_EXPLORER_11;
		}
		
		WebClient client = new WebClient(browserVersion);
		
		// ignore javascript errors
		client.getOptions().setThrowExceptionOnScriptError(false);
		
		// load requested page
		HtmlPage page = client.getPage(url);
		// give time for javascript to complete
		client.waitForBackgroundJavaScript(timeout);	
		// get rendered contents as a string
		String rendered = page.asXml();
		
		// Manually replace <?xml version=\"1.0\" encoding=\"utf-8\"?>
		// because HtmlUnit deletes <!DOCTYPE html> and puts <?xml...>.
		rendered = rendered.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "<!DOCTYPE html>");
		
		// close browser
		client.closeAllWindows();
		// return rendered result
		return rendered.trim();
	}
}
