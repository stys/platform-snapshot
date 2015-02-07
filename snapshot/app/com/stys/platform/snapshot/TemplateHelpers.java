package com.stys.platform.snapshot;

import play.mvc.Controller;

public class TemplateHelpers extends Controller {

	public static boolean snapshotting() {
		
		return SnapshotUtils.snapshotRequested(ctx());
		
	}
	
}
