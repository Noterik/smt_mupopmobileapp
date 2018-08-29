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
package org.springfield.lou.controllers.image.selection;

import org.json.simple.JSONObject;
import org.springfield.fs.FsNode;
import org.springfield.fs.FsPropertySet;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.screen.Screen;
import org.springfield.lou.controllers.ExhibitionMemberManager;

/**
 * CoverFlowRemoteController.java
 *
 * @author Pieter van Leeuwen
 * @copyright Copyright: Noterik B.V. 2016
 * @package org.springfield.lou.controllers.image.selection
 * 
 */
public class SelectionMapRemoteController extends Html5Controller {


	double lastx = -1;
	double lasty = -1;
	private String mycolor = "#888888";
	String deviceid;
	String userLanguage;
	boolean voicecoverplayed = false;
	String username = null;

	public SelectionMapRemoteController() { }

	public void attach(String sel) {
		selector = sel;

		FsNode member = ExhibitionMemberManager.getMember(screen); // based on exhibitionid and browserid
		fillSelectionPage();
	}

	public void fillSelectionPage() {
		String userLanguage = model.getProperty("@userlanguage");
		String audiosrc = "";
		String transcript = "";
		FsNode stationnode = model.getNode("@station");
		if (stationnode != null) {
			JSONObject data = new JSONObject();
			data.put("title", stationnode.getSmartProperty("en", "title"));

			FsNode contentnode = model.getNode("@contentnode");
			audiosrc = contentnode.getProperty("voiceover");

			FsNode language_content = model.getNode("@language_photoexplore_coverflow_screen");
			if (language_content!=null) {
				data.put("helptext1", language_content.getSmartProperty(userLanguage, "swipe_help_text"));
				data.put("helptext2", language_content.getSmartProperty(userLanguage, "select_help_text"));
			}
			
			if (audiosrc != null) {
				data.put("audio", language_content.getSmartProperty(userLanguage, "coverflow_intro_audio"));
				data.put("transcript",  language_content.getSmartProperty(userLanguage, "coverflow_transcript"));
				data.put("transcript-text", contentnode.getProperty("transcript"));
			}

			screen.get(selector).render(data);
			screen.get("#trackpad").track("mousemove","mouseMove", this); // track mouse move event on the #trackpad
		}		
	}
	
	public void mouseMove(Screen s, JSONObject data) {
		double width = ((long) data.get("width")) * 1.0;
		double height = ((long) data.get("height")) * 1.0;

		double rx = data.get("screenX").toString().indexOf(".") > -1 ? (double) data.get("screenX") : ((long) data.get("screenX")) * 1.0;
		double ry = data.get("screenY").toString().indexOf(".") > -1 ? (double) data.get("screenY") : ((long) data.get("screenY")) * 1.0;

		lastx = (rx / width) * 100; 
		lasty = (ry / height) * 100;	

		FsNode msg = new FsNode("msg","1");
		msg.setProperty("x", "" + lastx); // we should support auto convert
		msg.setProperty("y", "" + lasty);
		msg.setProperty("color", mycolor);
		msg.setProperty("action", "spotmove");
		model.notify("@stationevents/fromclient",msg);

		//Update position on triggering screen
		screen.get("#pointer_icon").css("left",(rx)+"px");
		screen.get("#pointer_icon").css("top",(ry)+"px"); // comp back for the top shift
		screen.get("#pointer_icon").css("background-color", mycolor);
	}



	public void previousPage(Screen s, JSONObject data) {
		System.out.println("Station select requested by mobile");
		model.setProperty("/screen/state","globalcodeselect");
	}
}
