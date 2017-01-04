package org.springfield.lou.controllers.interactivevideo;


import org.json.simple.JSONObject;
import org.springfield.fs.FsNode;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.controllers.intro.language.LanguageSelectionRemoteControllerMupop;
import org.springfield.lou.screen.Screen;

public class HeadphonesController extends Html5Controller {
	
	String sharedspace;
	String deviceid;
	public HeadphonesController() {
	}
	
	public void attach(String sel) {
		selector = sel;
		//model.setProperty("@videoid", ""+1);
		//deviceid = model.getProperty("@deviceid");
		//System.out.println("DEVICEID:::::: " + deviceid);
		screen.get(selector).render();
		screen.get("#play-icon").on("click", "onPlay", this);
		screen.get(".earlierpage").on("click", "onEarlierPageClicked", this);
	}
	
	public void onPlay(Screen s, JSONObject data){
		System.out.println("ON PLAY");
		//screen.get("#mobile").append("div","interactivevideoremote",new InteractiveVideoRemoteController());
		screen.get(selector).remove();
		screen.get("#mobile").update(new JSONObject());
		FsNode message = new FsNode("message",screen.getId());
		message.setProperty("request","join");
		model.notify("@exhibitionevents/fromclient",message);
	}
	
	/*
	public void onEarlierPageClicked(Screen s, JSONObject data) {
		screen.get("#mobile").append("div", "languageselectionmupopremote", new LanguageSelectionRemoteControllerMupop());
		screen.get(selector).remove();
    }
    */
}
