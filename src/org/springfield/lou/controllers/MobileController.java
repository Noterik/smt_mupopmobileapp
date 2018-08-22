/* 
* 
* Copyright (c) 2017 Noterik B.V.
* 
* This file is part of MuPoP framework
*
* MuPoP framework is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* MuPoP framework is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with MuPoP framework .  If not, see <http://www.gnu.org/licenses/>.
*/
package org.springfield.lou.controllers;

import org.json.simple.JSONObject;
import org.springfield.fs.FSList;
import org.springfield.fs.FsNode;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.controllers.image.selection.*;
import org.springfield.lou.controllers.image.zoom.PhotoExplorerRemoteController;
import org.springfield.lou.controllers.interactivevideo.InteractiveVideoRemoteController;
import org.springfield.lou.controllers.intro.audio.AudioTestRemoteController;
import org.springfield.lou.controllers.intro.language.GlobalLanguageSelectionRemoteController;
import org.springfield.lou.controllers.intro.language.LanguageSelectionRemoteController;
import org.springfield.lou.controllers.intro.language.LanguageSelectionRemoteControllerMupop;
import org.springfield.lou.controllers.intro.station.GlobalCodeSelectionRemoteController;
import org.springfield.lou.controllers.intro.station.GlobalNameSelectRemoteController;
import org.springfield.lou.controllers.intro.station.StationCodeSelectionRemoteController;
import org.springfield.lou.controllers.intro.station.StationSelectionRemoteController;
import org.springfield.lou.controllers.photoexplore.PhotoExploreRemoteController;
import org.springfield.lou.controllers.photoinfospots.PhotoInfoSpotsRemoteController;
import org.springfield.lou.controllers.photozoom.PhotoZoomRemoteController;
import org.springfield.lou.controllers.trivia.TriviaRemoteController;
import org.springfield.lou.controllers.quiz.QuizRemoteController;
import org.springfield.lou.controllers.whatwethink.WhatWeThinkRemoteController;
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


		screen.get(selector).render();
	    screen.get(selector).loadScript(this);
		String exh = model.getProperty("@exhibitionid");
		if (exh!=null && exh.equals("unknown")) {
			model.setProperty("/screen/state","globalcodeselect"); // will trigger a event 
			//model.setProperty("/screen/state","globallanguageselect"); // will trigger a event 
		} else {
			model.setProperty("/screen/state","init"); // will trigger a event 
		}
		System.out.println("C1");
	}

	public void onStateChange(ModelEvent event) {
		String state = event.getTargetFsNode().getProperty("state");
		if (oldstate.equals(state)) return;
		oldstate = state;		
		System.out.println("STATE="+state);
		model.setProperty("@contentrole", state); 	//state and contentrole are the same ????
		if (state.equals("init")) { // init the exhibition and probably show language selector
		    initStep(); 
		} else if (state.equals("globalcodeselect")) { // pre-step global code select
			globalSelectStep();
		} else if (state.equals("globalnameselect")) { // we need to select a name based on exhibition
			globalNameSelect();
		} else if (state.equals("globallanguageselect")) { // pre-step global code select
			globalLanguageStep();
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
		System.out.println("INIT STEP CALLED");
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

			//userLanguage = null;
			if (userLanguage==null || userLanguage.equals("")) {
				if (languageselect.equals("default")) {
					screen.get("#mobile").append("div", "languageselectionremote", new LanguageSelectionRemoteController());
					return;
				} else if (languageselect.equals("flags")) {
					screen.get("#mobile").append("div", "languageselectionmupopremote", new LanguageSelectionRemoteControllerMupop());
					return;
				} else {
				    //set default language, pick first one
				    String languages = model.getProperty("@exhibition/availablelanguages");
				    String[] languageList = languages.split(",");
				    
				    
				    model.setProperty("@userlanguage",languageList[0]);
				}
				model.setProperty("/screen/state","audiocheck"); 
				return;
			} 	
		} else {
		    //set default language, pick first one
		    String languages = model.getProperty("@exhibition/availablelanguages");
		    String[] languageList = languages.split(",");
		    
		    
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
			screen.get("#mobile").append("div", "stationselectionremote", new StationSelectionRemoteController());
		} else if (type.equals("codeselect")) {
			screen.get("#mobile").append("div", "stationcodeselectionremote", new StationCodeSelectionRemoteController());
		}		    
	}
	
	private void globalLanguageStep() {
		String userLanguage = model.getProperty("@userlanguage");
		userLanguage = null;
		String style="leuven"; // very temp hack
		screen.loadStyleSheet("mobile/styles/"+style+".css");
		screen.get("#mobile").append("div", "globallanguageselectionremote", new GlobalLanguageSelectionRemoteController());
	}
	
	private void globalSelectStep() {
		resetScreen();
		String style="leuven"; // very temp hack
		screen.loadStyleSheet("mobile/styles/"+style+".css");
		screen.get("#mobile").append("div", "globalcodeselectionremote", new GlobalCodeSelectionRemoteController());		
	}
	
	private void globalNameSelect() {
		resetScreen();
		screen.get("#mobile").append("div", "globalnameselectremote", new GlobalNameSelectRemoteController());		
	}

	private void contentSelectStep() {
		resetScreen();

		String type = model.getProperty("@station/contentselect");
		if (type!=null && !type.equals("")) {
			if (type.equals("coverflow")) {
				screen.get("#mobile").append("div", "coverflowremote", new CoverFlowRemoteController());
			} else if (type.equals("selectionmap")) {
				screen.get("#mobile").append("div", "selectionmapremote", new SelectionMapRemoteController());
			}
		}
	}

	private void mainAppStep() {
		resetScreen();

		FsNode stationnode = model.getNode("@station");	
		String app =  stationnode.getProperty("app"); // get the app name
		System.out.println("WANTED APP="+app);
		if (app!=null) {
			if (app.equals("photoexplore")) {
				screen.get("#mobile").append("div","photoexplorerremote",new PhotoExplorerRemoteController());
			} else if (app.equals("photoinfospots")) {
				screen.get("#mobile").append("div","photoinfospotsremote",new PhotoInfoSpotsRemoteController());
			} else if (app.equals("photozoom")) {
				screen.get("#mobile").append("div","photozoomremote",new PhotoZoomRemoteController());
			} else if (app.equals("interactivevideo")) {
				screen.get("#mobile").append("div","interactivevideoremote", new InteractiveVideoRemoteController());
			} else if (app.equals("trivia")) {
				screen.get("#mobile").append("div","triviaremote", new TriviaRemoteController());
			} else if (app.equals("quiz")) {
				screen.get("#mobile").append("div","quizremote", new QuizRemoteController()); 
			} else if (app.equals("whatwethink")) {
				screen.get("#mobile").append("div","whatwethinkremote", new WhatWeThinkRemoteController());
			}
		} else {
			//TODO: should display error that no app was selected and curator should set it
			screen.get("#mobile_content").html("");
		}
	}

	private void feedbackStep() {
		String type = model.getProperty("@station/feedbackselect");
		if (type!=null && !type.equals("")) {

		}
	}

	public void onStationEvent(ModelEvent e) {
		FsNode message = e.getTargetFsNode();
		String request = message.getProperty("request");
		System.out.println("STATIONEVENT");
		if (request!=null) { 
		    model.setProperty("@contentrole", request);
			if (request.equals("init")) {
				resetScreen();	
				//model.setProperty("/screen/state","stationselect");
				//stationSelectStep();
				model.setProperty("/screen/state","globalcodeselect");
			} else if (request.equals("contentselect")) {
				resetScreen();
				contentSelectStep();
			} else if (request.equals("mainapp")) {
				resetScreen();
				model.setProperty("/screen/state","mainapp");
				model.setProperty("@itemid", message.getProperty("itemid"));
				mainAppStep();
			}
		}
	}

	public void newGPSLocation(Screen s,JSONObject data) {
		//System.out.println("LOCATION="+data.toJSONString());
	}

	private void resetScreen() {
		screen.get("#languageselectionremote").remove();	
		screen.get("#languageselectionmupopremote").remove();
		screen.get("#stationselectionremote").remove();	
		screen.get("#stationcodeselectionremote").remove();
		screen.get("#globalcodeselectionremote").remove();
		screen.get("#coverflowremote").remove();
		screen.get("#selectionmapremote").remove();	
		screen.get("#quizremote").remove();	
		screen.get("#photoexplorerremote").remove();
		screen.get("#photoinfospotsremote").remove();
		screen.get("#photozoomremote").remove();
		screen.get("#interactivevideoremoteholder").remove();
		screen.get("#interactivevideoremote").remove();
	}
}
