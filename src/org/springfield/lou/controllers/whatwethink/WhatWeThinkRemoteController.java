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
import java.util.List;
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
	String mycolor;
	String posx=null;
	String posy=null;
	FSList axis;
	String questionnumber="0";
	int questioncount=-1;
	String question="";
	String answer1="";
	String answer2="";

	public WhatWeThinkRemoteController() { }
	

	public void attach(String sel) {
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
		if (feedback) return;
		
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
			 screen.get("#whatwethink-statements-dot").css("top",""+screenYP+"%");
			 screen.get("#whatwethink-statements-dot").css("left",""+screenXP+"%");
			 
			FsNode pnode = model.getNode("@whatwethinkdots/results/"+username); // based on a bug needs fixing

			pnode.setProperty("x", ""+screenXP);
			pnode.setProperty("y",""+screenYP);
			pnode.setProperty("lastseen",""+new Date().getTime());
			pnode.setProperty("c",mycolor);
			model.setProperty("@whatwethinkdots/changed",""+new Date().getTime()); // set dirty
		}
	 }

	public void onAppStateChange(ModelEvent e) {
		FsNode message = e.getTargetFsNode();
		
		String command = message.getProperty("command");
		if (command.equals("newquestion")) {
			//only target device that is addressed
			String screenid = message.getProperty("screenid");
			if (!screenid.equals("all") && !screenid.equals(screen.getId())) {
				return;
			}
			question = message.getProperty("question");
			answer1 = message.getProperty("answer1");
			answer2 = message.getProperty("answer2");
			
			//model.setProperty("@itemid", message.getProperty("itemid")); // why is this needed not in shared space?
			
			//model.setProperty("@itemquestionid", message.getProperty("itemquestionid"));
			if (message.getProperty("feedback").equals("true")) {
				feedback=true;

			} else {
				feedback=false;	
			}
			String m = message.getProperty("color");
			if (m!=null && !m.equals("")) {
				mycolor = m;
			}
			
			String qn = message.getProperty("questioncount");
			try {
				questioncount = Integer.parseInt(qn);
			} catch(Exception e2) {
				e2.printStackTrace();
			}
			questionnumber = message.getProperty("questionnumber");
			
			// move the dot back
			FsNode player = model.getNode("@station/player['"+username+"']");
			if (player!=null) {
				String dataline = player.getProperty("pos_"+questionnumber);
				if (dataline!=null && !dataline.equals("") && dataline.indexOf("-1")==-1) {
					String[] parts = dataline.split(",");
					 posx = parts[0];
					 posy = parts[1];
				} else {
					 posx = "-100";
					 posy = "-100";
				}
				m = player.getProperty("color");
				if (m!=null && !m.equals("")) {
					mycolor = m;
				}
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
			score = message.getProperty("score");
			answerid= message.getProperty("answerid");
			fillPage();
		} else if (command.equals("timer")) {			
			timeout = message.getProperty("timer");
			screen.get("#whatwethink-timer-one").html(timeout);
			screen.get("#whatwethink-timer-two").html("volgende keuze in "+timeout+" sec");
		} else if (command.equals("feedbackstate")) {
			if (message.getProperty("feedback").equals("true")) {
				feedback=true;
			} else {
				feedback=false;	
			}
			fillPage();
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
		if (axis==null) axis = model.getList("@itemaxis");
		//FsNode item = model.getNode("@item");

		JSONObject data = new JSONObject();
		if (feedback) {
			data.put("feedback","true");
		}
		data.put("timer",timeout);
		data.put("timer2","volgende keuze in "+timeout+" sec");
		

		//String playername = model.getProperty("@playername");
		//data.put("username", playername);
		//FsNode questionnode = model.getNode("@itemquestion");
		
		data.put("question", question);
		//data.put("questionid", questionnode.getId());
		data.put("questionnumber", questionnumber);
		data.put("answer1", answer1);
		data.put("answer2", answer2);
		if (mycolor!=null) data.put("color", mycolor);
		if (posx!=null) data.put("posx", posx);
		if (posy!=null) data.put("posy", posy);
		
		// add axis info
	
		FSList axisnodes =new FSList();
		List<FsNode> nodes = axis.getNodes();
		if (nodes != null) {
			
			FsNode pnode = model.getNode("@station/player['"+username+"']");
			//System.out.println("pnode="+pnode+" username="+username);
			//System.out.println("N="+pnode.asXML());
			int hm = 0;
			double cf = 1;
			for (Iterator<FsNode> iter = nodes.iterator(); iter.hasNext();) {
				FsNode node = (FsNode) iter.next();
				try {
					String m = pnode.getProperty("axis_"+node.getId());
					int mi = Integer.parseInt(m);
					if (mi>hm) {
						hm = mi;
						cf = 50/hm;
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}

			//System.out.println("HIGHMARK="+hm+" cf="+cf);
			for (Iterator<FsNode> iter = nodes.iterator(); iter.hasNext();) {
				FsNode node = (FsNode) iter.next();
				FsNode axisnode = new FsNode("axis",node.getId());
				axisnode.setProperty("name", node.getProperty("name"));
				axisnode.setProperty("color", node.getProperty("color"));
				try {
					String m = pnode.getProperty("axis_"+node.getId());
					int mi = Integer.parseInt(m);
					axisnode.setProperty("size", ""+((mi*cf)+1));
				} catch(Exception e) {}
				axisnodes.addNode(axisnode);
			}
		}
		
		int answercount=getAnswerCount();
		
		if (answercount==0) {
			data.put("noanswer","true");
		} else if(answercount<5) {
			data.put("below5answer","true");
		} else if (answercount<questioncount) {
			data.put("enoughanswer","true");
		} else {
			data.put("allanswer","true");
		}
		data.put("answercount",""+answercount);
		
		data.put("axis",axisnodes.toJSONObject("en","name,color,size"));
		
		screen.get(selector).render(data);

 		screen.get("#mobile").track("mousemove","mouseMove", this);

	}
	
	private int getAnswerCount() {
		int count = 0;
		FsNode player = model.getNode("@station/player['"+username+"']");
		for (int i=1;i<=questioncount;i++) {
			if (player!=null) {
				String dataline = player.getProperty("pos_"+i);
				if (dataline!=null && dataline.indexOf("null")==-1 && dataline.indexOf("-1")==-1) {
					count++;
				}
			}
		}
		return count;
	}
	



}
