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

import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONObject;
import org.springfield.fs.FSList;
import org.springfield.fs.FSListManager;
import org.springfield.fs.FsNode;
import org.springfield.fs.FsPropertySet;
import org.springfield.lou.application.Html5ApplicationInterface;
import org.springfield.lou.controllers.Html5Controller;
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

	String sharedspace;
	private Html5ApplicationInterface app;
	double lastx = -1;
	double lasty = -1;

	public ZoomAndAudioRemoteController() {
	}

	public void attach(String sel) {
		selector = sel;

		app = screen.getApplication();

		String path = model.getProperty("/screen/exhibitionpath");

		FsNode stationnode = model.getNode(path);
		if (stationnode != null) {
			JSONObject data = new JSONObject();

			screen.get(selector).parsehtml(data);
			
			screen.get("#trackpad").track("mousemove","mouseMove", this); // track mouse move event on the #trackpad
	 		//screen.get("#trackpad").on("mousemove","mouseMove", this); 
			screen.get("#trackpad").on("mouseup","mouseUp", this);
			screen.get("#trackpad").on("touchend","mouseUp", this);
			screen.get("#previousPage").on("mousedown", "previousPage", this);
			
		}

		sharedspace = model.getProperty("/screen/sharedspace");
		//System.out.println("About to notify about screen joining");
		model.notify("/screen/tst", new FsNode("join", "1"));
	}

	public void mouseUp(Screen s, JSONObject data) {
		//System.out.println("UP=" + data.toJSONString());

		FsPropertySet ps = new FsPropertySet(); // send them as a set so we get
												// 1 event
		ps.setProperty("x", "" + lastx); // we should support auto convert
		ps.setProperty("y", "" + lasty);
		ps.setProperty("action", "up");
		model.setProperties(sharedspace + "station/"+model.getProperty("@stationid"), ps);

		String url = getAudio(lastx, lasty); // get the audio (if any) for this
												// location
		
		if (url != null) { // if audio found lets push it to the screen (so it
							// plays)
			JSONObject d = new JSONObject();
			d.put("command", "update");
			d.put("src", url);
			screen.get("#zoomandaudioremote").update(d);
		}
	

	}

	public void mouseMove(Screen s, JSONObject data) {
		//lastx = getPercX(data);
		//lasty = getPercY(data);
		System.out.println("DATA="+data.toJSONString());
		//System.out.println("X="+lastx);
		lastx = (Double)data.get("screenXP");
		lasty = (Double)data.get("screenYP");

		
		FsPropertySet ps = new FsPropertySet(); // send them as a set so we get
												// 1 event
		ps.setProperty("x", "" + lastx); // we should support auto convert
		ps.setProperty("y", "" + lasty);
		ps.setProperty("action", "move");
		model.setProperties(sharedspace + "station/"+model.getProperty("@stationid"), ps);
		
		
		/*
		String url = getAudio(lastx, lasty); // get the audio (if any) for this
												// location
		if (url != null) { // if audio found lets push it to the screen (so it plays)
			JSONObject d = new JSONObject();
			d.put("command", "update");
			d.put("src", url);
			screen.get("#zoomandaudioremote").update(d);
		}
		*/
	}
	
	public void previousPage(Screen s, JSONObject data) {
		System.out.println("previous page requested");
	}


	private String getAudio(double x, double y) {
		FSList fslist = FSListManager.get("/domain/mecanex/app/sceneplayer/scene/blue/element/screen5/sounds",true);
		List<FsNode> nodes = fslist.getNodes();
		if (nodes != null) {
			for (Iterator<FsNode> iter = nodes.iterator(); iter.hasNext();) {
				FsNode node = (FsNode) iter.next();
				String url = node.getProperty("url");
				try {
					float ox = Float.parseFloat(node.getProperty("x"));
					float oy = Float.parseFloat(node.getProperty("y"));
					float ow = Float.parseFloat(node.getProperty("width")) / 2; //
					float oh = Float.parseFloat(node.getProperty("height")) / 2;
					if (x > (ox - ow) && x < (ox + ow)) { // within x range
						if (y > (oy - oh) && y < (oy + oh)) { // within y range
							//System.out.println("On hotspot");
							return url;
							//return "http://images1.noterik.com/mupop/audio/calligraphy.m4a";
						}
					}
				} catch (Exception e) {
					System.out.println("Error parsing audioimage data");
				}
			}
		}
		return null;
	}
}
