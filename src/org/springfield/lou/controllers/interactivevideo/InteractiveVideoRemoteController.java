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
 		screen.get(selector).render(jso);
		
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
		System.out.println("WANTED TIME="+event.getTargetFsNode().asXML());
		JSONObject audiocmd = new JSONObject();
		audiocmd.put("action","wantedtime");
		audiocmd.put("wantedtime",event.getTargetFsNode().getId());
		screen.get("#mobile").update(audiocmd);
	}
	

	public void onExhibitionEnd(ModelEvent e){
		System.out.println("Remote:: Exhibition ended");
		screen.get("#audiocheck").remove();
		screen.get("#questionnaire").remove();
		screen.get("#interactivevideoremote").remove();
		screen.get("#mobile").append("div", "exhibitionend", new ExhibitionEndController());
	}

	public void destroyed() {
		super.destroyed();
	}
}
