package org.springfield.lou.controllers.interactivevideo;

import org.json.simple.JSONObject;
import org.springfield.fs.FsNode;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.model.ModelBindEvent;
import org.springfield.lou.model.ModelEvent;
import org.springfield.lou.screen.Screen;

public class InteractiveVideoRemoteController extends Html5Controller  {
		
	public InteractiveVideoRemoteController() {
		
	}
	
	public void attach(String sel) {
		selector = sel;
		String stationid = model.getProperty("@stationid");
		String exhibitionid = model.getProperty("@exhibitionid");
		
		model.setProperty("/screen/languagecode", "nl");
		System.out.println("THIS PRINTS!!: ");
		FsNode audio_settings = model.getNode("/domain/mupop/user/daniel/exhibition/"+exhibitionid+"/station/"+stationid+"/video/1/audio/1/");
		System.out.println("THIS ALSO PRINTS!!: ");
		String audioUrl = audio_settings.getSmartProperty(model.getProperty("/screen/languagecode"), "url");
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

		//add event listeners
		model.onNotify("/shared/exhibition/"+exhibitionid+"/station/"+ stationid +"/vars/play", "onPlayEvent", this);
		model.onNotify("/shared/exhibition/"+exhibitionid+"/station/"+ stationid +"/vars/pause", "onPauseEvent", this);
		model.onNotify("/shared/exhibition/"+exhibitionid+"/station/"+ stationid +"/vars/wantedtime", "onClockUpdate", this);
		model.onNotify("/shared/exhibition/"+exhibitionid+"/station/"+ stationid +"/vars/exhibitionEnded", "onExhibitionEnd", this);
		
		//time based event listeners
		model.onTimeLineNotify("/domain/mupop/user/daniel/exhibition/1475504815025/station/1475504866572/video/1","/shared/mupop/exhibition/"+exhibitionid+"/station/"+ stationid+"/currenttime","starttime","duration","onTimeLineEvent",this);
		
	}
	
	public void onTimeLineEvent(ModelEvent e) {
		if (e.eventtype==ModelBindEvent.TIMELINENOTIFY_ENTER) {
//			System.out.println("MOBILE VIDEO ENTERED BLOCK ("+e.eventtype+") ="+);
			if(e.getTargetFsNode().getName().equals("question")){
				pauseAudio();
				//start question controller
				screen.get(selector).hide();
				screen.get("#mobile_content").append("div", "questionnaire", new QuestionController(e.getTargetFsNode()));
				
			}
//			System.out.println("question is: " + e.getTargetFsNode().getProperty("question"));
		} else if (e.eventtype==ModelBindEvent.TIMELINENOTIFY_LEAVE) {
			if(e.getTargetFsNode().getName().equals("question")){
				//remove question controller and play
				screen.get("#questionnaire").remove();
				screen.get(selector).show();
				playAudio();
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
		nd.put("action","wantedtime");
		nd.put("target", "audiop");
		nd.put("wantedtime", model.getProperty("/shared/exhibition/"+exhibitionid+"/station/"+ stationid +"/vars/wantedtime"));
		screen.get(selector).update(nd);		
	}
	
	public void onExhibitionEnd(ModelEvent e){
		System.out.println("Remote:: Exhibition ended");
		screen.get("#audiocheck").remove();
		screen.get("#interactivevideoremote").remove();
		screen.get("#mobile_content").append("div", "exhibitionend", new ExhibitionEndController());
	}
	
	public void destroyed() {
		super.destroyed();
	}
}
