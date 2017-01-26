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
package org.springfield.lou.controllers.photoinfospots;

import java.awt.Color;
import java.util.Random;

import org.json.simple.JSONObject;
import org.springfield.fs.FSList;
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
public class PhotoInfoSpotsRemoteController extends Html5Controller {

	private Html5ApplicationInterface app;
	double lastx = -1;
	double lasty = -1;
	private String mycolor = "#888888";
	String deviceid;
	String userLanguage;

	public PhotoInfoSpotsRemoteController() { }

	public void attach(String sel) {
		selector = sel;

		app = screen.getApplication();

		String path = model.getProperty("/screen/exhibitionpath");
		deviceid = model.getProperty("@deviceid");
		userLanguage = model.getProperty("@userlanguage");

		FsNode stationnode = model.getNode(path+"/station/"+model.getProperty("@stationid"));
		FsNode imagenode = model.getNode("@image");

		if (stationnode != null) {
			JSONObject data = new JSONObject();

			String title = stationnode.getSmartProperty(userLanguage, "title");
			String helptext = stationnode.getSmartProperty(userLanguage, "hotspots_help_text");
			String transcript = stationnode.getSmartProperty(userLanguage, "hotspot_transcript");
			data.put("title", title);
			data.put("helptext", helptext);
			data.put("transcript", transcript);
			if (imagenode != null) {
				data.put("external-website", imagenode.getProperty("external-website"));
				data.put("audiourl", imagenode.getProperty("audiourl"));
				data.put("text", imagenode.getSmartProperty(userLanguage, "text"));
			}

			screen.get(selector).render(data);
			screen.get(selector).loadScript(this);

			mycolor = getColor();

			screen.get("#trackpad").track("mousemove","mouseMove", this); // track mouse move event on the #trackpad
			screen.get("#trackpad").on("mouseup","mouseUp", this);
			screen.get("#trackpad").on("touchend","mouseUp", this);
			screen.get("#photoinfospots-header").on("mouseup", "previousPage", this);
			screen.get("#zoomandaudiohelp").on("click", "helpPage", this);
			screen.get("#audiop").on("loaded", "loaded", this);
			screen.get("#pointer_icon").css("background-color","#"+mycolor);
			model.onNotify("@photoinfospots/spot/audio", "onStartAudio",this);

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
		ps.setProperty("language", userLanguage);
		ps.setProperty("action", "up");
		model.setProperties("@photoinfospots/spot/move", ps);
	}

	public void onStartAudio(ModelEvent e) {
		FsNode message = e.getTargetFsNode();

		//only reach device that triggered this event
		if (!message.getProperty("deviceid").equals(deviceid)) {
			return;
		}

		String action = message.getProperty("action");

		if (action.equals("startaudio")) {
			String url = message.getProperty("url");
			if (url != null) { // if audio found lets push it to the screen (so it plays)
				JSONObject d = new JSONObject();
				d.put("command", "update");
				String text = message.getProperty("text") == null ? "" : message.getProperty("text");
				d.put("text", text);
				screen.get(selector).update(d);
				
				JSONObject audiocmd = new JSONObject();
				audiocmd.put("action","playonnew");
				audiocmd.put("src",url);
				screen.get("#mobile").update(audiocmd);
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
		ps.setProperty("language", userLanguage);
		ps.setProperty("action", "move");
		model.setProperties("@photoinfospots/spot/move", ps);

		//Update position on triggering screen
		screen.get("#pointer_icon").css("left",(rx)+"px");
		screen.get("#pointer_icon").css("top",(ry)+"px"); // comp back for the top shift
		screen.get("#pointer_icon").css("background-color", mycolor);
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

	public void helpPage(Screen s, JSONObject data) {
		FsNode node = new FsNode("help", "requested");
		node.setProperty("deviceid", deviceid);
		node.setProperty("originalcontroller", "zoomandaudioremote");

		model.notify("@photoinfospots/help/page", node);
	}

	public void loaded(Screen s, JSONObject data) {
	    	//System.out.println("The following device has its audio loaded");
	    	//System.out.println(data.toJSONString());
	    	
		FsNode node = new FsNode("audio", "loaded");
		node.setProperty("deviceid", deviceid);

		model.notify("@photoinfospots/spotting/player", node);
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
		// Saturation between 0.6 and 0.8
		final float saturation = (random.nextInt(2000) + 6000) / 10000f;
		final float luminance = 0.9f;
		final Color color = Color.getHSBColor(hue, saturation, luminance);

		return "#"+Integer.toHexString(color.getRGB()).substring(2);
	}
}
