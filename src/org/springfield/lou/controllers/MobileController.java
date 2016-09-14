package org.springfield.lou.controllers;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONObject;
import org.springfield.fs.FSList;
import org.springfield.fs.FSListManager;
import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.controllers.photoexplore.PhotoExploreRemoteController;
import org.springfield.lou.controllers.photoinfospots.PhotoInfoSpotsRemoteController;
import org.springfield.lou.screen.Screen;

public class MobileController extends Html5Controller {
	

	public MobileController() {
	}
	
	public void attach(String sel) {
		System.out.println("mobile controller attached called");
		selector = sel;
		fillPage();
	}
	
	private void fillPage() {
		String stationpath  = model.getProperty("/screen/exhibitionpath")+"/station";
		FSList list = model.getList(stationpath);
		if (list!=null) {
			JSONObject data = FSList.ArrayToJSONObject(list.getNodes(),"en","labelid");
			screen.get(selector).render(data);
		} else {
			screen.get(selector).render();
		}
		screen.get("#mobile_input").on("change","selectorButton",this);
	}

	
	
	public void selectorButton(Screen s,JSONObject data) {
		String stationpath  = model.getProperty("/screen/exhibitionpath");
		System.out.println(stationpath+"/station/"+(String)data.get("value"));	
		FsNode stationnode = model.getNode(stationpath+"/station/"+(String)data.get("value"));	
		if (stationnode!=null) {
			System.out.println("STATIONNODE="+stationnode.asXML());
			String app =  stationnode.getProperty("app"); // get the app name
			if (app!=null) {
				//TODO: should be a case or loaded system
				if (app.equals("photoexplore")) {
    				screen.get("#mobile_content").append("div","photoexploreremote",new PhotoExploreRemoteController());
				} else if (app.equals("photoinfospots")) {
					screen.get("#mobile_content").append("div","photoinfospotsremote",new PhotoInfoSpotsRemoteController());
				}
			} else {
				// should display error that no app was selected and curator should set it
				screen.get("#mobile_content").html("");
			}
		} else {
			// should show some illegal station controller with urls to all the valid ones?
			screen.get("#mobile_content").html("");
		}
	}
			

 	 
}
