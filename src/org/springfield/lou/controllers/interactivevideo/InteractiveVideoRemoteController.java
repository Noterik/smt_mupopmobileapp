package org.springfield.lou.controllers.interactivevideo;


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
import org.springfield.lou.model.ModelBindEvent;
import org.springfield.lou.model.ModelEvent;
import org.springfield.lou.screen.Screen;

public class InteractiveVideoRemoteController extends Html5Controller  {
	
	public InteractiveVideoRemoteController() {
	}
	
	public void attach(String sel) {
		selector = sel;

 		screen.get(selector).render(new JSONObject());
		model.setProperty("@videoid","1");
 		model.onTimeLineNotify("@video","/shared['mupop']/videotimers['1']/currenttime","starttime","duration","onTimeLineEvent",this);
	}

public void onTimeLineEvent(ModelEvent e) {
	if (e.eventtype==ModelBindEvent.TIMELINENOTIFY_ENTER) {
		System.out.println("MOBILE VIDEO ENTERED BLOCK ("+e.eventtype+") ="+e.getTargetFsNode().getId());
	} else if (e.eventtype==ModelBindEvent.TIMELINENOTIFY_LEAVE) {
		System.out.println("MOBILE VIDEO LEFT BLOCK ("+e.eventtype+") ="+e.getTargetFsNode().getId());
	}
}
 	 
}
