/* 
* PhotoInfoSpotsRemoteController.java
* 
* Copyright (c) 2016 Noterik B.V.
* 
* This file is part of smt_mupopmobileapp, related to the Noterik Springfield project.
*
* smt_mupopmobileapp is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* smt_mupopmobileapp is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with smt_mupopmobileapp.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.springfield.lou.controllers.photoinfospots;

import org.json.simple.JSONObject;
import org.springfield.fs.FsNode;
import org.springfield.fs.FsPropertySet;
import org.springfield.lou.application.Html5ApplicationInterface;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.screen.Screen;

/**
 * PhotoInfoSpotsRemoteController.java
 *
 * @author Pieter van Leeuwen
 * @copyright Copyright: Noterik B.V. 2016
 * @package org.springfield.lou.controllers.photoinfospots
 * 
 */
public class PhotoInfoSpotsRemoteController extends Html5Controller {
	
	String sharedspace;
	private Html5ApplicationInterface app;
	float lastx = -1;
	float lasty = -1;
	
	public PhotoInfoSpotsRemoteController() {}
	
	public void attach(String sel) {
		selector = sel;
		app = screen.getApplication();
		
		String path = model.getProperty("/screen/exhibitionpath");

		FsNode stationnode = model.getNode(path);
		if (stationnode!=null) {
			JSONObject data = new JSONObject();
			//data.put("url",stationnode.getProperty("url_mobile"));
			
			screen.get(selector).parsehtml(data);
			screen.get("#photoinfospotsremote_trackarea").track("mousemove","mouseMove", this); // track mouse move event on the #screen tag
			screen.get("#photoinfospotsremote_trackarea").on("mouseup","mouseUp",this);
			screen.get("#photoinfospotsremote_trackarea").on("touchend","mouseUp",this);
		}
		
		sharedspace = model.getProperty("/screen/sharedspace");
	}
	
	 public void mouseUp(Screen s,JSONObject data) {
		 //System.out.println("UP="+data.toJSONString());

		 FsPropertySet ps = new FsPropertySet(); // send them as a set so we get 1 event
		 ps.setProperty("x",""+lastx); // we should support auto convert
		 ps.setProperty("y",""+lasty);
		 ps.setProperty("action","up");
		 model.setProperties(sharedspace+"station/2",ps);
	 }
	
	 public void mouseMove(Screen s,JSONObject data) {
		 //System.out.println("MOVE="+data.toJSONString());
		 
		 lastx = getPercX(data);
		 lasty = getPercY(data);
		 
		 FsPropertySet ps = new FsPropertySet(); // send them as a set so we get 1 event
		 ps.setProperty("x",""+lastx); // we should support auto convert
		 ps.setProperty("y",""+lasty);
		 ps.setProperty("action","move");
		 model.setProperties(sharedspace+"station/2",ps);
	 }
	 
	 private float getPercX(JSONObject data) {
		 float x = 0;
		 float rx = 0;
		 try {
			String[] posxy = ((String)data.get("clientXY")).split(",");
		 	rx = Float.parseFloat(posxy[0]);
			long width = (Long)data.get("width");
			x = ((float)rx/width)*100;
			return x;
		 } catch(Exception e) {
			 e.printStackTrace();
			 return -1;
		 }
	 }
	 
	 private float getPercY(JSONObject data) {
		 float y = 0;
		 float ry = 0;
		 try {
			String[] posxy = ((String)data.get("clientXY")).split(",");
		 	ry = Float.parseFloat(posxy[1]);

			long height = (Long)data.get("height");
			y = ((float)ry/height)*100;
		
			if (y<0) y = 0;
			if (y>100) y = 100;
			return y;
		 } catch(Exception e) {
			 e.printStackTrace();
			 return -1;
		 }
		 
	 }
}
