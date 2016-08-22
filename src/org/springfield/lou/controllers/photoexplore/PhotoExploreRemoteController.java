package org.springfield.lou.controllers.photoexplore;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONObject;
import org.springfield.fs.FSList;
import org.springfield.fs.FSListManager;
import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.lou.application.Html5Application;
import org.springfield.lou.application.Html5ApplicationInterface;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.screen.Screen;

public class PhotoExploreRemoteController extends Html5Controller  {
	
	//private Html5ApplicationInterface app;
	FsViewPort viewport;
	
	public PhotoExploreRemoteController() {
	}
	
	public void attach(String sel) {
		selector = sel;
		screen.loadStyleSheet("mobile/photoexploreremote/photoexploreremote.css");
 		screen.get(selector).parsehtml(new JSONObject());
 		screen.get("#photoexploreremote_trackarea").track("mousemove","mouseMove", this);
		screen.get("#photoexploreremote_trackarea").on("touchend","touchEnd",this);
		screen.get("#photoexploreremote_trackarea").on("touchstart","touchStart",this);
		screen.get("#photoexploreremote_trackarea").on("mousedown","startDrag",this);
		screen.get("#photoexploreremote_trackarea").on("mouseup","stopDrag",this);
 		
 		viewport = new FsViewPort(screen,"#photoexploreremote_trackarea","/app/sceneplayer/location/"+model.getProperty("/screen/location")+"/element/screen1");

	}
	
	 public void mouseMove(Screen s,JSONObject data) {
		System.out.println("MOVE="+data.toJSONString());
		viewport.mouseMove(s,data);
	 }
	 
	 public void touchEnd(Screen s,JSONObject data) {
		System.out.println("END="+data.toJSONString());
		viewport.touchEnd(s,data);
	 }
	 
	 public void touchStart(Screen s,JSONObject data) {
		System.out.println("START="+data.toJSONString());
		viewport.touchStart(s,data);
	 }
	 
	 public void startDrag(Screen s,JSONObject data) {
			System.out.println("STARTDRAG="+data.toJSONString());
		viewport.startDrag(s,data);
	 }
	 
	 public void stopDrag(Screen s,JSONObject data) {
		System.out.println("STOPDRAG="+data.toJSONString());
		viewport.stopDrag(s,data);
	 }
	
 	 
}
