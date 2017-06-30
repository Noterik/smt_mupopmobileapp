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
package org.springfield.lou.controllers.intro.language;

import org.json.simple.JSONArray;
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
public class GlobalLanguageSelectionRemoteController extends Html5Controller {

    String deviceid;
    
    public GlobalLanguageSelectionRemoteController() { }
	
    public void attach(String sel) {
    	selector = sel;
    	System.out.println("GL1");
    	//String languages = model.getProperty("@exhibition/availablelanguages");
    	String languages = "en,nl";
    	String[] languageList = languages.split(",");
    	
    	JSONObject data = new JSONObject();
	JSONArray nodes = new JSONArray();
	data.put("nodes", nodes);
    	
    	for (String lang : languageList) {
    	    JSONObject node = new JSONObject();
    	    node.put("id",lang);
    	    node.put("language_name", model.getProperty("@language_language_screen/name", lang));
    	    nodes.add(node);
    	}
    	
    	String defaultLanguage = model.getProperty("@exhibition/language");
    	data.put("title", model.getProperty("@language_language_screen/title", defaultLanguage));
  
    	screen.get(selector).render(data);
    	screen.get(".language").on("click", "onLanguageSelected", this);
    }
	
    public void onLanguageSelected(Screen s,JSONObject data) {
    	screen.removeContent(selector.substring(1));
    	model.setProperty("@userlanguage",(String)data.get("id"));
		model.setProperty("/screen/state","globalcodeselect"); 
    }
    
    

}
