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

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springfield.fs.FSList;
import org.springfield.fs.FsNode;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.model.ModelEvent;
import org.springfield.lou.screen.Screen;

/**
 * StationSelectionRemoteController.java
 *
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2016
 * @package org.springfield.lou.controllers.intro.station
 * 
 */
public class GlobalNameSelectRemoteController extends Html5Controller {

	int timeout_toselect = 5;
	
	public GlobalNameSelectRemoteController() { }
	
	

	public void attach(String sel) {
		selector = sel;
		fillPage();
		model.onNotify("/shared[timers]/1second","onTimeoutChecks",this); 
		System.out.println("ADDING NAME SELECT CONTROLLER !!!!");
	}

	private  void fillPage() {
		JSONObject data = new JSONObject();

		screen.get(selector).render(data);


	}
	
	public void onTimeoutChecks(ModelEvent e) {
		System.out.println("TIMER!!!!");
		// time to move we give the user, 5 seconds to start picking if not we pick
		if (timeout_toselect!=0) {
			timeout_toselect=timeout_toselect-1;
			if  (timeout_toselect==0) {
				System.out.println("JUMP TO THE NEXT PHASE !!!!!");
				FsNode message = new FsNode("message",screen.getId());
				message.setProperty("request","station");
				message.setProperty("@stationid",model.getProperty("@stationid"));
				System.out.println("STATION MSG="+message.asXML());
				model.notify("@stationevents/fromclient",message);
			}
		}
	}
	

}
