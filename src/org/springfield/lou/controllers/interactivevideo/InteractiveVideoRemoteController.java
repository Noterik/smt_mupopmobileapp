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
package org.springfield.lou.controllers.interactivevideo;

import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONObject;
import org.springfield.fs.FSList;
import org.springfield.fs.FSListManager;
import org.springfield.fs.FsNode;
import org.springfield.fs.FsPropertySet;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.controllers.intro.language.LanguageSelectionRemoteControllerMupop;
import org.springfield.lou.model.ModelBindEvent;
import org.springfield.lou.model.ModelEvent;
import org.springfield.lou.screen.Screen;

public class InteractiveVideoRemoteController extends Html5Controller  {

	FSList list;
	String stationid;
	String exhibitionid;
	String timeline;
	long duration = -1;
	int countdown = 0;

	public InteractiveVideoRemoteController() {

	}

	public void attach(String sel) {
		selector = sel;
		stationid = model.getProperty("@stationid");
		exhibitionid = model.getProperty("@exhibitionid");
		
 		JSONObject jso= new JSONObject(); 

 		FsNode stationnode = model.getNode("@station");
 		if (stationnode != null) { 		    
 		    jso.put("title", stationnode.getSmartProperty("nl", "title"));
 		}
    	String type = model.getProperty("@station/contentselect");
    	if (type!=null && !type.equals("none")) {
    		jso.put("previous","true");
    	}
 		
 		screen.get(selector).render(jso);
 		
 		//screen.get("#interactive_video_remote_previous").on("mouseup", "previousPage", this);
		screen.get("#previous").on("mouseup", "previousPage", this);
		screen.get("#screenbutton").on("mouseup", "onScreenButton", this);
		
		model.onNotify("/shared/exhibition/"+exhibitionid+"/station/"+ stationid +"/vars/wantedtime", "onClockUpdate", this);

		FsNode message = new FsNode("message",screen.getId());
		message.setProperty("action","playrequest"); // lets ask the station for the audio we need to play and where to listen for events
		model.notify("@stationevents/fromclient",message);
		model.onPropertiesUpdate("/screen/audiocommand","onAudioCommandFromServer", this);
		
		model.setProperty("/screen['vars']/score","0");
		model.setProperty("/screen['vars']/numberofquestions","7");
		model.setProperty("/screen['vars']/numberofanswers","1");
		screen.get("#interactive_video_remote_numberofquestions").html("vraag 1 van 7");
	}

	public void onAudioCommandFromServer(ModelEvent e) {
		FsPropertySet ps = (FsPropertySet)e.target;
		String action = ps.getProperty("action");
		if (action.equals("playaudio")) {
			String audiourl = ps.getProperty("audiourl");
			timeline = ps.getProperty("timeline");
			try {
				duration = Long.parseLong(ps.getProperty("duration"))/1000;
				setScreenTimer(duration);
			} catch(Exception e2) {
				e2.printStackTrace();
			}
			JSONObject audiocmd = new JSONObject();
			audiocmd.put("action","play");
			audiocmd.put("src",audiourl);
			screen.get("#mobile").update(audiocmd);
			model.onTimeLineNotify(timeline,"/shared/mupop/exhibition/"+exhibitionid+"/station/"+ stationid+"/currenttime","starttime","duration","onTimeLineEvent",this);

		}
	}
	
	public void setScreenTimer(long duration) {
		countdown = (int)duration;
		model.onNotify("/shared[timers]/1second","on1SecondTimer",this); 
	}
	
	public void on1SecondTimer(ModelEvent event) {
		countdown--;
		if (countdown<0) countdown=0; // just in case looks ugly;
			int total =  countdown;
		    int min = total / 60;
		    int sec = total - min * 60;
		    screen.get("#interactive_video_remote_duration_min").html(""+min);
		    screen.get("#interactive_video_remote_duration_sec").html(""+sec);
	}


	public void onTimeLineEvent(ModelEvent e) {
		if (e.eventtype==ModelBindEvent.TIMELINENOTIFY_ENTER) {
			if(e.getTargetFsNode().getName().equals("question")){
				//pauseAudio();
				//start question controller
				screen.get(selector).hide();
				String questionpath=timeline+"['"+e.getTargetFsNode().getId()+"']";
				model.setProperty("/screen['vars']/fullquestionpath",questionpath);
				screen.get("#mobile").append("div", "questionnaire", new QuestionController());

			}
		} else if (e.eventtype==ModelBindEvent.TIMELINENOTIFY_LEAVE) {
			if(e.getTargetFsNode().getName().equals("question")){
				//remove question controller and play
				screen.get("#questionnaire").remove();
				int score = Integer.parseInt(model.getProperty("/screen['vars']/score"));
				int noq = Integer.parseInt(model.getProperty("/screen['vars']/numberofquestions"));
				int noa = Integer.parseInt(model.getProperty("/screen['vars']/numberofanswers"));
				screen.get("#interactive_video_remote_score").html("Aantal goed - "+score);
				
				// add one to answer number (none given is wrong !)
				noa++;
				model.setProperty("/screen['vars']/numberofanswers",""+noa);
				
				screen.get("#interactive_video_remote_numberofquestions").html("vraag "+noa+" van "+noq);
				screen.get(selector).show();
			}
		}
	}
	

	public void onClockUpdate(ModelEvent event) {
		JSONObject audiocmd = new JSONObject();
		audiocmd.put("action","wantedtime");
		audiocmd.put("wantedtime",event.getTargetFsNode().getId());
		screen.get("#mobile").update(audiocmd);
	}
	

	public void destroyed() {
		super.destroyed();
	}
	
	public void previousPage(Screen s, JSONObject data) {	
	    JSONObject audiocmd = new JSONObject();
	    audiocmd.put("action","pause");
	    screen.get("#mobile").update(audiocmd);
	    
	    FsNode message = new FsNode("message",screen.getId());
	    message.setProperty("action","");
	    message.setProperty("request","contentselectforce");
	    model.notify("@stationevents/fromclient",message);
		
	    FsNode m = new FsNode("message",screen.getId());
	    m.setProperty("request","contentselect");
	    model.notify("@stationevents/fromclient",m);

	}
	
	public void onScreenButton(Screen s, JSONObject data) {
	    System.out.println("Screen button pressed");
    	JSONObject audiocmd = new JSONObject();
    	audiocmd.put("action","play");
		audiocmd.put("src","http://mupop.net/eddie/sounds/silent.m4a");
		System.out.println("PLAY AUDIO="+audiocmd.toJSONString());
		screen.get("#mobile").update(audiocmd);
		model.setProperty("/screen/state","globalcodeselect");
	}
}
