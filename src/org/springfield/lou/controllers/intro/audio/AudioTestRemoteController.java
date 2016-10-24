/* 
* AudioTestRemoteController.java
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
package org.springfield.lou.controllers.intro.audio;

import org.json.simple.JSONObject;
import org.springfield.fs.FsNode;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.screen.Screen;

/**
 * AudioTestRemoteController.java
 *
 * @author Pieter van Leeuwen
 * @copyright Copyright: Noterik B.V. 2016
 * @package org.springfield.lou.controllers.intro.language
 * 
 */
public class AudioTestRemoteController extends Html5Controller {

	public AudioTestRemoteController() { }
	
	public void attach(String sel) {
		selector = sel;
		
		String path = model.getProperty("/screen/exhibitionpath");
		
		String language = model.getProperty("@language");
		System.out.println("The following lanugage is set "+language);
		
		FsNode stationnode = model.getNode(path);
		if (stationnode != null) {
			JSONObject data = new JSONObject();
			
			screen.get(selector).parsehtml(data);
			
			screen.get("#start").on("click", "onStartClicked", this);
			screen.get("#page1").on("click", "onPreviousPageRequested", this);
		}
	}
	
	public void onStartClicked(Screen s,JSONObject data) {
		 model.notify("/shared/photoinfospots/intro/audiotest", new FsNode("ready", "start"));
	}
	
	public void onPreviousPageRequested(Screen s, JSONObject data) {
	    model.notify("/shared/photoinfospots/intro/audiotest", new FsNode("return", "languageselection"));
	}
}
