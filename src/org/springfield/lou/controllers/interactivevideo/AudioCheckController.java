package org.springfield.lou.controllers.interactivevideo;

import org.json.simple.JSONObject;
import org.springfield.fs.FsNode;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.screen.Screen;

public class AudioCheckController extends Html5Controller  {
		
	public AudioCheckController() {
	}
	
	public void attach(String sel) {
		selector = sel;
 		screen.get(selector).render(); 		
 		
 		screen.get("#ap_back_button").on("click", "onBackButton", this);
 		screen.get("#home_back_button").on("click", "onRefresh", this);
	}
	
	public void onBackButton(Screen s, JSONObject data){
		System.out.println("AUDIO PROBLEM BACK BUTTON");
		screen.get("#interactivevideoremote").show();
		screen.get(selector).remove();
	}
	
	public void onRefresh(Screen s, JSONObject data) {
	    System.out.println("Home button pressed");
	    
	    FsNode node = new FsNode("page", "requested");
	    node.setProperty("exhibition", model.getProperty("@exhibitionid"));
		
	    model.notify("@exhibition/entryscreen/requested", node);
	}
}
