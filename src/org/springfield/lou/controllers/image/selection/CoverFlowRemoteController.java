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
	
	public CoverFlowRemoteController() { }
	
	public void attach(String sel) {
		selector = sel;
		
		String path = model.getProperty("/screen/exhibitionpath");
		
		FsNode stationnode = model.getNode(path);
		if (stationnode != null) {
			JSONObject data = new JSONObject();
			data.put("title", stationnode.getProperty("title"));

			screen.get(selector).parsehtml(data);
			screen.get(selector).loadScript(this);
			
			screen.get("#trackpad").on("swipeleft",
					"swipeLeft", this);
			screen.get("#trackpad").on("swiperight",
					"swipeRight", this);
			screen.get("#trackpad").on("enter",
					"enter", this);
		}
	}
	
	public void swipeLeft(Screen s, JSONObject data) {
		model.notify("/screen/photoinfospots", new FsNode("coverflow", "left"));
	}
	
	public void swipeRight(Screen s, JSONObject data) {
		model.notify("/screen/photoinfospots", new FsNode("coverflow", "right"));
	}
	
	public void enter(Screen s, JSONObject data) {
		model.notify("/screen/photoinfospots", new FsNode("coverflow", "enter"));
	}
}
