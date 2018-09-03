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
package org.springfield.lou.controllers.image.selection;

import java.awt.Color;
import java.util.Random;

import org.json.simple.JSONObject;
import org.springfield.fs.FsNode;
import org.springfield.fs.FsPropertySet;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.model.ModelEvent;
import org.springfield.lou.screen.Screen;
import org.springfield.lou.controllers.ExhibitionMemberManager;

/**
 * CoverFlowRemoteController.java
 *
 * @author Pieter van Leeuwen
 * @copyright Copyright: Noterik B.V. 2016
 * @package org.springfield.lou.controllers.image.selection
 * 
 */
public class SelectionMapRemoteController extends Html5Controller {


	double lastx = -1;
	double lasty = -1;
	private String mycolor = "#888888";
	String deviceid;
	String userLanguage;
	boolean voicecoverplayed = false;
	String username = null;
	FsNode member;

	public SelectionMapRemoteController() { }

	public void attach(String sel) {
		selector = sel;

		member = ExhibitionMemberManager.getMember(screen); // based on exhibitionid and browserid
		member.setProperty("master","waiting");
		model.onNotify("@selectionmapevent","onSelectionmapeventChange",this);
		
		mycolor = getColor();
		
		fillPage();
	}
	
    public void onSelectionmapeventChange(ModelEvent event) {
    	FsNode msg = event.getTargetFsNode();
    	String master = msg.getProperty("master");
    	String mastername = msg.getProperty("mastername");
    	System.out.println("S="+screen.getBrowserId()+" I="+master+" mastername="+mastername);
    	if (master.equals(screen.getBrowserId())) {
    		if (member.getProperty("master").equals("waiting")) {
    			member.setProperty("master","master");
    			member.setProperty("currentmaster",mastername);
        		fillPage();
        	}
    	} else {
    		if (member.getProperty("master").equals("waiting")) {
    			member.setProperty("master","slave");
    			member.setProperty("currentmaster",mastername);
        		fillPage();
        	}	
    	}
    }

	public void fillPage() {
		String userLanguage = model.getProperty("@userlanguage");
		String audiosrc = "";
		String transcript = "";
		FsNode stationnode = model.getNode("@station");
		if (stationnode != null) {
			JSONObject data = new JSONObject();
			data.put("title", stationnode.getSmartProperty("en", "title"));

			FsNode contentnode = model.getNode("@contentnode");
			audiosrc = contentnode.getProperty("voiceover");

			FsNode language_content = model.getNode("@language_photoexplore_coverflow_screen");
			if (language_content!=null) {
				data.put("helptext1", language_content.getSmartProperty(userLanguage, "swipe_help_text"));
				data.put("helptext2", language_content.getSmartProperty(userLanguage, "select_help_text"));
			}
			
			if (audiosrc != null) {
				data.put("audio", language_content.getSmartProperty(userLanguage, "coverflow_intro_audio"));
				data.put("transcript",  language_content.getSmartProperty(userLanguage, "coverflow_transcript"));
				data.put("transcript-text", contentnode.getProperty("transcript"));
			}
			if (member!=null) {
				String master = member.getProperty("master");
				System.out.println("master="+master);
				if (master.equals("") || master.equals("waiting")) {
					data.put("waiting","true");
				} else if (master.equals("master")) {
					data.put("master","true");	
				} else {
					data.put("slave","true");		
				}
			}
			data.put("nl","true");
			screen.get(selector).render(data);
			screen.get("#trackpad").track("mousemove","mouseMove", this); // track mouse move event on the #trackpad
			screen.get("#trackpad").on("enter","onEnter", this);
			screen.get("#pointer_icon").css("background-color","#"+mycolor);
		}		
	}
	
	public void mouseMove(Screen s, JSONObject data) {
		double width = ((long) data.get("width")) * 1.0;
		double height = ((long) data.get("height")) * 1.0;

		double rx = data.get("screenX").toString().indexOf(".") > -1 ? (double) data.get("screenX") : ((long) data.get("screenX")) * 1.0;
		double ry = data.get("screenY").toString().indexOf(".") > -1 ? (double) data.get("screenY") : ((long) data.get("screenY")) * 1.0;

		lastx = (rx / width) * 100; 
		lasty = (ry / height) * 100;	

		FsNode msg = new FsNode("msg","1");
		msg.setProperty("x", "" + lastx); // we should support auto convert
		msg.setProperty("y", "" + lasty);
		msg.setProperty("color", mycolor);
		msg.setProperty("action", "spotmove");
		model.notify("@stationevents/fromclient",msg);

		//Update position on triggering screen
		screen.get("#pointer_icon").css("left",(rx)+"px");
		screen.get("#pointer_icon").css("top",(ry)+"px"); // comp back for the top shift
		screen.get("#pointer_icon").css("background-color", mycolor);
	}
	
	public void onEnter(Screen s, JSONObject data) {
		System.out.println("Enter received from client on x="+lastx+" y="+lasty);
		FsNode msg = new FsNode("msg","1");
		msg.setProperty("x", "" + lastx); // we should support auto convert
		msg.setProperty("y", "" + lasty);
		msg.setProperty("color", mycolor);
		msg.setProperty("action", "enter");
		model.notify("@stationevents/fromclient",msg);
	}

	public void previousPage(Screen s, JSONObject data) {
		System.out.println("Station select requested by mobile");
		model.setProperty("/screen/state","globalcodeselect");
	}
	
	private String getColor() {     
		String color;
		String colorProperty = model.getProperty("@color");

		if (colorProperty != null) {
			color = colorProperty;
		} else {		
			color = generateColor();
			model.setProperty("@color", color);
		}	    
		return color;
	}

	private String generateColor() {	    
		//to get rainbow, pastel colors
		Random random = new Random();
		final float hue = random.nextFloat();
		// Saturation between 0.6 and 0.8
		final float saturation = (random.nextInt(2000) + 6000) / 10000f;
		final float luminance = 0.9f;
		final Color color = Color.getHSBColor(hue, saturation, luminance);

		return "#"+Integer.toHexString(color.getRGB()).substring(2);
	}
	
}
