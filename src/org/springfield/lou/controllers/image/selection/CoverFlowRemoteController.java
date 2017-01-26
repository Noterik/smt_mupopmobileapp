/* 
* CoverFlowRemoteController.java
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
package org.springfield.lou.controllers.image.selection;

import org.json.simple.JSONObject;
import org.springfield.fs.FsNode;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.screen.Screen;

/**
 * CoverFlowRemoteController.java
 *
 * @author Pieter van Leeuwen
 * @copyright Copyright: Noterik B.V. 2016
 * @package org.springfield.lou.controllers.image.selection
 * 
 */
public class CoverFlowRemoteController extends Html5Controller {

    String deviceid;
    
    public CoverFlowRemoteController() { }
	
    public void attach(String sel) {
	selector = sel;
		
	String path = model.getProperty("/screen/exhibitionpath");
	
	deviceid = model.getProperty("@deviceid");	
	String userLanguage = model.getProperty("@userlanguage");
	String audiosrc = "";
	String transcript = "";
	
	FsNode stationnode = model.getNode(path+"/station/"+model.getProperty("@stationid"));
	if (stationnode != null) {
	    JSONObject data = new JSONObject();
	    data.put("title", stationnode.getSmartProperty("en", "title"));
	    
	    FsNode contentnode = model.getNode("@contentnode");
	    audiosrc = contentnode.getProperty("voiceover");
	    
	    FsNode language_content = model.getNode("@language_photoexplore_coverflow_screen");
    	    data.put("helptext1", language_content.getSmartProperty(userLanguage, "swipe_help_text"));
    	    data.put("helptext2", language_content.getSmartProperty(userLanguage, "select_help_text"));
	    
	    if (audiosrc != null) {
		data.put("audio", language_content.getSmartProperty(userLanguage, "coverflow_intro_audio"));
		data.put("transcript",  language_content.getSmartProperty(userLanguage, "coverflow_transcript"));
		data.put("transcript-text", contentnode.getProperty("transcript"));
	    }
	    
	    screen.get(selector).render(data);
	    screen.get(selector).loadScript(this);
			
	    screen.get("#coverflow-trackpad").on("swipeleft","swipeLeft", this);
	    screen.get("#coverflow-trackpad").on("swiperight","swipeRight", this);
	    screen.get("#coverflow-trackpad").on("enter","enter", this);
	    screen.get("#coverflow-help").on("click", "helpClicked", this);
	    screen.get("#coverflow-header").on("mouseup", "previousPage", this);
	    
	    JSONObject audiocmd = new JSONObject();
	    
	    String played = model.getProperty("@coverflowplayed");
	    
	    if (audiosrc != null) {
        	    //only play on first time in coverflow, otherwise getting very annoying!
        	    if (played == null) {
        		audiocmd.put("action","play");
        		model.setProperty("@coverflowplayed", "true");
        	    } else {
        		audiocmd.put("action","load");
        	    }
        	    audiocmd.put("src",audiosrc);
        	    screen.get("#mobile").update(audiocmd);
	    }
	    
	    JSONObject d = new JSONObject();	
	    d.put("command","init");
	     screen.get(selector).update(d);
	}
    }
	
    public void swipeLeft(Screen s, JSONObject data) {
    	FsNode message = new FsNode("message",screen.getId());
    	message.setProperty("action","left");
	System.out.println("Swipe left received "+message.asXML());
    	model.notify("@stationevents/fromclient",message);
    }
	
    public void swipeRight(Screen s, JSONObject data) {
    	FsNode message = new FsNode("message",screen.getId());
    	message.setProperty("action","right");
    	System.out.println("Swipe left received "+message.asXML());
	model.notify("@stationevents/fromclient",message);
    }
	
    public void enter(Screen s, JSONObject data) {
    	FsNode message = new FsNode("message",screen.getId());
    	message.setProperty("action","enter");
    	System.out.println("Swipe left received "+message.asXML());
	model.notify("@stationevents/fromclient",message);
    }
	
    public void helpClicked(Screen s, JSONObject data) { 
	FsNode node = new FsNode("help", "requested");
	node.setProperty("deviceid", deviceid);
	node.setProperty("originalcontroller", "coverflowremote");
	    
	model.notify("@photoinfospots/help/page", node);
    }
    
    public void previousPage(Screen s, JSONObject data) {
	System.out.println("Station select requested by mobile");
	model.setProperty("/screen/state","stationselect");
    }
}
