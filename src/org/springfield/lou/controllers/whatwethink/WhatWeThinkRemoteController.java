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
package org.springfield.lou.controllers.whatwethink;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;

import org.json.simple.JSONObject;
import org.springfield.fs.FSList;
import org.springfield.fs.FsNode;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.model.ModelEvent;
import org.springfield.lou.screen.Screen;

public class WhatWeThinkRemoteController extends Html5Controller {
	
	String correctanswer = "";
	String score="";
	String answerid;
	boolean feedback = false;
	boolean answercorrect = false;
	String timeout;
	String username;

	public WhatWeThinkRemoteController() { }
	

	public void attach(String sel) {
		System.out.println("WWT MOBILE ATTACHED");
		selector = sel;
		username = "guest"+	screen.getCapabilities().getCapability("smt_browserid");
		
		JSONObject data = new JSONObject();

			data.put("username",username);
			screen.get(selector).render(data);
			FsNode message = new FsNode("message",screen.getId());
			message.setProperty("username", username);	
			message.setProperty("command", "join");	
			model.notify("@stationevents/towhatwethinkserver", message);

			model.onNotify("@appstate", "onAppStateChange", this);
	}
	
	 public void mouseMove(Screen s,JSONObject data) {
	//	System.out.println("MOVE="+data.toJSONString());
		Double screenXP;
		if (data.get("screenXP") instanceof Double) {
			screenXP=(Double)data.get("screenXP");
		} else {
			Long l=(Long)data.get("screenXP");
			screenXP = new Double(l);
		}
		
		Double screenYP;
		if (data.get("screenYP") instanceof Double) {
			screenYP=(Double)data.get("screenYP");
		} else {
			Long l=(Long)data.get("screenYP");
			screenYP = new Double(l);
		}
		if (screenYP<50) {
//			System.out.println("XP="+screenXP+" YP="+screenYP);
			 screen.get("#whatwethink-statements-dot").css("top",""+screenYP+"%");
			 screen.get("#whatwethink-statements-dot").css("left",""+screenXP+"%");
			 
			FsNode pnode = model.getNode("@whatwethinkdots/results/"+username); // based on a bug needs fixing
			pnode.setProperty("x", ""+screenXP);
			pnode.setProperty("y",""+screenYP*2);
			pnode.setProperty("c","red");
			model.setProperty("@whatwethinkdots/changed",""+new Date().getTime()); // set dirty
		}
	//	System.out.println("BROWSERID="+username);
	 }

	public void onAppStateChange(ModelEvent e) {
		FsNode message = e.getTargetFsNode();
		
		String command = message.getProperty("command");
		System.out.println("CLIENT COMMAND="+command);
		if (command.equals("newquestion")) {
			//only target device that is addressed
			String screenid = message.getProperty("screenid");
			if (!screenid.equals("all") && !screenid.equals(screen.getId())) {
				return;
			}
			model.setProperty("@itemid", message.getProperty("itemid")); // why is this needed not in shared space?
			model.setProperty("@itemquestionid", message.getProperty("itemquestionid"));
			if (message.getProperty("feedback").equals("true")) {
				feedback=true;
			} else {
				feedback=false;	
			}
			fillPage();
		} else if (command.equals("remove")) {
			//only target device that is addressed
			if (!message.getProperty("screenid").equals(screen.getId())) {
				return;
			}
			//player didn't answer, ask to join again
		//	removePlayer();
		} else if (command.equals("scoreupdate")) {
			System.out.println("SCOREUPDATE="+message.asXML());
			score = message.getProperty("score");
			System.out.println("SCORE="+score);
			answerid= message.getProperty("answerid");
			fillPage();
		} else if (command.equals("timer")) {			
			timeout = message.getProperty("timer");
			//screen.get("#trivia-timer").html(timeout);
			//screen.get("#trivia-feedback-timer").html(timeout);
			System.out.println("CLIENTTIMER="+timeout);
		} else if (command.equals("feedbackstate")) {
			if (message.getProperty("feedback").equals("true")) {
				feedback=true;
			} else {
				feedback=false;	
			}
			fillPage();
		}
	}

	private void fillPage() {
		model.setProperty("@contentrole", "mainapp");

		FsNode item = model.getNode("@item");

		JSONObject data = new JSONObject();
		if (feedback) {
			data.put("feedback","true");
		}
		data.put("timer",timeout);

		String playername = model.getProperty("@playername");
		data.put("username", playername);
		FsNode questionnode = model.getNode("@itemquestion");
		data.put("question", questionnode.getProperty("question"));
		data.put("answer1", questionnode.getProperty("answer1"));
		data.put("answer2", questionnode.getProperty("answer2"));

		screen.get(selector).render(data);
 		screen.get("#mobile").track("mousemove","mouseMove", this);

	}



}
