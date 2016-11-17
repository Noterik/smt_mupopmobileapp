package org.springfield.lou.controllers;

import org.springfield.fs.FSList;
import org.springfield.fs.FsNode;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.controllers.interactivevideo.InteractiveVideoRemoteController;
import org.springfield.lou.controllers.interactivevideo.AudioCheckController;
import org.springfield.lou.controllers.interactivevideo.HeadphonesController;
import org.springfield.lou.controllers.interactivevideo.InteractiveVideoHolderController;
import org.springfield.lou.controllers.intro.language.LanguageSelectionRemoteController;
import org.springfield.lou.controllers.intro.language.LanguageSelectionRemoteControllerMupop;
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
	
	String style = null;
	FsNode exhibition = model.getNode("@exhibition");
	if (exhibition != null) {
	    style = exhibition.getProperty("style");
	}
	
	if (style == null || style.equals("neutral")) {
	    screen.loadStyleSheet("mobile/styles/neutral.css");
	} else if (style.equals("leuven")) {
	    screen.loadStyleSheet("mobile/styles/leuven.css");
	} else if (style.equals("soundandvision")) {
	    screen.loadStyleSheet("mobile/styles/soundandvision.css");
	}
	
	//Get user language from session
	String userLanguage = model.getProperty("@userlanguage");
	
	//get languages from the languages
	FSList languageList = model.getList("@languages");
	
	model.onNotify("@exhibition/intro/languageselection", "onLanguageSelected", this);
	
	//only show languageselection when multiple languages are configured
	if (languageList != null && languageList.size() > 1) {
	    //only show language selection when the user has not language selected earlier or the selected
	    //language is not available in this exhibition
	    if (userLanguage == null || languageList.getNodesById(userLanguage).size() == 0) {
	    	if (model.getProperty("@exhibitionid").equals("1475504815025")){
	    		screen.get("#mobile").append("div", "languageselectionmupopremote", new LanguageSelectionRemoteControllerMupop());
	    	}else{
	    		screen.get("#mobile").append("div", "languageselectionremote", new LanguageSelectionRemoteController());
	    	}
	    } else {
		stationSelection();
	    }
	} else {
	    if (languageList != null) {
		if (userLanguage == null) {
		    //Set user language to the only language available
		    model.setProperty("@userlanguage", languageList.getNodes().get(0).getId());
		}
	    } else {
		System.out.println("No languages set! Please add at least one language!");
	    }
	    stationSelection();
	}
    }
    
    private void stationSelection() {
	//check if multiple stations are configured	
	FSList list = model.getList("@stations");

	if (list!=null) {
	    //in case of a single station directly go to this station
	    if (list.size() == 1) {
		 FsNode station = list.getNodes().get(0);
		 model.setProperty("@stationid", station.getId());
		 openApp();	
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
	    String originalController = target.getProperty("originalcontroller");

	    screen.get("#"+originalController).remove();
	    if (model.getProperty("@exhibitionid").equals("1475504815025")){
    		screen.get("#mobile").append("div", "languageselectionmupopremote", new LanguageSelectionRemoteControllerMupop());
    	}else{
    		screen.get("#mobile").append("div", "languageselectionremote", new LanguageSelectionRemoteController());
    	}
	    model.onNotify("/shared/exhibition/intro/languageselection", "onLanguageSelected", this);
	}
    }
	
    public void onLanguageSelected(ModelEvent e) {
    	System.out.println("LANGUAGE SELECTION EVENT!!!!");
		FsNode target = e.getTargetFsNode();
		System.out.println("LANGUAGE PAGE REQUESTED");
		//only reach device that triggered this event
		if (!target.getProperty("deviceid").equals(deviceid)) {
		    return;
		}
			
		if (target.getId().equals("selected")) {
		    screen.get("#languageselectionmupopremote").remove();
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
		openApp();
		
	    } else {
		//TODO: should show some illegal station controller with urls to all the valid ones?
		screen.get("#mobile_content").html("");
	    }
	}	
    }
    
    public void openApp() {
	FsNode stationnode = model.getNode("@station");
	
	String app =  stationnode.getProperty("app"); // get the app name
	if (app!=null) {
	    if (app.equals("photoexplore")) {
		screen.get("#mobile").append("div","photoexploreremote",new PhotoExploreRemoteController());
	    } else if (app.equals("photoinfospots")) {
		screen.get("#mobile").append("div","photoinfospotsremote",new PhotoInfoSpotsRemoteController());
	    } else if (app.equals("interactivevideo")) {
	    screen.get("#mobile").append("div","interactivevideoremoteholder", new InteractiveVideoHolderController());
		}
	} else {
	    //TODO: should display error that no app was selected and curator should set it
	    screen.get("#mobile_content").html("");
	}
    }
}
