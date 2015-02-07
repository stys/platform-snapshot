package controllers;

import com.stys.platform.snapshot.Snapshot;

import play.mvc.Controller;
import play.mvc.Result;

public class Application extends Controller {
	
	@Snapshot
	public static Result index() {
		return ok(views.html.index.render());
	}
}
