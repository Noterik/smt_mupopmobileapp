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
package org.springfield.lou.controllers.quiz;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

import org.json.simple.JSONObject;
import org.springfield.fs.FSList;
import org.springfield.fs.FsNode;
import org.springfield.lou.controllers.ExhibitionMemberManager;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.model.ModelEvent;
import org.springfield.lou.screen.Screen;

public class QuizRemoteController extends Html5Controller {
	
	String correctanswer = "";
	String score="";
	String answerid;
	boolean feedback = false;
	boolean answercorrect = false;
	String timeout;
	String slidetype;
	FsNode slidenode;
	private int slidetimeout=-1;
	private int showtimer=0;
	private String showanswer="false";
	private String myanswer="";

	public QuizRemoteController() { }
	

	public void attach(String sel) {
		selector = sel;
		JSONObject data = new JSONObject();	
		//setSlideNodeValues();
		//fillPage();
		model.onNotify("@quizslide","onSlideUpdate", this);
		//model.onNotify("/shared[timers]/1second", "on1SecondTimer", this);
		
	}
	
	public void onSlideUpdate(ModelEvent e) {
		FsNode node = e.getTargetFsNode();
		if (node!=slidenode) { // extra check since we always get a node from shared space
			slidenode = node;
			slidetype = slidenode.getProperty("type");
			if (slidetype==null) slidetype="image";
			slidetimeout =  Integer.parseInt(slidenode.getProperty("timeout"));
			if (slidetype.equals("imagequestion") || slidetype.equals("videoquestion")) {
				showtimer = 3;
			}
			myanswer="";
			//fillPage();
		} 
		String sa = node.getProperty("showanswer");
		if (sa!=null) {
			showanswer = sa;
		}
		fillPage();
	}


	public void onAppStateChange(ModelEvent e) {
		FsNode message = e.getTargetFsNode();
		String command = message.getProperty("command");
		System.out.println("CLIENT COMMAND="+command);
	}

	private void fillPage() {
		model.setProperty("@contentrole", "mainapp");
		FsNode item = model.getNode("@item");
		JSONObject data = new JSONObject();
		
		FsNode member= ExhibitionMemberManager.getMember(screen);
		if (member!=null) {
			data.put("membername",member.getProperty("name"));
		}
		if (slidenode!=null) {
			data.put("slidetype",slidetype);
			data.put(slidetype,"true");
			if (showanswer.equals("true")) {
				if (slidenode.getProperty("correctanswer").equals(myanswer)) {
					System.out.println("CORRECT !!");
					data.put("answercorrect","true");	
				}
				data.put("showanswer","true");
			}
			if (!myanswer.equals("")) {
				data.put("myanswer"+myanswer,"true");
			} else {
				data.put("noanswer","true");
			}
			String sc = ExhibitionMemberManager.getMember(screen).getProperty("score");
			if (sc==null || sc.equals("")) sc = "0";
			data.put("score",sc);
			data.put("slideid",slidenode.getId());
			if (slidetype.equals("image")) {
				data.put("imageurl",slidenode.getProperty("imageurl"));
			} else if (slidetype.equals("imagequestion")) {
				data.put("imageurl",slidenode.getProperty("imageurl"));
				data.put("slidequestion",slidenode.getProperty("question"));
				data.put("slideanswer1",slidenode.getProperty("answer1"));
				data.put("slideanswer2",slidenode.getProperty("answer2"));
				data.put("slideanswer3",slidenode.getProperty("answer3"));
				data.put("slideanswer4",slidenode.getProperty("answer4"));
				if (slidenode.getProperty("correctanswer").equals(myanswer)) {
					data.put("answercorrect","true");	
				} 
			} else if (slidetype.equals("videoquestion")) {
				data.put("videourl",slidenode.getProperty("videourl"));
				data.put("slidequestion",slidenode.getProperty("question"));
				data.put("slideanswer1",slidenode.getProperty("answer1"));
				data.put("slideanswer2",slidenode.getProperty("answer2"));
				data.put("slideanswer3",slidenode.getProperty("answer3"));
				data.put("slideanswer4",slidenode.getProperty("answer4"));
				if (slidenode.getProperty("correctanswer").equals(myanswer)) {
					data.put("answercorrect","true");	
				} 
			}
		}


	//	data.put("score",score);
		screen.get(selector).setViewProperty("template","mobile/quizremote/quizremote_"+slidetype+".mst");
		screen.get(selector).render(data);
		screen.get(".quiz-game-answer").on("mouseup", "onAnswer", this);
	}
	

	public void onAnswer(Screen s, JSONObject data) {
		String id = ((String) data.get("id")).substring(16,17);
		myanswer = id;
		if (slidenode.getProperty("correctanswer").equals(myanswer)) {
			try {
				int score = Integer.parseInt(ExhibitionMemberManager.getMember(screen).getProperty("score"));
				score++;
				ExhibitionMemberManager.getMember(screen).setProperty("score",""+score);	
			} catch(Exception e) {
				ExhibitionMemberManager.getMember(screen).setProperty("score","1");	
			}
		}
		fillPage();
	}
	

}
