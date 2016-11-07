/* 
 * ZoomAndAudioRemoteController.java
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
package org.springfield.lou.controllers.image.spotting;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.json.simple.JSONObject;
import org.springfield.fs.FSList;
import org.springfield.fs.FSListManager;
import org.springfield.fs.FsNode;
import org.springfield.fs.FsPropertySet;
import org.springfield.lou.application.Html5ApplicationInterface;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.model.ModelEvent;
import org.springfield.lou.screen.Screen;

/**
 * ZoomAndAudioRemoteController.java
 * 
 * @author Pieter van Leeuwen
 * @copyright Copyright: Noterik B.V. 2016
 * @package org.springfield.lou.controllers.image.spotting
 * 
 */
public class ZoomAndAudioRemoteController extends Html5Controller {

	private Html5ApplicationInterface app;
	double lastx = -1;
	double lasty = -1;
	private String mycolor = "#888888";
	String deviceid;

	public ZoomAndAudioRemoteController() {
	}

	public void attach(String sel) {
	    selector = sel;

	    app = screen.getApplication();
	    String audiourl = null;

	    String path = model.getProperty("/screen/exhibitionpath");
	    deviceid = model.getProperty("@deviceid");
		
	    FsNode stationnode = model.getNode(path);

	    if (stationnode != null) {
		JSONObject data = new JSONObject();
		String waitscreenmode = model.getProperty("@station/waitscreenmode");
		
		if (waitscreenmode != null && !waitscreenmode.equals("off")) {
		    data.put("previous", "true");
		}
		
		screen.get(selector).render(data);
		screen.get(selector).loadScript(this);
			
		mycolor = getColor();

		screen.get("#trackpad").track("mousemove","mouseMove", this); // track mouse move event on the #trackpad
		screen.get("#trackpad").on("mouseup","mouseUp", this);
		screen.get("#trackpad").on("touchend","mouseUp", this);
		screen.get("#previous").on("click", "previousPage", this);
		screen.get("#pointer_icon").css("background-color","#"+mycolor);
		model.onNotify("/shared['mupop']/station/"+model.getProperty("@stationid"),"onStartAudio",this);
		
		JSONObject d = new JSONObject();	
		d.put("command","init");
		screen.get(selector).update(d);
	    }
	}

	public void mouseUp(Screen s, JSONObject data) {
		FsPropertySet ps = new FsPropertySet(); // send them as a set so we get 1 event
		ps.setProperty("x", "" + lastx); // we should support auto convert
		ps.setProperty("y", "" + lasty);
		ps.setProperty("deviceid", deviceid);
		ps.setProperty("action", "up");
		model.setProperties("/shared['mupop']/station/"+model.getProperty("@stationid"), ps);
	}
	
	public void onStartAudio(ModelEvent e) {
	    FsNode message = e.getTargetFsNode();
	   
	    //only reach device that triggered this event
	    if (!message.getProperty("deviceid").equals(deviceid)) {
		return;
	    }
	    
	    String action = message.getProperty("action");
	    
	    System.out.println("Start audio requested");
	    
	    if (action.equals("startaudio")) {
		String url = message.getProperty("url");
		if (url != null) { // if audio found lets push it to the screen (so it plays)
		    System.out.println("START AUDIO REQUESTED="+url);
		    JSONObject d = new JSONObject();
		    d.put("command", "update");
		    d.put("src", url);
		    screen.get("#zoomandaudioremote").update(d);
		}
	    }		
	}

	public void mouseMove(Screen s, JSONObject data) {
	    	double width = ((long) data.get("width")) * 1.0;
	    	double height = ((long) data.get("height")) * 1.0;
	    	
	    	double rx = data.get("screenX").toString().indexOf(".") > -1 ? (double) data.get("screenX") : ((long) data.get("screenX")) * 1.0;
	    	double ry = data.get("screenY").toString().indexOf(".") > -1 ? (double) data.get("screenY") : ((long) data.get("screenY")) * 1.0;
	    	
	    	lastx = (rx / width) * 100; 
	    	lasty = (ry / height) * 100;	
	    	
	    	FsPropertySet ps = new FsPropertySet(); // send them as a set so we get 1 event
	    	ps.setProperty("x", "" + lastx); // we should support auto convert
		ps.setProperty("y", "" + lasty);
		ps.setProperty("color", mycolor);
		ps.setProperty("deviceid", deviceid);
		ps.setProperty("action", "move");
		model.setProperties("/shared['mupop']/station/"+model.getProperty("@stationid"), ps);
		
		//Update position on triggering screen
		screen.get("#pointer_icon").css("left",(rx)+"px");
		screen.get("#pointer_icon").css("top",(ry)+"px"); // comp back for the top shift
		screen.get("#pointer_icon").css("background-color", mycolor);
	}
	
	public void previousPage(Screen s, JSONObject data) {
	    FsNode node = new FsNode("coverflow", "requested");
	    node.setProperty("deviceid", deviceid);
	    
	    model.notify("/shared/photoinfospots/image/spotting", node);
	}
	
	private String getColor() {     
	    String color;
	    String colorProperty = model.getProperty("@color");
	    
	    if (colorProperty != null) {
		color = colorProperty;
	    } else {		
		color = generateColor();
		model.setProperty("@color", color);
	    }	    
	    return color;
	}
	
	private String generateColor() {	    
	    //to get rainbow, pastel colors
	    Random random = new Random();
	    final float hue = random.nextFloat();
	    // Saturation between 0.1 and 0.3
	    final float saturation = (random.nextInt(2000) + 1000) / 10000f;
	    final float luminance = 0.9f;
	    final Color color = Color.getHSBColor(hue, saturation, luminance);
	    
	    return "#"+Integer.toHexString(color.getRGB()).substring(2);
	}
}
