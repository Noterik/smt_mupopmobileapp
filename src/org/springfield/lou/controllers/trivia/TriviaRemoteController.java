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
package org.springfield.lou.controllers.trivia;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.jws.WebParam.Mode;

import org.json.simple.JSONObject;
import org.springfield.fs.FSList;
import org.springfield.fs.FSListManager;
import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.lou.application.Html5Application;
import org.springfield.lou.application.Html5ApplicationInterface;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.model.ModelEvent;
import org.springfield.lou.screen.Screen;

public class TriviaRemoteController extends Html5Controller  {
	
	
	public TriviaRemoteController() {
	}
	
	public void attach(String sel) {
		selector = sel;
		System.out.println("TRIVIA STARTED");
 		screen.get(selector).render();
		model.onNotify("@appstate", "onAppStateChange", this);
 	}
	
	public void onAppStateChange(ModelEvent event) {
		model.setProperty("@itemid",event.getTargetFsNode().getProperty("itemid")); // why is this needed not in shared space?
		System.out.println("eV="+event.getTargetFsNode().asXML());
		fillPage();
	}
	
	private void fillPage() {
		model.setProperty("@contentrole","mainapp");
		System.out.println("EXHIBITIONID="+model.getProperty("@exhibitionid"));	
		System.out.println("STATIONID="+model.getProperty("@stationid"));	
		System.out.println("ITEMID="+model.getProperty("@itemid"));	
		FsNode item = model.getNode("@item");
		System.out.println("ITEM="+item);	
		if (item!=null) {
			System.out.println("ITEMNODE="+item.asXML());
		}
		
		Collection<JSONObject> anwers = new ArrayList<JSONObject>();
		JSONObject data = new JSONObject();
		model.setProperty("@itemquestionid","1");
		FsNode questionnode = model.getNode("@itemquestion");
		System.out.println("QUESTION="+questionnode.asXML());
		
		data.put("question", questionnode.getProperty("question"));
		data.put("duration", questionnode.getProperty("duration"));
		
		JSONObject a = new JSONObject();
		String a1 =questionnode.getProperty("answer1");
		if (a1!=null && !a1.equals("")) {
			a.put("name", a1);
			a.put("value", "1");
			anwers.add(a);
		}
		String a2 =questionnode.getProperty("answer2");
		System.out.println("A2="+a2);
		a = new JSONObject();
		if (a2!=null && !a2.equals("")) {
			a.put("name", a2);
			a.put("value", "2");
			anwers.add(a);
		}
		String a3 =questionnode.getProperty("answer3");
		a = new JSONObject();
		if (a3!=null && !a3.equals("")) {
			a.put("name", a3);
			a.put("value", "3");
			anwers.add(a);
		}
		String a4 =questionnode.getProperty("answer4");
		a = new JSONObject();
		if (a4!=null && !a4.equals("")) {
			a.put("name", a4);
			a.put("value", "4");
			anwers.add(a);
		}
		data.put("answers", anwers);
 		screen.get(selector).render(data);
 		
 		
		screen.get(".trivia_answer").on("mouseup","onAnswer",this);
	}
	
	public void onAnswer(Screen s, JSONObject data) {
		System.out.println("WOW="+data.toJSONString());
		FsNode msgnode = new FsNode("msgnode","1");
		msgnode.setProperty("value",(String)data.get("id"));
		msgnode.setProperty("clientid",screen.getId());
		model.notify("@itemanwser",msgnode);
	}
 	 
}
