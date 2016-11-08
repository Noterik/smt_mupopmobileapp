package org.springfield.lou.controllers.interactivevideo;

import org.json.simple.JSONObject;
import org.springfield.fs.FsNode;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.screen.Screen;

public class ExhibitionEndController extends Html5Controller{
	
	public void attach(String sel) {
		selector = sel;
		screen.get(selector).render();
		screen.get("#station_rejoin").on("click", "onRejoin", this);
	}

	public void onRejoin(Screen s, JSONObject data){
		screen.get("#exhibitionend").remove();
		screen.get("#mobile_content").append("div", "interactivevideoremote", new InteractiveVideoRemoteController());
		screen.get("#interactivevideoremote").append("div","audiocheck", new AudioCheckController());
	}
}


