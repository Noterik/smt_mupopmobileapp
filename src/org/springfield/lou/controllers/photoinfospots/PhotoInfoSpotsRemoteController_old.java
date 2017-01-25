/* 
* PhotoInfoSpotsRemoteController.java
* 
* Copyright (c) 2016 Noterik B.V.
* 
* This file is part of smt_mupopmobileapp, related to the Noterik Springfield project.
*
* smt_mupopmobileapp is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* smt_mupopmobileapp is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with smt_mupopmobileapp.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.springfield.lou.controllers.photoinfospots;

import org.springfield.fs.FSList;
import org.springfield.fs.FsNode;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.controllers.help.HelpRemoteController;
import org.springfield.lou.controllers.image.selection.CoverFlowRemoteController;
import org.springfield.lou.controllers.image.spotting.ZoomAndAudioRemoteController;
import org.springfield.lou.controllers.intro.audio.AudioTestRemoteController;
import org.springfield.lou.controllers.intro.language.LanguageSelectionRemoteController;
import org.springfield.lou.model.ModelEvent;

/**
 * PhotoInfoSpotsRemoteController.java
 *
 * @author Pieter van Leeuwen
 * @copyright Copyright: Noterik B.V. 2016
 * @package org.springfield.lou.controllers.photoinfospots
 * 
 */
public class PhotoInfoSpotsRemoteController_old extends Html5Controller {

    String state;
    String mainscreenState;
    String deviceid;
  
    public PhotoInfoSpotsRemoteController_old() {}
	
    public void attach(String sel) {
	selector = sel;
		
	String path = model.getProperty("/screen/exhibitionpath");
	deviceid = model.getProperty("@deviceid");
		
	FsNode stationnode = model.getNode(path);
	if (stationnode!=null) {
	    state = "audiotestremote";
	    screen.get("#photoinfospotsremote").append("div", "audiotestremote", new AudioTestRemoteController());
	    model.onNotify("@photoinfospots/intro/audiotest", "onStartClicked", this);
	    model.onNotify("@photoinfospots/image/spotting", "onCoverflowRequested", this);
	    model.onNotify("@photoinfospots/help/page", "onHelpRequested", this);
	    model.onNotify("@photoinfospots/help/return", "onHelpReturn", this);
	    model.onNotify("@photoinfospots/entryscreen/loaded", "onEntryScreenLoaded", this);
	}
    } 
	
    public void onEnterImage(ModelEvent e) {
	FsNode target = e.getTargetFsNode();
	
	model.setProperty("@imageid", target.getId());
	
	//reach all devices that had have zoomandaudio screen
	if (state.equals("coverflow")) {
	    screen.get("#coverflowremote").remove();
	    loadZoomAndAudio();
	}
    }
	
    public void onStartClicked(ModelEvent e) {
	FsNode target = e.getTargetFsNode();

	//only reach device that triggered this event
	if (!target.getProperty("deviceid").equals(deviceid)) {
	    return;
	}
		
	if (target.getId().equals("start")) {
	   screen.get("#audiotestremote").remove();
	    
	   //check state of mainscreen to show appropriate mobile screen
	   mainscreenState = model.getProperty("@photoinfospots/vars/state");
	   
	   model.onNotify("@photoinfospots/image/selected", "onEnterImage", this);
	   if (mainscreenState == null) {
	       //no main screen!
	   } else if (mainscreenState.equals("staticentryscreen") || mainscreenState.equals("imagerotationentryscreen")) {
	       FSList imagesList = model.getList("@images");
		    
		model.notify("@photoinfospots/device/connected", new FsNode("device", "1"));
		    
		if (imagesList.size() > 1) {
		    loadCoverflow();
		} else {
		    model.setProperty("@imageid", model.getProperty("@photoinfospots/vars/imageid"));
		    loadZoomAndAudio();
		}
	   } else if (mainscreenState.equals("coverflow")) {
		loadCoverflow();
	   } else if (mainscreenState.equals("zoomandaudio")) {
	       	model.setProperty("@imageid", model.getProperty("@photoinfospots/vars/imageid"));
		loadZoomAndAudio();
	   }
	} else if (target.getId().equals("languageselection")) {
	    screen.get("#"+target.getProperty("originalcontroller")).remove();    
	    screen.get("#photoinfospotsremote").append("div", "languageselectionremote", new LanguageSelectionRemoteController());
	} else if (target.getId().equals("audiotest")) {
	    screen.get("#"+target.getProperty("originalcontroller")).remove();
	    screen.get("#photoinfospotsremote").append("div", "audiotestremote", new AudioTestRemoteController());
	}
    }

