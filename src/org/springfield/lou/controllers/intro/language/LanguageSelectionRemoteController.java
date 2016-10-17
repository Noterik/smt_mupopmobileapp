/* 
* LanguageSelectionRemoteController.java
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
package org.springfield.lou.controllers.intro.language;

import java.util.ArrayList;
import java.util.Collection;

import org.json.simple.JSONObject;
import org.springfield.fs.FsNode;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.screen.Screen;

/**
 * LanguageSelectionRemoteController.java
 *
 * @author Pieter van Leeuwen
 * @copyright Copyright: Noterik B.V. 2016
 * @package org.springfield.lou.controllers.intro.language
 * 
 */
public class LanguageSelectionRemoteController extends Html5Controller {

	public LanguageSelectionRemoteController() { }
	
	public void attach(String sel) {
		selector = sel;
		
		String path = model.getProperty("/screen/exhibitionpath");
		
		FsNode stationnode = model.getNode(path);
		if (stationnode != null) {
			JSONObject data = new JSONObject();
			
			//TODO: get list of available languages
			Collection<JSONObject> languages = new ArrayList<JSONObject>();
			JSONObject nl = new JSONObject();
			JSONObject en = new JSONObject();
			JSONObject fr = new JSONObject();
			
			nl.put("name", "Nederlands");
			nl.put("value", "nl");
			en.put("name", "English");
			en.put("value", "en");
			fr.put("name", "Français");
			fr.put("value", "fr");
			
			languages.add(nl);
			languages.add(en);
			languages.add(fr);

			data.put("languages", languages);
			screen.get(selector).parsehtml(data);
			
			screen.get(".language").on("click", "onLanguageSelected", this);
		}
	}
	
	 public void onLanguageSelected(Screen s,JSONObject data) {
		 System.out.println("The following language was selected "+ data.get("id"));
		 //TODO: store language for user session
		 model.setProperty("@language",(String)data.get("id"));
		 
		 model.notify("/screen/photoinfospots/intro/languageselection", new FsNode("language", "selected"));
		 
	 }
}
