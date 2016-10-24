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

	public ZoomAndAudioRemoteController() {
	}

	public void attach(String sel) {
		selector = sel;

		app = screen.getApplication();
		String audiourl = null;

		String path = model.getProperty("/screen/exhibitionpath");

		FsNode stationnode = model.getNode(path);
		if (stationnode != null) {
			JSONObject data = new JSONObject();

			screen.get(selector).parsehtml(data);
			
			screen.get("#trackpad").track("mousemove","mouseMove", this); // track mouse move event on the #trackpad
	 		//screen.get("#trackpad").on("mousemove","mouseMove", this); 
			screen.get("#trackpad").on("mouseup","mouseUp", this);
			screen.get("#trackpad").on("touchend","mouseUp", this);
			screen.get("#previous").on("click", "previousPage", this);
			model.onNotify("/shared['mupop']/station/"+model.getProperty("@stationid"),"onStartAudio",this);
		}

	}

	public void mouseUp(Screen s, JSONObject data) {
		FsPropertySet ps = new FsPropertySet(); // send them as a set so we get 1 event
		ps.setProperty("x", "" + lastx); // we should support auto convert
		ps.setProperty("y", "" + lasty);
		ps.setProperty("action", "up");
		model.setProperties("/shared['mupop']/station/"+model.getProperty("@stationid"), ps);
	}
	
	public void onStartAudio(ModelEvent e) {
		FsNode message = e.getTargetFsNode();
		String action = message.getProperty("action");
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
		try {
			lastx = (Double)data.get("screenXP"); // hate this
		}  catch(Exception e) {
			Long t = (Long)data.get("screenXP");
			lastx = t.doubleValue();
		}
		try {
			lasty = (Double)data.get("screenYP");
		}  catch(Exception e) {
			Long t = (Long)data.get("screenYP");
			lasty = t.doubleValue();
		}

		
		FsPropertySet ps = new FsPropertySet(); // send them as a set so we get
												// 1 event
		ps.setProperty("x", "" + lastx); // we should support auto convert
		ps.setProperty("y", "" + lasty);
		ps.setProperty("action", "move");
		model.setProperties("/shared['mupop']/station/"+model.getProperty("@stationid"), ps);
	}
	
	public void previousPage(Screen s, JSONObject data) {		
		model.notify("/shared/photoinfospots/image/spotting", new FsNode("coverflow", "requested"));
	}

}
