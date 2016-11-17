/* 
* HelpRemoteController.java
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
package org.springfield.lou.controllers.help;

import org.json.simple.JSONObject;
import org.springfield.fs.FSList;
import org.springfield.fs.FsNode;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.model.ModelEvent;
import org.springfield.lou.screen.Screen;

/**
 * HelpRemoteController.java
 *
 * @author Pieter van Leeuwen
 * @copyright Copyright: Noterik B.V. 2016
 * @package org.springfield.lou.controllers.help
 * 
 */
public class HelpRemoteController extends Html5Controller {

    String originalcontroller;
    String deviceid;
    String userLanguage;
    
    public HelpRemoteController() { }
    
    public HelpRemoteController(String originalcontroller) {
	this.originalcontroller = originalcontroller;
    }
    
    public void attach(String sel) {
	selector = sel;
	
	deviceid = model.getProperty("@deviceid");
	userLanguage = model.getProperty("@userlanguage");

	FSList questions = model.getList("@help_topics");
	FsNode exhibition = model.getNode(model.getProperty("/screen/exhibitionpath"));
	
	JSONObject data = new JSONObject();
	
	if (questions != null) {   
	    data = FSList.ArrayToJSONObject(questions.getNodes(), userLanguage, "question,answer");
	}
	data.put("help_title", exhibition.getSmartProperty(userLanguage, "help_title"));
	
	screen.get(selector).render(data);
	screen.get("#previousfromhelp").on("click", "previousPage", this);
    }
    
    public void previousPage(Screen s, JSONObject data) {	
	FsNode node = new FsNode("previous", "requested");
	node.setProperty("deviceid", deviceid);
	node.setProperty("originalcontroller", originalcontroller);
	    
	model.notify("@photoinfospots/help/return", node);
    }
}
