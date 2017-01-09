/* 
* PhotoExplorerRemoteController.java
* 
* Copyright (c) 2017 Noterik B.V.
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
package org.springfield.lou.controllers.image.zoom;

import org.json.simple.JSONObject;
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
	
	String path = model.getProperty("/screen/exhibitionpath");
	
	FsNode stationnode = model.getNode(path+"/station/"+model.getProperty("@stationid"));
	if (stationnode != null) {
	    data.put("title", stationnode.getSmartProperty("en", "title"));
	}

	screen.get(selector).render(data);
	screen.get(selector).loadScript(this);
	
	screen.get("#photoexplorer-trackpad").on("swipeleft","swipeLeft", this);
	screen.get("#photoexplorer-trackpad").on("swiperight","swipeRight", this);
	screen.get("#photoexplorer-trackpad").on("pinch","pinch", this);
	 
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
}
