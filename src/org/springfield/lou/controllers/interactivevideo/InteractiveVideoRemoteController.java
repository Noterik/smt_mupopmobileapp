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
	
	public InteractiveVideoRemoteController() {
		
	}
	
	public void attach(String sel) {
		selector = sel;
		stationid = model.getProperty("@stationid");
		exhibitionid = model.getProperty("@exhibitionid");
		/*
		stationid = model.getProperty("@stationid");
		exhibitionid = model.getProperty("@exhibitionid");
		FsNode audio_settings = model.getNode("/domain/mupop/user/daniel/exhibition/"+exhibitionid+"/station/"+stationid+"/video/1/audio/1/");
		String audioUrl = audio_settings.getSmartProperty(model.getProperty("@userlanguage"), "url");
		System.out.println("Audio URL: " + audioUrl);
 		JSONObject jso= new JSONObject(); 
 		jso.put("url", audioUrl);
		screen.get(selector).render(jso);
		screen.get(selector).loadScript(this);
		
		model.notify("/shared/exhibition/"+exhibitionid+"/station/"+ stationid +"/vars/userJoined");
		
		String isPlaying = model.getProperty("/shared/app/interactivevideo/exhibition/"+exhibitionid+"/station/"+ stationid +"/vars/isplaying");
		if (isPlaying != null && isPlaying.equals("true"))
 			playAudio();
		onClockUpdate(new ModelEvent());
		*/
		
		//screen.get("#language_select").on("click", "onLanguageSelect", this);
		//screen.get("#audio_problem").on("click", "onAudioProblem", this);
		
		//add event listeners
		//model.onNotify("/shared/exhibition/"+exhibitionid+"/station/"+ stationid +"/vars/play", "onPlayEvent", this);
		//model.onNotify("/shared/exhibition/"+exhibitionid+"/station/"+ stationid +"/vars/pause", "onPauseEvent", this);
		//model.onNotify("/shared/exhibition/"+exhibitionid+"/station/"+ stationid +"/vars/wantedtime", "onClockUpdate", this);
		//model.onNotify("/shared/exhibition/"+exhibitionid+"/station/"+ stationid +"/vars/exhibitionEnded", "onExhibitionEnd", this);
		//list = FSListManager.get("/domain/mupop/user/daniel/exhibition/"+exhibitionid+"/station/"+stationid+"/video/1",false);

		//time based event listeners
		//model.onTimeLineNotify("/domain/mupop/user/daniel/exhibition/"+exhibitionid+"/station/"+stationid+"/video/1","/shared/mupop/exhibition/"+exhibitionid+"/station/"+ stationid+"/currenttime","starttime","duration","onTimeLineEvent",this);
	
    	FsNode message = new FsNode("message",screen.getId());
    	message.setProperty("action","playrequest"); // lets ask the station for the audio we need to play and where to listen for events
		model.notify("@stationevents/fromclient",message);
		model.onPropertiesUpdate("/screen/audiocommand","onAudioCommandFromServer", this);
	}
	
	public void onAudioCommandFromServer(ModelEvent e) {
		System.out.println("GOT BACK AUDIO COMMAND="+e);
		FsPropertySet ps = (FsPropertySet)e.target;
		String action = ps.getProperty("action");
		if (action.equals("playaudio")) {
			String audiourl = ps.getProperty("audiourl");
			System.out.println("AUDIO URL="+audiourl);
			timeline = ps.getProperty("timeline");
			System.out.println("AUDIO timeline="+timeline);
	    	JSONObject audiocmd = new JSONObject();
	    	audiocmd.put("action","play");
	    	audiocmd.put("src",audiourl);
	    	screen.get("#mobile").update(audiocmd);
			model.onTimeLineNotify(timeline,"/shared/mupop/exhibition/"+exhibitionid+"/station/"+ stationid+"/currenttime","starttime","duration","onTimeLineEvent",this);

		}
	}
	
	
	
	public FsNode getCurrentFsNode(double time) {
		FsNode match = null;
		double nextstarttime = 1000000000;
		list = FSListManager.get("/domain/mupop/user/daniel/exhibition/"+exhibitionid+"/station/"+stationid+"/video/1",false);
		List<FsNode> nodes = list.getNodes();
		if (nodes != null) {
			for (Iterator<FsNode> iter = nodes.iterator(); iter.hasNext();) {
				FsNode node = (FsNode) iter.next();
				if (time<node.getStarttime() && node.getStarttime()<nextstarttime) {
					nextstarttime = node.getStarttime();
					match = node;
				}
			}
		}
		return match;
	}
	
	
	public void onLanguageSelect(Screen s, JSONObject data){
		screen.get("#mobile").append("div", "languageselectionmupopremote", new LanguageSelectionRemoteControllerMupop());
		screen.get(selector).remove();
	}
	
	public void onAudioProblem(Screen s, JSONObject data){
		System.out.println("AUDIO PROBLEM CLICKED!");
		screen.get(selector).hide();
		screen.get("#mobile").append("div", "audiocheck", new AudioCheckController());
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
				screen.get(selector).show();
				//playAudio();
			}
		}
	}
	
	public void onPlayEvent(ModelEvent e){
		System.out.println("Play Event");
		playAudio();
		
	}
	
	public void onPauseEvent(ModelEvent e){
		System.out.println("Pause Event");
		pauseAudio();
		
	}
	
	public void pauseAudio(){
		JSONObject nd = new JSONObject();
		nd.put("action","pause");
		nd.put("target", "audiop");
		screen.get(selector).update(nd);
	}
	
	public void playAudio(){
		JSONObject nd = new JSONObject();
		nd.put("action","play");
		nd.put("target", "audiop");
		screen.get(selector).update(nd);
	}
	
	public void onClockUpdate(ModelEvent e) {
		String stationid = model.getProperty("@stationid");
		String exhibitionid = model.getProperty("@exhibitionid");
		JSONObject nd = new JSONObject();
		String s = model.getProperty("/shared/exhibition/"+exhibitionid+"/station/"+ stationid +"/vars/curPlayTime");
		double time = 0;
		if (s==null)
			s = "";
		else 
			time = Double.parseDouble(s);
		FsNode fsn = getCurrentFsNode(time);
		
		nd.put("action","wantedtime");
		nd.put("target", "audiop");
		nd.put("wantedtime", model.getProperty("/shared/exhibition/"+exhibitionid+"/station/"+ stationid +"/vars/wantedtime"));
		if (s != null)
			nd.put("streamtime", s);
		if(fsn!=null)
			nd.put("endsAt", (fsn.getStarttime()));
		screen.get(selector).update(nd);		
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
