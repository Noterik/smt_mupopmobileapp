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
 		screen.get("#audio_yes_button").on("click", "onAudioOk", this);
 		screen.get("#audio_no_button").on("click", "onAudioNotOk", this);
	}
	
	public void onAudioOk(Screen s, JSONObject data){
		String stationid = model.getProperty("@stationid");
		String exhibitionid = model.getProperty("@exhibitionid");
		screen.get("#audiocheck").remove();
	}
	
	
	public void onAudioNotOk(Screen s, JSONObject data){
		String stationid = model.getProperty("@stationid");
		String exhibitionid = model.getProperty("@exhibitionid");
//		if(screen.getModel().getProperty("/screen/isMasterRemote").equals("true")){
//			System.out.println("We have to pause the video");
//			screen.getModel().notify("/shared/exhibition/"+exhibitionid+"/station/"+ stationid +"/vars/pauseClock", new FsNode("item","1"));	
//		}else{
//			System.out.println("Video will continue, but this screen has to reselect language");
//		}
		//go back to the language selection screen
		screen.get(selector).remove();
		screen.get("#interactivevideoremote").remove();
	}
}