    public void loadCoverflow() {	
	state = "coverflow";
	screen.get("#photoinfospotsremote").append("div", "coverflowremote", new CoverFlowRemoteController());
    }
    
    public void loadZoomAndAudio() {
	state = "zoomandaudio";
	screen.get("#photoinfospotsremote").append("div", "zoomandaudioremote", new ZoomAndAudioRemoteController());
    }
    
    public void onCoverflowRequested(ModelEvent e) {
	FsNode target = e.getTargetFsNode();
		
	if (target.getId().equals("requested")) {
	    //reach all devices that had have zoomandaudio screen
	    if (state.equals("zoomandaudio")) {
		screen.get("#zoomandaudioremote").remove();
	    	loadCoverflow();
	    }
	}
    }
    
    public void onHelpRequested(ModelEvent e) {
	FsNode target = e.getTargetFsNode();

	//only reach device that triggered this event
	if (!target.getProperty("deviceid").equals(deviceid)) {
	    return;
	}

	if (target.getId().equals("requested")) {
	    String originalController = target.getProperty("originalcontroller");
	    screen.get("#"+originalController).remove();

	    state = "help";
	    screen.get("#photoinfospotsremote").append("div", "helpremote", new HelpRemoteController(originalController));
	}
    }
    
    public void onHelpReturn(ModelEvent e) {
	FsNode target = e.getTargetFsNode();

	//only reach device that triggered this event
	if (!target.getProperty("deviceid").equals(deviceid)) {
	    return;
	}
	
	if (target.getId().equals("requested")) {
	    screen.get("#helpremote").remove();
	    
	    String originalController = target.getProperty("originalcontroller");
	    	    
	    //check if in meanwhile the mainscreen has switched
	    mainscreenState = model.getProperty("@photoinfospots/vars/state");
	    
	    if (originalController.equals("zoomandaudioremote") && mainscreenState.equals("coverflow")) {
		originalController = "coverflowremote";
	    }
	    if (originalController.equals("coverflowremote") && mainscreenState.equals("zoomandaudio")) {
		originalController = "zoomandaudioremote";
	    }

	    if (originalController.equals("zoomandaudioremote")) {
		 screen.get("#photoinfospotsremote").append("div", "zoomandaudioremote", new ZoomAndAudioRemoteController());
	    } else if (originalController.equals("audiotestremote")) {
		 screen.get("#photoinfospotsremote").append("div", "audiotestremote", new AudioTestRemoteController());
	    } else if (originalController.equals("coverflowremote")) {
		 screen.get("#photoinfospotsremote").append("div", "coverflowremote", new CoverFlowRemoteController());
	    }
	}
    }
    
    public void onEntryScreenLoaded(ModelEvent e) {
	FsNode target = e.getTargetFsNode();
	
	if (target.getId().equals("loaded")) {
	    if (state.equals("coverflow")) {
		screen.get("#coverflowremote").remove();
	    } else if (state.equals("zoomandaudio")) {
		screen.get("#zoomandaudioremote").remove();
	    } else {
		//not affected
		return;
	    }
	    
	    mainscreenState = model.getProperty("@photoinfospots/vars/state");
		 
	    if (mainscreenState.equals("staticentryscreen") || mainscreenState.equals("imagerotationentryscreen")) {
		screen.get("#photoinfospotsremote").append("div", "audiotestremote", new AudioTestRemoteController());
	    } else if (mainscreenState.equals("coverflow")) {
		screen.get("#photoinfospotsremote").append("div", "coverflowremote", new CoverFlowRemoteController());
	    } else if (mainscreenState.equals("zoomandaudio")) {
		screen.get("#photoinfospotsremote").append("div", "zoomandaudioremote", new ZoomAndAudioRemoteController());
	    }
	}
    }
}
