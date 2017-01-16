package org.springfield.lou.controllers;

import org.json.simple.JSONObject;
import org.springfield.fs.FSList;
import org.springfield.fs.FsNode;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.controllers.image.selection.CoverFlowRemoteController;
import org.springfield.lou.controllers.image.zoom.PhotoExplorerRemoteController;
import org.springfield.lou.controllers.interactivevideo.InteractiveVideoRemoteController;
import org.springfield.lou.controllers.intro.audio.AudioTestRemoteController;
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
		System.out.println("ATTACH DONE");
		screen.get(selector).render();
	}

	public void onStateChange(ModelEvent event) {
		String state = event.getTargetFsNode().getProperty("state");
		if (oldstate.equals(state)) return;
		oldstate = state;
		System.out.println("Mobile STEP="+state);

		model.setProperty("@contentrole", state); 	//state and contentrole are the same ????
		if (state.equals("init")) { // init the exhibition and probably show language selector
		    initStep(); 
		} else if (state.equals("audiocheck")) { // perform the asked audio request
			audioCheckStep();
		} else if (state.equals("stationselect")) { // check station selection method if more than one station
			stationSelectStep();
		} else if (state.equals("contentselect") && !model.getProperty("@appstate").equals("mainapp")) { // use a 'pre-app' to select the content we want to use (like coverflow)
			model.onNotify("@stationevents/fromclient","onStationEvent",this);
			contentSelectStep();
		} else if (state.equals("mainapp") || model.getProperty("@appstate").equals("mainapp")) { // check station selection method if more than one station
		    	model.setProperty("@contentrole", "mainapp"); // due to possibility to go here from another state is appstate = mainapp 
		    	model.onNotify("@stationevents/fromclient","onStationEvent",this);
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
			//userLanguage = null;
			if (userLanguage==null || userLanguage.equals("")) {
				if (languageselect.equals("default")) {
					screen.get("#mobile").append("div", "languageselectionremote", new LanguageSelectionRemoteController());
				} else if (languageselect.equals("flags")) {
					screen.get("#mobile").append("div", "languageselectionmupopremote", new LanguageSelectionRemoteControllerMupop());
				} else {
				    //set default language, pick first one
				    String languages = model.getProperty("@exhibition/availablelanguages");
				    String[] languageList = languages.split(",");
				    
				    System.out.println("Picking first language from list "+languageList[0]);
				    
				    model.setProperty("@userlanguage",languageList[0]);
				}
				model.setProperty("/screen/state","audiocheck"); 
				return;
			} 	
		} else {
		    //set default language, pick first one
		    String languages = model.getProperty("@exhibition/availablelanguages");
		    String[] languageList = languages.split(",");
		    
		    System.out.println("Picking first language from list "+languageList[0]);
		    
		    model.setProperty("@userlanguage",languageList[0]);
		}
		// move to the next logical state
		model.setProperty("/screen/state","audiocheck"); 
		return;
	}

	private void audioCheckStep() {
		// check if we need todo a audiocheck 
		String audiocheck = model.getProperty("@exhibition/audiocheck");
		if (audiocheck!=null && audiocheck.equals("true")) {
			// set local action to perform audiocheck
			System.out.println("AUDIOCHECK="+audiocheck);
			screen.get("#mobile").append("div", "audiotestremote", new AudioTestRemoteController());

			//screen.get("#mobile").append("div","headphonescheck", new HeadphonesController());
		} else {
			FsNode message = new FsNode("message",screen.getId());
			message.setProperty("request","join");
			model.notify("@exhibitionevents/fromclient",message);
		}
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
				screen.get("#mobile").append("div","interactivevideoremote", new InteractiveVideoRemoteController());
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

	public void onStationEvent(ModelEvent e) {
		FsNode message = e.getTargetFsNode();
		String request = message.getProperty("request");
		
		System.out.println("Station event received "+message.asXML());
		
		if (request!=null) { 
		    model.setProperty("@contentrole", request);
			if (request.equals("init")) {
				resetScreen();				
				stationSelectStep();
			} else if (request.equals("contentselect")) {
				resetScreen();
				contentSelectStep();
			} else if (request.equals("mainapp")) {
				resetScreen();
				model.setProperty("/screen/state","mainapp");
				System.out.println("Setting item id "+message.asXML());
				model.setProperty("@itemid", message.getProperty("itemid"));
				mainAppStep();
			}
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
