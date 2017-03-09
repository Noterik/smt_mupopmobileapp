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
	    JSONObject data = new JSONObject();
	    String userLanguage = model.getProperty("@userlanguage");

	    data.put("title", model.getProperty("@language_audio_test_screen/title", userLanguage));
	    data.put("explanation", model.getProperty("@language_audio_test_screen/explanation", userLanguage));
	    data.put("start", model.getProperty("@language_audio_test_screen/start", userLanguage));
	    data.put("audiosrc", model.getProperty("@language_audio_test_screen/audio", userLanguage));
	    
	    screen.get(selector).render(data);
	    screen.get(selector).loadScript(this);

	    screen.get("#start").on("click", "onStartClicked", this);	
	    //screen.get("#audiotest_help").on("click", "helpPage", this);
	    //screen.get("#audiotest_previous").on("click", "previousPage", this);

	    JSONObject d = new JSONObject();	
	    d.put("command","init");
	    screen.get(selector).update(d);
	}

	public void onStartClicked(Screen s,JSONObject data) {
		System.out.println("START CLICKED");
	    screen.get(selector).remove();
	    screen.get("#mobile").update(new JSONObject());
	    FsNode message = new FsNode("message",screen.getId());
	    message.setProperty("request","join");
	    model.notify("@exhibitionevents/fromclient",message);
	}

	public void helpPage(Screen s, JSONObject data) {
		//FsNode node = new FsNode("help", "requested");
		//node.setProperty("deviceid", deviceid);
	//	node.setProperty("originalcontroller", "audiotestremote");

	//	model.notify("@photoinfospots/help/page", node);
	}

	public void previousPage(Screen s, JSONObject data) {
		//FsNode node = new FsNode("goto", "languageselection");
		//node.setProperty("deviceid", deviceid);
		//node.setProperty("originalcontroller", "audiotestremote");
		//model.notify("@photoinfospots/intro/audiotest", node);
	}
}
