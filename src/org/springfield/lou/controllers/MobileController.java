package org.springfield.lou.controllers;

import org.springfield.fs.FSList;
import org.springfield.fs.FsNode;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.controllers.interactivevideo.InteractiveVideoRemoteController;
import org.springfield.lou.controllers.intro.language.LanguageSelectionRemoteController;
import org.springfield.lou.controllers.intro.station.StationSelectionRemoteController;
import org.springfield.lou.controllers.photoexplore.PhotoExploreRemoteController;
import org.springfield.lou.controllers.photoinfospots.PhotoInfoSpotsRemoteController;
import org.springfield.lou.model.ModelEvent;

public class MobileController extends Html5Controller {
	
    String deviceid;
    
    public MobileController() { }
	
    public void attach(String sel) {	
	selector = sel;
	deviceid = model.getProperty("@deviceid");
	
	fillPage();
    }
	
    private void fillPage() {
	//fill username and exhibition id
	String exhibitionpath  = model.getProperty("/screen/exhibitionpath");
	String[] parts = exhibitionpath.split("/");
	screen.getModel().setProperty("@username", parts[4]);
	screen.getModel().setProperty("@exhibitionid", parts[6]);
	
	//Get user language from session
	String userLanguage = model.getProperty("@userlanguage");
	    
	//get languages from the languages
	FSList languageList = model.getList("@languages");
	
	//only show languageselection when multiple languages are configured
	if (languageList != null && languageList.size() > 1) {
	    //only show language selection when the user has not language selected earlier or the selected
	    //language is not available in this exhibition
	    System.out.println("language available ? "+languageList.getNodesById(userLanguage).size());
	    if (userLanguage == null || languageList.getNodesById(userLanguage).size() == 0) {
		screen.get("#mobile").append("div", "languageselectionremote", new LanguageSelectionRemoteController());
		model.onNotify("/shared/exhibition/intro/languageselection", "onLanguageSelected", this);
	    } else {
		stationSelection();
	    }
	} else {
	    stationSelection();
	}
    }
    
    private void stationSelection() {
	//check if multiple stations are configured
	String stationpath  = model.getProperty("/screen/exhibitionpath")+"/station";
	FSList list = model.getList(stationpath);
	if (list!=null) {
	    //in case of a single station directly go to this station
	    if (list.size() == 1) {
			
	    } else {
		screen.get("#mobile").append("div", "stationselectionremote", new StationSelectionRemoteController());
		model.onNotify("/shared/exhibition/intro/stationselection", "onStationSelected", this);
		model.onNotify("/shared/exhibition/intro/languagepage", "onLanguagePageRequested", this);
	    }		    
	} else {
	    //TODO: Error: no station found for this exhibition
	}
    }
    
    public void onLanguagePageRequested(ModelEvent e) {
	FsNode target = e.getTargetFsNode();
	    
	//only reach device that triggered this event
	if (!target.getProperty("deviceid").equals(deviceid)) {
	    return;
	}
		
	if (target.getId().equals("requested")) {		
	    screen.get("#stationselectionremote").remove();
	    screen.get("#mobile").append("div", "languageselectionremote", new LanguageSelectionRemoteController());
	    model.onNotify("/shared/exhibition/intro/languageselection", "onLanguageSelected", this);
	}
    }
	
    public void onLanguageSelected(ModelEvent e) {
	FsNode target = e.getTargetFsNode();
	    
	//only reach device that triggered this event
	if (!target.getProperty("deviceid").equals(deviceid)) {
	    return;
	}
		
	if (target.getId().equals("selected")) {		
	    screen.get("#languageselectionremote").remove();
	    stationSelection();
	}
    }
	
    public void onStationSelected(ModelEvent e) {
	FsNode target = e.getTargetFsNode();
	
	//only reach device that triggered this event
	if (!target.getProperty("deviceid").equals(deviceid)) {
	    return;
	}
	
	if (target.getId().equals("selected")) {		
	    screen.get("#stationselectionremote").remove();
	    
	    String exhibitionpath  = model.getProperty("/screen/exhibitionpath");
	    FsNode stationnode = model.getNode(exhibitionpath+"/station/"+(String)target.getProperty("stationid"));	
    	
	    if (stationnode!=null) {
		model.setProperty("@stationid", stationnode.getId());
		String app =  stationnode.getProperty("app"); // get the app name
		if (app!=null) {
		    if (app.equals("photoexplore")) {
			screen.get("#mobile").append("div","photoexploreremote",new PhotoExploreRemoteController());
		    } else if (app.equals("photoinfospots")) {
			screen.get("#mobile").append("div","photoinfospotsremote",new PhotoInfoSpotsRemoteController());
		    } else if (app.equals("interactivevideo")) {
			screen.get("#mobile").append("div","interactivevideoremote",new InteractiveVideoRemoteController());
		    }
		} else {
		    //TODO: should display error that no app was selected and curator should set it
		    screen.get("#mobile_content").html("");
		}
	    } else {
		//TODO: should show some illegal station controller with urls to all the valid ones?
		screen.get("#mobile_content").html("");
	    }
	}	
    }
}
