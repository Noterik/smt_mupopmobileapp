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
import org.springfield.lou.screen.Screen;

public class InteractiveVideoRemoteController extends Html5Controller  {
	
	public InteractiveVideoRemoteController() {
	}
	
	public void attach(String sel) {
		selector = sel;

 		screen.get(selector).render(new JSONObject());
	}
 	 
}
