/* 
 *SceneApplication.java
 * 
 * Copyright (c) 2016 Noterik B.V.

 */
package org.springfield.lou.application.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springfield.fs.FSList;
import org.springfield.fs.FSListManager;
import org.springfield.fs.FsNode;
import org.springfield.lou.application.*;
import org.springfield.lou.controllers.*;
import org.springfield.lou.homer.LazyHomer;
import org.springfield.lou.screen.*;
import org.springfield.lou.servlet.LouServlet;

public class MupopmobileApplication extends Html5Application {

	public MupopmobileApplication(String id) {
		super(id); 
		this.setSessionRecovery(true);
		this.addToRecoveryList("profile/language");
		this.addToRecoveryList("profile/color");
		this.addToRecoveryList("profile/deviceid");
	}

	public void onNewScreen(Screen s) {
		s.setLanguageCode("en");
		s.get("#screen").attach(new ScreenController());

		loadStyleSheet(s, "libs/bootstrap.min");
		loadStyleSheet(s, "libs/bootstrap-theme");
		loadStyleSheet(s, "libs/font-awesome.min");

		//Check if we have an id for this device already
		if (s.getModel().getProperty("@deviceid") == null) {
			s.getModel().setProperty("@deviceid", UUID.randomUUID().toString());
		}	
		

		String path = s.getParameter("path");
		
		System.out.println("NEW SCREEN CALLED"+path);
		if (path!=null) {
			if (path.equals("")) {
				s.getModel().setProperty("@username","unknown");
				s.getModel().setProperty("@exhibitionid","unknown");
				s.get("#screen").append("div","mobile",new MobileController());
			} else {
				System.out.println("OLD METHOD JUMPER OR URL IS WRONG !");
			}
		} else {
			String u = s.getParameter("u");
			String e = s.getParameter("e");
			System.out.println("USER="+u+" EX="+e);
			s.getModel().setProperty("@username", u);
			s.getModel().setProperty("@exhibitionid",e);
			s.get("#screen").append("div","mobile",new MobileController());
		}
	}
}
