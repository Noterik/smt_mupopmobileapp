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

    	data.put("title", model.getProperty("@language_station_code_selection_screen/title", userLanguage));
    	data.put("explanation", model.getProperty("@language_station_code_selection_screen/explanation", userLanguage));

    	if (toggle) {
    		fillNumberCodes(data);
    	} else {
    		fillAlphaCodes(data);
    	}
    	toggle = !toggle;
    	
    	screen.get(selector).render(data);
    	screen.get(".codeselect-button").on("click", "onCodeSelectSelected", this);
    }
    
    public void onCodeSelectSelected(Screen s, JSONObject data) {
    	JSONObject audiocmd = new JSONObject();
    	audiocmd.put("action","play");
		audiocmd.put("src","http://betadash.mupop.net/eddie/sounds/click.mp3");
		screen.get("#mobile").update(audiocmd);
		
	String buttonid = (String) data.get("id");
    	String value = codes.get(buttonid);
    	if (toggle) {
    		alphacode = value;
    	} else {
    		numbercode = value;
    	}
    	
    	String trycode = alphacode+numbercode;
    	
    FSList users = model.getList("/domain['mupop']/user");
	List<FsNode> usernodes = users.getNodes(); 
	if (usernodes != null) {
	    for (Iterator<FsNode> iter = usernodes.iterator(); iter.hasNext();) {
	    	FsNode node = (FsNode) iter.next();
	    	String userid=node.getId();
	    	//System.out.println("USERID="+userid);
	        FSList exhibitions = model.getList("/domain['mupop']/user['"+userid+"']/exhibition");
	    	List<FsNode> exhibitionnodes = exhibitions.getNodes(); 
	    	if (exhibitionnodes != null) {
	    	    for (Iterator<FsNode> iter2 = exhibitionnodes.iterator(); iter2.hasNext();) {
	    	    	FsNode enode = (FsNode) iter2.next();
	    	    	String estate = enode.getProperty("state");
	    	    	if (estate!=null && estate.equals("on")) {
	    	    		String exhibitionid=enode.getId();
	    		        FSList stations = model.getList("/domain['mupop']/user['"+userid+"']/exhibition['"+exhibitionid+"']/station");
	    		    	List<FsNode> stationnodes = stations.getNodes(); 
	    		    	if (stationnodes != null) {
	    		    	    for (Iterator<FsNode> iter3 = stationnodes.iterator(); iter3.hasNext();) {
	    		    			FsNode snode = (FsNode) iter3.next();
	    		    			String correctcode = snode.getProperty("codeselect");
	    			    	    System.out.println("CHECK CODE "+trycode+" "+correctcode);
	    				    	if (correctcode!=null && correctcode.equals(trycode)) {
	    				    	    System.out.println("CORRECT CODE SELECTED !!!");
	    				    	    screen.removeContent(selector.substring(1));
	    				    	    model.setProperty("@username",userid);
	    				    	    model.setProperty("@exhibitionid",exhibitionid);
	    				    	    model.setProperty("@stationid",snode.getId());
	    				    		String style = model.getProperty("@exhibition/style");
	    				    		if (style==null || style.equals("")) {
	    				    			style="neutral";
	    				    		}
	    				    		screen.loadStyleSheet("mobile/styles/"+style+".css");
	    				    	    FsNode message = new FsNode("message",screen.getId());
	    				    	    message.setProperty("request","station");
	    				    	    message.setProperty("@stationid", snode.getId());
	    				    	    model.notify("@stationevents/fromclient",message);
	    				    	    return;
	    				    	}
	    		    	    }
	    		    	}
	    	    	}
	    	    }
	    	}
	    }
	}
    	
    FSList stations = model.getList("@stations"); // check all the active stations
	List<FsNode> nodes = stations.getNodes();
	if (nodes != null) {
	    for (Iterator<FsNode> iter = nodes.iterator(); iter.hasNext();) {
		FsNode node = (FsNode) iter.next();
		String correctcode = node.getProperty("codeselect");
	    	if (correctcode!=null && correctcode.equals(trycode)) {
	    	    System.out.println("CORRECT CODE SELECTED !!!");
	    	    screen.removeContent(selector.substring(1));
	    	    model.setProperty("@stationid",node.getId());
	    	    FsNode message = new FsNode("message",screen.getId());
	    	    message.setProperty("request","station");
	    	    message.setProperty("@stationid", node.getId());
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
