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
import org.springfield.fs.FSList;
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
	
	FsNode stationnode = model.getNode(path+"/station/"+model.getProperty("@stationid"));
	if (stationnode != null) {
	    JSONObject data = new JSONObject();
	    data.put("title", stationnode.getSmartProperty(userLanguage, "title"));
	    data.put("helptext1", stationnode.getSmartProperty(userLanguage, "swipe_help_text"));
	    data.put("helptext2", stationnode.getSmartProperty(userLanguage, "select_help_text"));
	    data.put("audio", stationnode.getSmartProperty(userLanguage, "coverflow_intro_audio"));
	    data.put("transcript",  stationnode.getSmartProperty(userLanguage, "coverflow_transcript"));
	    data.put("transcript-text", stationnode.getSmartProperty(userLanguage, "coverflow_transcript_text"));
	    
	    screen.get(selector).render(data);
	    screen.get(selector).loadScript(this);
			
	    screen.get("#trackpad").on("swipeleft",
					"swipeLeft", this);
	    screen.get("#trackpad").on("swiperight",
					"swipeRight", this);
	    screen.get("#trackpad").on("enter",
					"enter", this);
	    screen.get("#coverflow-help").on("click", "helpClicked", this);
	    screen.get("#coverflow-play").on("click", "playClicked", this);
	    screen.get("#coverflow-text").on("click", "textClicked", this);
	    screen.get("#coverflow_previous").on("click", "previousPage", this);
			
	    JSONObject d = new JSONObject();	
	    d.put("command","init");
	    screen.get(selector).update(d);
	}
    }
	
    public void swipeLeft(Screen s, JSONObject data) {
	FsNode node = new FsNode("coverflow", "left");
	node.setProperty("deviceid", deviceid);
	
	model.notify("@photoinfospots", node);
    }
	
    public void swipeRight(Screen s, JSONObject data) {
	FsNode node = new FsNode("coverflow", "right");
	node.setProperty("deviceid", deviceid);
	
	model.notify("@photoinfospots", node);
    }
	
    public void enter(Screen s, JSONObject data) {
	FsNode node = new FsNode("coverflow", "enter");
	
	model.notify("@photoinfospots", node);
    }
	
    public void helpClicked(Screen s, JSONObject data) { 
	FsNode node = new FsNode("help", "requested");
	node.setProperty("deviceid", deviceid);
	node.setProperty("originalcontroller", "coverflowremote");
	    
	model.notify("@photoinfospots/help/page", node);
    }
	
    public void playClicked(Screen s, JSONObject data) {
	System.out.println("Play clicked");
    }
	
    public void textClicked(Screen s, JSONObject data) {
	System.out.println("Text clicked");
    }
    
    public void previousPage(Screen s, JSONObject data) {	
	FsNode node = new FsNode("goto", "audiotest");
	node.setProperty("deviceid", deviceid);
	node.setProperty("originalcontroller", "coverflowremote");
	    
	model.notify("@photoinfospots/intro/audiotest", node);
    }
}
