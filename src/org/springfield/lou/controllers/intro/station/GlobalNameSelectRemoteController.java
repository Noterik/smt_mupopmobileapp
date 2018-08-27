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
import org.springfield.lou.controllers.ExhibitionMemberManager;

/**
 * StationSelectionRemoteController.java
 *
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2016
 * @package org.springfield.lou.controllers.intro.station
 * 
 */
public class GlobalNameSelectRemoteController extends Html5Controller {

	int timeout_toselect = 500;
	String name1,name2,name3;
	
	public GlobalNameSelectRemoteController() { 
		
	}
	
	

	public void attach(String sel) {
		selector = sel;
		fillPage();
		model.onNotify("/shared[timers]/1second","onTimeoutChecks",this); 
		System.out.println("ADDING NAME SELECT CONTROLLER !!!!");
	}

	private  void fillPage() {
		FsNode member= ExhibitionMemberManager.getMember(screen);
		System.out.println("MEMBER NAME="+member);
		if (member==null) {
			JSONObject data = new JSONObject();
			name1 = ExhibitionMemberManager.getNextFreeName(screen);
			name2 = ExhibitionMemberManager.getNextFreeName(screen);
			name3 = ExhibitionMemberManager.getNextFreeName(screen);
			data.put("name1",name1);
			data.put("name2",name2);
			data.put("name3",name3);
			data.put("name4","custom");
			screen.get(selector).render(data);
			System.out.println("ONE="+name1+" TWO="+name2+" THREE="+name3);
			screen.get(".flag").on("click", "onFlagSelected", this);
			screen.get("#station_nameselect_name1").on("click", "onName1", this);
			screen.get("#station_nameselect_name2").on("click", "onName2", this);
			screen.get("#station_nameselect_name3").on("click", "onName3", this);
			screen.get("#station_nameselect_name4").on("click", "onName4", this);
		} else {
			FsNode message = new FsNode("message",screen.getId());
			message.setProperty("request","station");
			message.setProperty("@stationid",model.getProperty("@stationid"));
			model.notify("@stationevents/fromclient",message);
		}
	}
	
	public void onTimeoutChecks(ModelEvent e) {
		// time to move we give the user, 5 seconds to start picking if not we pick
		if (timeout_toselect!=0) {
			timeout_toselect=timeout_toselect-1;
			if  (timeout_toselect==0) {
				FsNode message = new FsNode("message",screen.getId());
				message.setProperty("request","station");
				message.setProperty("@stationid",model.getProperty("@stationid"));
				model.notify("@stationevents/fromclient",message);
			}
		}
	}
	
	public void onName1(Screen s, JSONObject data) {
		ExhibitionMemberManager.claimMember(screen,name1);
		ExhibitionMemberManager.freeName(screen,name2);
		ExhibitionMemberManager.freeName(screen,name3);
		gotoNextStep();
	}
	
	public void onName2(Screen s, JSONObject data) {
		ExhibitionMemberManager.claimMember(screen,name2);
		ExhibitionMemberManager.freeName(screen,name1);
		ExhibitionMemberManager.freeName(screen,name3);
		gotoNextStep();
	}
	
	public void onName3(Screen s, JSONObject data) {
		ExhibitionMemberManager.claimMember(screen,name3);
		ExhibitionMemberManager.freeName(screen,name1);
		ExhibitionMemberManager.freeName(screen,name2);
		gotoNextStep();
	}
	
	public void onName4(Screen s, JSONObject data) {
		System.out.println("NAME4");
	}
	
	
	public void onFlagSelected(Screen s, JSONObject data) {
		// temp free all names
		ExhibitionMemberManager.freeAllNames(screen);
	}
	
	private void gotoNextStep() {
		FsNode message = new FsNode("message",screen.getId());
		message.setProperty("request","station");
		message.setProperty("@stationid",model.getProperty("@stationid"));
		model.notify("@stationevents/fromclient",message);
	}
	
	
	

}
