package com.stys.platform.snapshot;

import static com.stys.platform.snapshot.SnapshotUtils.getSnapshot;
import static com.stys.platform.snapshot.SnapshotUtils.getSnapshotUrl;
import static com.stys.platform.snapshot.SnapshotUtils.snapshotRequired;
import play.libs.F.Function;
import play.libs.F.Function0;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;

/**
 * Snapshotting action
 */
public class SnapshotAction extends Action.Simple {

	@Override
	public Promise<Result> call(final Context ctx) throws Throwable {
		
		// check if snapshotted version is being requested
		if(snapshotRequired(ctx)) {	
			
			final String snapshotUrl = getSnapshotUrl(ctx);
			
			// See: http://www.playframework.com/documentation/2.3.x/JavaAsync
			Promise<String> promiseOfRenderedPage = Promise.promise(new Function0<String>(){
				@Override
				public String apply() throws Throwable {
					return getSnapshot(snapshotUrl);
				}
			});
			// return promise of the result
			return promiseOfRenderedPage.map( new Function<String, Result> () {
				@Override
				public Result apply(String rendered) throws Throwable {
					ctx.response().setContentType("text/html");
					// return rendered page
					return ok(rendered);
				}			
			});
		
		// otherwise pass execution to the next action
		} else {
			return delegate.call(ctx);
		}
	}
}
