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

    String deviceid;
    
    public AudioTestRemoteController() { }
	
    public void attach(String sel) {
	selector = sel;
		
	String path = model.getProperty("/screen/exhibitionpath");
	
	deviceid = model.getProperty("@deviceid");	
	String userLanguage = model.getProperty("@userlanguage");
		
	FsNode stationnode = model.getNode(path+"/station/"+model.getProperty("@stationid"));	
	
	if (stationnode != null) {
	    JSONObject data = new JSONObject();
	    data.put("audio_test_intro_text", stationnode.getSmartProperty(userLanguage, "audio_test_intro_text"));
	    data.put("start", stationnode.getSmartProperty(userLanguage, "start"));
	    data.put("headphone", stationnode.getSmartProperty(userLanguage, "headphone"));
	    data.put("audiosrc", stationnode.getSmartProperty(userLanguage, "audio_test_intro_audio"));
	    
	    screen.get(selector).render(data);
	    screen.get(selector).loadScript(this);
	    
	    screen.get("#start").on("click", "onStartClicked", this);	
	    screen.get("#audiotest_help").on("click", "helpPage", this);
	    screen.get("#audiotest_previous").on("click", "previousPage", this);
	    
	    JSONObject d = new JSONObject();	
	    d.put("command","init");
	    screen.get(selector).update(d);
	}
    }
	
    public void onStartClicked(Screen s,JSONObject data) {
	FsNode node = new FsNode("ready", "start");
	node.setProperty("deviceid", deviceid);
	
	model.notify("/shared/photoinfospots/intro/audiotest", node);
    }
    
    public void helpPage(Screen s, JSONObject data) {
	FsNode node = new FsNode("help", "requested");
	node.setProperty("deviceid", deviceid);
	node.setProperty("originalcontroller", "audiotestremote");
	    
	model.notify("/shared/photoinfospots/help/page", node);
    }
    
    public void previousPage(Screen s, JSONObject data) {
	FsNode node = new FsNode("languagepage", "requested");
	node.setProperty("deviceid", deviceid);
	node.setProperty("originalcontroller", "audiotestremote");
	model.notify("/shared/exhibition/intro/languagepage", node);
    }
}
