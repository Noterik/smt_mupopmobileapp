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
		this.addToRecoveryList("profile/playername");
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
		//System.out.println("A0");
		System.out.println("NEW SCREEN CALLED"+path);
		if (path!=null) {
			//System.out.println("A1");
			if (path.equals("")) {
				//System.out.println("A11");
				s.getModel().setProperty("@username","unknown");
				s.getModel().setProperty("@exhibitionid","unknown");
				//System.out.println("A12");
				s.get("#screen").append("div","mobile",new MobileController());
				//System.out.println("A13");
			} else {
				System.out.println("OLD METHOD JUMPER OR URL IS WRONG !");
			}
		} else {
			//System.out.println("B1");
			String u = s.getParameter("u");
			String e = s.getParameter("e");
			//System.out.println("USER="+u+" EX="+e);
			s.getModel().setProperty("@username", u);
			s.getModel().setProperty("@exhibitionid",e);
			s.get("#screen").append("div","mobile",new MobileController());
		}
	}
}
