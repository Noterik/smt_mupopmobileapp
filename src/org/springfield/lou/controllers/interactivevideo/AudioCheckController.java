package org.springfield.lou.controllers.interactivevideo;

import org.json.simple.JSONObject;
import org.springfield.fs.FsNode;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.model.ModelEvent;
import org.springfield.lou.screen.Screen;

public class AudioCheckController extends Html5Controller  {
		
	public AudioCheckController() {
	}
	
	public void attach(String sel) {
		selector = sel;
 		screen.get(selector).render(); 		
 		
 		screen.get("#ap_back_button").on("click", "onBackButton", this);
	}
	
	public void onBackButton(Screen s, JSONObject data){
		System.out.println("AUDIO PROBLEM BACK BUTTON");
		screen.get("#interactivevideoremote").show();
		screen.get(selector).remove();
	}
}
