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

import org.json.simple.JSONObject;
import org.springfield.fs.FSList;
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
public class LanguageSelectionRemoteControllerMupop extends Html5Controller {

    String deviceid;
    
    public LanguageSelectionRemoteControllerMupop() { }
	
    public void attach(String sel) {
		selector = sel;
		System.out.print("LANGUAGE SELECTION!!!!");
		String path = model.getProperty("/screen/exhibitionpath");
		deviceid = model.getProperty("@deviceid");
			
		FsNode stationnode = model.getNode(path);
		if (stationnode != null) {
		    //get languages from the languages
		    FSList languageList = model.getList("@languages");
		    
		    JSONObject languages = FSList.ArrayToJSONObject(languageList.getNodes(),"en","language_name,flag_url");
		    System.out.println(languages.toString());
		    System.out.print("LANGUAGE SELECTION DONE!!!!");
		    screen.get(selector).render(languages);
				
		    screen.get(".language").on("click", "onLanguageSelected", this);
		}
    }
	
    public void onLanguageSelected(Screen s,JSONObject data) {
    	model.setProperty("@userlanguage",(String)data.get("id"));
		
		FsNode languageNode = new FsNode("language", "selected");
		languageNode.setProperty("deviceid", deviceid);
		model.notify("/shared/exhibition/intro/languageselection", languageNode);	 
    }
}
