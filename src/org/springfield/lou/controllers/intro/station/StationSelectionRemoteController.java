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
package org.springfield.lou.controllers.intro.station;

import org.json.simple.JSONObject;
import org.springfield.fs.FSList;
import org.springfield.fs.FsNode;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.screen.Screen;

/**
 * StationSelectionRemoteController.java
 *
 * @author Pieter van Leeuwen
 * @copyright Copyright: Noterik B.V. 2016
 * @package org.springfield.lou.controllers.intro.station
 * 
 */
public class StationSelectionRemoteController extends Html5Controller {

    String deviceid;
    String userLanguage;
    
    public StationSelectionRemoteController() { }
    
    public void attach(String sel) {
    	selector = sel;
	
    	deviceid = model.getProperty("@deviceid");
    	String userLanguage = model.getProperty("@userlanguage");

    	FSList stations = model.getList("@stations");
    	FsNode exhibition = model.getNode("@exhibition");
    	if (stations != null) {   
    		JSONObject data = FSList.ArrayToJSONObject(stations.getNodes(), userLanguage, "labelid,name");
    		data.put("select_station", exhibition.getSmartProperty(userLanguage, "select_station"));
    		screen.get(selector).render(data);
    		screen.get(".station").on("click", "onStationSelected", this);
    		//screen.get(".earlierpage").on("click", "onEarlierPageClicked", this);
    	}
    }
    
    public void onStationSelected(Screen s, JSONObject data) {
		String stationid = (String) data.get("id");
    	System.out.println("On station selected "+stationid);
    	screen.removeContent(selector.substring(1));
    	model.setProperty("@stationid",stationid);
    	FsNode message = new FsNode("message",screen.getId());
    	message.setProperty("request","station");
    	message.setProperty("@stationid", stationid);
		model.notify("@stationevents/fromclient",message);
    }
    
    public void onEarlierPageClicked(Screen s, JSONObject data) {
    /*
		System.out.println(data.toJSONString());
	
		FsNode node = new FsNode("languagepage", "requested");
		node.setProperty("deviceid", deviceid);
		node.setProperty("originalcontroller", "stationselectionremote");
		model.notify("/shared/exhibition/intro/languagepage", node);
	*/
    }
}
