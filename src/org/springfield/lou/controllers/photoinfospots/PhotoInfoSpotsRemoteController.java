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

import org.springfield.fs.FsNode;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.controllers.image.selection.CoverFlowRemoteController;
import org.springfield.lou.controllers.image.spotting.ZoomAndAudioRemoteController;
import org.springfield.lou.model.ModelEvent;

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
	
	public PhotoInfoSpotsRemoteController() {}
	
	public void attach(String sel) {
		selector = sel;
		
		String path = model.getProperty("/screen/exhibitionpath");

		FsNode stationnode = model.getNode(path);
		if (stationnode!=null) {
			
			sharedspace = model.getProperty("/screen/sharedspace");
			System.out.println("About to notify about screen joining");
			model.notify("/screen/tst", new FsNode("join", "1"));
			model.onNotify("/screen/photoinfospots", "onEnterImage", this);
			
			//TODO: load from config what needs to be loaded
			screen.get("#photoinfospotsremote").append("div", "coverflowremote", new CoverFlowRemoteController());
		}
	} 
	
	public void onEnterImage(ModelEvent e) {
		
		FsNode target = e.getTargetFsNode();

		if (target.getId().equals("enter")) {		
			screen.get("#coverflowremote").remove();
		
			screen.get("#photoinfospotsremote").append("div", "zoomandaudioremote", new ZoomAndAudioRemoteController());
		}
	}
}
