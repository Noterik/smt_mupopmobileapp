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
package org.springfield.lou.controllers.image.zoom;

import org.json.simple.JSONObject;
import org.springfield.fs.FSList;
import org.springfield.fs.FsNode;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.screen.Screen;

/**
 * PhotoExplorerRemoteController.java
 *
 * @author Pieter van Leeuwen
 * @copyright Copyright: Noterik B.V. 2017
 * @package org.springfield.lou.controllers.image.zoom
 * 
 */
public class PhotoExplorerRemoteController extends Html5Controller {
    
    public PhotoExplorerRemoteController() { }
    
    public void attach(String sel) {
	selector = sel;
	JSONObject data = new JSONObject();
	
	String userLanguage = model.getProperty("@userlanguage");
	String audiosrc = "";
	
	FsNode stationnode = model.getNode("@station");
	if (stationnode != null) {
	    FsNode item = model.getNode("@item");
	    System.out.println("ITEMNODE="+item.asXML());
	    
	    data.put("title", stationnode.getSmartProperty("en", "title"));
	    audiosrc = item.getProperty("voiceover");
	    
	    System.out.println("contentnode = "+item.asXML());
	    
	    FsNode language_content = model.getNode("@language_photoexplore_main_screen");
	    data.put("helptext1", language_content.getSmartProperty(userLanguage, "pinch_help_text"));
	    data.put("helptext2", language_content.getSmartProperty(userLanguage, "swipe_help_text"));
	    data.put("transcript",  language_content.getSmartProperty(userLanguage, "photoexplore_transcript"));
	    data.put("transcript-text", item.getProperty("transcript"));
	    
    	String type = model.getProperty("@station/contentselect");
    	System.out.println("TYPE CONTENT="+type);
    	if (type!=null && !type.equals("none")) {
    		data.put("previous","true");
    	}
	
	}

	screen.get(selector).render(data);
	screen.get(selector).loadScript(this);
	
	screen.get("#photoexplorer-trackpad").on("swipeleft","swipeLeft", this);
	screen.get("#photoexplorer-trackpad").on("swiperight","swipeRight", this);
	screen.get("#photoexplorer-trackpad").on("pinch","pinch", this);
	screen.get("#photoexplorer-header").on("mouseup", "previousPage", this);
	 
	JSONObject audiocmd = new JSONObject();
	audiocmd.put("action","play");
	audiocmd.put("src",audiosrc);
	System.out.println("PLAAAAAY AUDIO="+audiosrc);
	screen.get("#mobile").update(audiocmd);
	
	JSONObject d = new JSONObject();	
	d.put("command","init");
	screen.get(selector).update(d);
    }
    
    public void swipeLeft(Screen s, JSONObject data) {
	FsNode message = new FsNode("message",screen.getId());
    	message.setProperty("action","leftonzoom");
	model.notify("@stationevents/fromclient",message);
    }
    
    public void swipeRight(Screen s, JSONObject data) {
	FsNode message = new FsNode("message",screen.getId());
    	message.setProperty("action","rightonzoom");
	model.notify("@stationevents/fromclient",message);
    }
    
    public void pinch(Screen s, JSONObject data) {
	FsNode message = new FsNode("message",screen.getId());
    	
    	JSONObject origin = (JSONObject) data.get("origin");
    	String  x = Long.toString((long) origin.get("x"));
    	String  y = Long.toString((long) origin.get("y"));
    	String value;
    	
    	try {
    	    value = Double.toString((double) data.get("value"));
    	} catch (ClassCastException e) {
    	    value = Long.toString((long) data.get("value"));
    	}
    	
    	message.setProperty("action","scale");
    	message.setProperty("originX", x);
    	message.setProperty("originY", y);
    	message.setProperty("value", value);
	model.notify("@stationevents/fromclient",message);
    }
    
    public void previousPage(Screen s, JSONObject data) {	
	System.out.println("Previous page requested");

	FsNode message = new FsNode("message",screen.getId());
	message.setProperty("action","");
	message.setProperty("request","contentselectforce");
	model.notify("@stationevents/fromclient",message);
	
	FsNode m = new FsNode("message",screen.getId());
	m.setProperty("request","contentselect");
	model.notify("@stationevents/fromclient",m);
    }
}
