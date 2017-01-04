package org.springfield.lou.controllers;

import org.json.simple.JSONObject;
import org.springfield.fs.FSList;
import org.springfield.fs.FsNode;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.controllers.image.selection.CoverFlowRemoteController;
import org.springfield.lou.controllers.image.zoom.PhotoExplorerRemoteController;
import org.springfield.lou.controllers.interactivevideo.InteractiveVideoRemoteController;
import org.springfield.lou.controllers.interactivevideo.AudioCheckController;
import org.springfield.lou.controllers.interactivevideo.HeadphonesController;
import org.springfield.lou.controllers.interactivevideo.InteractiveVideoHolderController;
import org.springfield.lou.controllers.intro.language.LanguageSelectionRemoteController;
import org.springfield.lou.controllers.intro.language.LanguageSelectionRemoteControllerMupop;
import org.springfield.lou.controllers.intro.station.StationCodeSelectionRemoteController;
import org.springfield.lou.controllers.intro.station.StationSelectionRemoteController;
import org.springfield.lou.controllers.photoexplore.PhotoExploreRemoteController;
import org.springfield.lou.controllers.photoinfospots.PhotoInfoSpotsRemoteController;
import org.springfield.lou.model.ModelEvent;
import org.springfield.lou.screen.Screen;

public class MobileController extends Html5Controller {
	
    String oldstate="";
    
    public MobileController() { }
    // lang sel.
    // station select
    // content sel
    // main app
    // feedback 
    
    public void attach(String sel) {	
	selector = sel;
		
	model.onPropertyUpdate("/screen/state","onStateChange",this);
	model.setProperty("/screen/state","init"); // will trigger a event 
	//screen.get("#screen").track("location","onGPSLocation", this);
	System.out.println("ATTACH DONE");
    }
    
   
	
    public void onStateChange(ModelEvent event) {
	String state = event.getTargetFsNode().getProperty("state");
	if (oldstate.equals(state)) return;
	oldstate = state;
	System.out.println("STEP="+state);
    	if (state.equals("init")) { // init the exhibition and probably show language selector
    		initStep(); 
    	} else if (state.equals("stationselect")) { // check station selection method if more than one station
        	stationSelectStep();
    	} else if (state.equals("contentselect")) { // use a 'pre-app' to select the content we want to use (like coverflow)
        	contentSelectStep();
    	} else if (state.equals("mainapp")) { // check station selection method if more than one station
        	mainAppStep();
    	} else if (state.equals("feedback")) { // check station selection method if more than one station
        	feedbackStep();
        }
    	return;
    }
    
    private void initStep() {
    	String style = model.getProperty("@exhibition/style");
    	if (style==null || style.equals("")) {
    		style="neutral";
    	}
    	screen.loadStyleSheet("mobile/styles/"+style+".css");
		
    	String languageselect = model.getProperty("@exhibition/languageselect");
    	if (languageselect!=null && !languageselect.equals("")) {
    	    // so exhibition wants language selector
    	    // first lets check if user already has one selected if so we skip on init state
    	    String userLanguage = model.getProperty("@userlanguage");

    	    System.out.println("USERLANGUAGE FROM 'COOKIE' "+userLanguage);
    	    if (userLanguage==null || userLanguage.equals("")) {
        	if (languageselect.equals("default")) {
        	    screen.get("#mobile").append("div", "languageselectionremote", new LanguageSelectionRemoteController());
        	} else if (languageselect.equals("flags")) {
            		screen.get("#mobile").append("div", "languageselectionmupopremote", new LanguageSelectionRemoteControllerMupop());
        	}
        	return;
    	    }
    	} 
	// move to the next logical state
	FsNode message = new FsNode("message",screen.getId());
    	message.setProperty("request","join");
    	model.notify("@exhibitionevents/fromclient",message);
	return;
    }
    
    private void stationSelectStep() {
	resetScreen();
    	
    	String type = model.getProperty("@exhibition/stationselect");
    	System.out.println("MuPoP: station select step called ="+type);
    	//check if multiple stations are configured	
    	
    	if (type==null || type.equals("") || type.equals("none")) {
    	    // show the first available that is online?
    	    FSList list = model.getList("@stations");
    	    if (list!=null) {
    		//in case of a single station directly go to this station
    		if (list.size() > 0) {
    		    FsNode station = list.getNodes().get(0);
    		    FsNode message = new FsNode("message",screen.getId());
    		    message.setProperty("request","station");
    		    model.setProperty("@stationid", station.getId());
    		    message.setProperty("@stationid", station.getId());
    		    model.notify("@stationevents/fromclient",message);
    		    model.setProperty("/screen/state","mainapp"); // lets move to the main app since we only have 1 station
    		}
    	    }
    	} else if (type.equals("listview")) {
    	    System.out.println("START LISTVIEW");
    	    screen.get("#mobile").append("div", "stationselectionremote", new StationSelectionRemoteController());
    	} else if (type.equals("codeselect")) {
    	    System.out.println("START CODESELECT");
    	    screen.get("#mobile").append("div", "stationcodeselectionremote", new StationCodeSelectionRemoteController());
    	}		    
    }
    
    private void contentSelectStep() {
    	resetScreen();
    	
	String type = model.getProperty("@station/contentselect");
    	System.out.println("MuPoP: content select step called ="+type);
    	if (type!=null && !type.equals("")) {
    	    if (type.equals("coverflow")) {
    		System.out.println("COVERFLOW WANTED");
    		screen.get("#mobile").append("div", "coverflowremote", new CoverFlowRemoteController());
    	    }
    	}
    }
    	
    private void mainAppStep() {
	resetScreen();
	
	FsNode stationnode = model.getNode("@station");	
    	String app =  stationnode.getProperty("app"); // get the app name
    	System.out.println("MuPoP mobile: mainapp select step called ="+app);
    	if (app!=null) {
    	    if (app.equals("photoexplore")) {
    		screen.get("#mobile").append("div","photoexplorerremote",new PhotoExplorerRemoteController());
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
    
    private void feedbackStep() {
    	String type = model.getProperty("@station/feedbackselect");
    	System.out.println("MuPoP: feedback step called ="+type);
    	if (type!=null && !type.equals("")) {
    	
    	}
    }
    
    public void newGPSLocation(Screen s,JSONObject data) {
	System.out.println("LOCATION="+data.toJSONString());
    }
    
    private void resetScreen() {
	screen.get("#languageselectionremote").remove();	
    	screen.get("#languageselectionmupopremote").remove();
	screen.get("#stationselectionremote").remove();	
    	screen.get("#stationcodeselectionremote").remove();
    	screen.get("#coverflowremote").remove();
    	screen.get("#photoexplorerremote").remove();
    	screen.get("#photoinfospotsremote").remove();
    	screen.get("#interactivevideoremoteholder").remove();
    }
}
