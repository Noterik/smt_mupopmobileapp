package org.springfield.lou.controllers.interactivevideo;

import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONObject;
import org.springfield.fs.FSList;
import org.springfield.fs.FSListManager;
import org.springfield.fs.FsNode;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.controllers.intro.language.LanguageSelectionRemoteControllerMupop;
import org.springfield.lou.model.ModelBindEvent;
import org.springfield.lou.model.ModelEvent;
import org.springfield.lou.screen.Screen;

public class InteractiveVideoHolderController extends Html5Controller  {
	
	FSList list;
	
	public InteractiveVideoHolderController() {
		
	}
	
	public void attach(String sel) {
		selector = sel;
		String stationid = model.getProperty("@stationid");
		String exhibitionid = model.getProperty("@exhibitionid");
		FsNode audio_settings = model.getNode("/domain/mupop/user/daniel/exhibition/"+exhibitionid+"/station/"+stationid+"/video/1/audio/1/");
		String audioUrl = audio_settings.getSmartProperty(model.getProperty("@userlanguage"), "url");
		JSONObject jso= new JSONObject(); 
 		jso.put("url", audioUrl);
		screen.get(selector).render(jso);
		
		screen.get("#mobile").append("div","headphonescheck", new HeadphonesController());
	}
	
	
	public void destroyed() {
		super.destroyed();
	}
}
