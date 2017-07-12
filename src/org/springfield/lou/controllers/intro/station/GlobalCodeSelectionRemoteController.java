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
package org.springfield.lou.controllers.intro.station;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springfield.fs.FSList;
import org.springfield.fs.FsNode;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.screen.Screen;

/**
 * StationSelectionRemoteController.java
 *
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2016
 * @package org.springfield.lou.controllers.intro.station
 * 
 */
public class GlobalCodeSelectionRemoteController extends Html5Controller {

    String deviceid;
    String userLanguage;
	private static Map<String, String> codes = new HashMap<String, String>();
	String alphacode="";
	String numbercode="";
	Boolean toggle = false;
	
    
    public GlobalCodeSelectionRemoteController() { }
    
    public void attach(String sel) {
    	selector = sel;
    	fillPage();
    }
    
    private  void fillPage() {
    	JSONObject data = new JSONObject();
    	
    	userLanguage = model.getProperty("@userlanguage");
    	if (userLanguage==null) {
    		// force to set
    		userLanguage = "en";
    		model.setProperty("@userlanguage","en");
    	}


    	data.put("title", model.getProperty("@language_station_code_selection_screen/title", userLanguage));
    	data.put("explanation", model.getProperty("@language_station_code_selection_screen/explanation", userLanguage));
  
    	if (userLanguage.equals("en")) {
    		data.put("linktext","Click here for MuPoP product");
    		data.put("codetext","Enter the code you see on the big screen to continue");
    	} else if (userLanguage.equals("nl")) {
       		data.put("linktext","Druk hier voor MuPoP produkt");   		
    		data.put("codetext","Geef de code in die op het grote scherm staat om verder te gaan");
    	}
    	if (toggle) {
    		fillNumberCodes(data);
    	} else {
    		fillAlphaCodes(data);
    	}
    	toggle = !toggle;
    	
    	screen.get(selector).render(data);
    	
    	// set the correct flag based on preset;
    	String langcode = "#flag_"+model.getProperty("@userlanguage");
    	System.out.println("LANGRENDER="+langcode);
    	screen.get(langcode).css("opacity","1");
    	screen.get(langcode).css("box-shadow","1px 1px 1px  #605c05");
    	

    	screen.get(".codeselect-button").on("click", "onCodeSelectSelected", this);
    	screen.get(".flag").on("click", "onFlagSelected", this);
    }
    
    public void onFlagSelected(Screen s, JSONObject data) {
    	String langcode = (String)data.get("id");
    	langcode = langcode.substring(5);
    	System.out.println("FLAG SELECTED="+langcode);
    	model.setProperty("@userlanguage",langcode);
    	toggle = !toggle; // reverse the toggle back!
    	fillPage();
    }
    
    public void onCodeSelectSelected(Screen s, JSONObject data) {
    	String buttonid = (String) data.get("id");
    	String value = codes.get(buttonid);
    	if (toggle) {
    		alphacode = value;
    	} else {
    		numbercode = value;
    	}
    	
    	String trycode = alphacode+numbercode;
    	FSList joincodes = model.getList("@joincodes"); // check all the active stations
    	if (joincodes != null) {
    		for (Iterator<FsNode> iter = joincodes.getNodes().iterator(); iter.hasNext();) {
    			FsNode node = (FsNode) iter.next();
    			String correctcode = node.getProperty("codeselect");
    	    	if (correctcode!=null && correctcode.equals(trycode)) {
    	    	    	String exhibitionid = node.getProperty("exhibitionid");
    	    	    	String stationid = node.getProperty("stationid");
    	    	    	String userid = node.getProperty("userid");
			    	    screen.removeContent(selector.substring(1));
			    	    model.setProperty("@username",userid);
			    	    model.setProperty("@exhibitionid",exhibitionid);
			    	    model.setProperty("@stationid",stationid);
			    	    System.out.println("STATION ID="+stationid);
			    	    System.out.println("USER ID="+userid);
			    	    System.out.println("EXHIBTION ID="+exhibitionid);
			    	    
			    	    String style = model.getProperty("@station/style");		    	    
			    		if (style==null || style.equals("")) {
			    			style = model.getProperty("@exhibition/style");
			    			if (style==null || style.equals("")) {
			    				style="neutral";
			    			}
			    		}
			    		System.out.println("STYLE="+style+" STATIONID="+stationid);
			    		screen.loadStyleSheet("mobile/styles/"+style+".css");
			    	    FsNode message = new FsNode("message",screen.getId());
			    	    message.setProperty("request","station");
			    	    message.setProperty("@stationid", stationid);
			    	    System.out.println("STATION MSG="+message.asXML());
			    	    model.notify("@stationevents/fromclient",message);
    	    	    return;
    	    	}
    		}
    	}
    	fillPage();
     }
    
    private void fillAlphaCodes(JSONObject data) {
    	codes = new HashMap<String, String>();
    	codes.put("codeselect-P1","A");data.put("P1","A");
    	codes.put("codeselect-P2","B");data.put("P2","B");
    	codes.put("codeselect-P3","C");data.put("P3","C");
    	codes.put("codeselect-P4","D");data.put("P4","D");
    	codes.put("codeselect-P5","E");data.put("P5","E");
    	codes.put("codeselect-P6","F");data.put("P6","F");
    	codes.put("codeselect-P7","G");data.put("P7","G");
    	codes.put("codeselect-P8","H");data.put("P8","H");
    	codes.put("codeselect-P9","I");data.put("P9","I");
    	codes.put("codeselect-P10","J");data.put("P10","J");
    	codes.put("codeselect-P11","K");data.put("P11","K");
    	codes.put("codeselect-P12","L");data.put("P12","L");
    }
    
    private void fillNumberCodes(JSONObject data) {
    	codes = new HashMap<String, String>();
    	codes.put("codeselect-P1","1");data.put("P1","1");
    	codes.put("codeselect-P2","2");data.put("P2","2");
    	codes.put("codeselect-P3","3");data.put("P3","3");
    	codes.put("codeselect-P4","4");data.put("P4","4");
    	codes.put("codeselect-P5","5");data.put("P5","5");
    	codes.put("codeselect-P6","6");data.put("P6","6");
    	codes.put("codeselect-P7","7");data.put("P7","7");
    	codes.put("codeselect-P8","8");data.put("P8","8");
    	codes.put("codeselect-P9","9");data.put("P9","9");
    	codes.put("codeselect-P10","");data.put("P10","");
    	codes.put("codeselect-P11","0");data.put("P11","0");
    	codes.put("codeselect-P12","");data.put("P12","");
    }
    
 }
