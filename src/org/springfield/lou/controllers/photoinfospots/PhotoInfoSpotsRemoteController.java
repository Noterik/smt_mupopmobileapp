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
public class PhotoInfoSpotsRemoteController extends Html5Controller {

    String deviceid;
    
    public PhotoInfoSpotsRemoteController() {}
	
    public void attach(String sel) {
	selector = sel;
		
	String path = model.getProperty("/screen/exhibitionpath");
	deviceid = model.getProperty("@deviceid");
		
	FsNode stationnode = model.getNode(path);
	if (stationnode!=null) {
			
	    System.out.println("MODE="+model.getProperty("@station/waitscreenmode"));
	    
	    screen.get("#photoinfospotsremote").append("div", "audiotestremote", new AudioTestRemoteController());
	    model.onNotify("/shared/photoinfospots/intro/audiotest", "onStartClicked", this);
	    model.onNotify("/shared/photoinfospots/image/spotting", "onCoverflowRequested", this);
	    model.onNotify("/shared/photoinfospots/help/page", "onHelpRequested", this);
	    model.onNotify("/shared/photoinfospots/help/return", "onHelpReturn", this);
	}
    } 
	
    public void onEnterImage(ModelEvent e) {
	FsNode target = e.getTargetFsNode();

	if (target.getId().equals("enter")) {		
	    screen.get("#coverflowremote").remove();		
	    screen.get("#photoinfospotsremote").append("div", "zoomandaudioremote", new ZoomAndAudioRemoteController());
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
	
	    FSList imagesList = model.getList("@images");
	    
	    model.notify("/shared/photoinfospots/device/connected", new FsNode("device", "1"));
	    
	    if (imagesList.size() > 1) {
    	    	model.onNotify("/shared/photoinfospots", "onEnterImage", this);

    	    	screen.get("#photoinfospotsremote").append("div", "coverflowremote", new CoverFlowRemoteController());
    	    } else {
    		screen.get("#photoinfospotsremote").append("div", "zoomandaudioremote", new ZoomAndAudioRemoteController());
    	    }
	} else if (target.getId().equals("languageselection")) {
	    screen.get("#audiotestremote").remove();    
	    screen.get("#photoinfospotsremote").append("div", "languageselectionremote", new LanguageSelectionRemoteController());
	}
    }
	
    public void onCoverflowRequested(ModelEvent e) {
	FsNode target = e.getTargetFsNode();
		
	//only reach device that triggered this event
	if (!target.getProperty("deviceid").equals(deviceid)) {
	    return;
	}
		
	if (target.getId().equals("requested")) {
	    screen.get("#zoomandaudioremote").remove();	
	    screen.get("#photoinfospotsremote").append("div", "coverflowremote", new CoverFlowRemoteController());
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
	    String originalController = target.getProperty("originalcontroller");
	    
	    screen.get("#helpremote").remove();
	    
	    if (originalController.equals("zoomandaudioremote")) {
		 screen.get("#photoinfospotsremote").append("div", "zoomandaudioremote", new ZoomAndAudioRemoteController());
	    } else if (originalController.equals("audiotestremote")) {
		 screen.get("#photoinfospotsremote").append("div", "audiotestremote", new AudioTestRemoteController());
	    }
	}
    }
}
