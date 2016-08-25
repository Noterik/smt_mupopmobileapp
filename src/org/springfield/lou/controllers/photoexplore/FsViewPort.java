package org.springfield.lou.controllers.photoexplore;

import java.util.Date;

import org.json.simple.JSONObject;
import org.springfield.fs.FsPropertySet;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.screen.Capabilities;
import org.springfield.lou.screen.Screen;

public class FsViewPort extends Html5Controller {
	
	private boolean indrag;
	private boolean pinchstart=false;;
	private Double pinchstartscale;
	private Double pinchstartdistance;
	private Double resultpercentage;
	private float startx;
	private float starty;
	private float resultx;
	private float resulty;
	private boolean dragstart=false;
	private float dragstartx = 0;
	private float dragstarty = 0;
	private long lasttouch = -1;
	private boolean pointermode = false;
	private String path;
	
	public FsViewPort(Screen s,String selector,String p) {
		path = p;
		pinchstartscale = 100.0;
	}


	 public void mouseMove(Screen s,JSONObject data) {
		 Capabilities cap = s.getCapabilities();
		 if (cap.getDeviceMode()==cap.MODE_GENERIC && !indrag) return;
		 String[] posxy = ((String)data.get("clientXY")).split(",");
		 Float x1 = Float.parseFloat(posxy[0]);
		 Float y1 = Float.parseFloat(posxy[1]);
		 if (posxy.length==2) {
			 touchDrag(s,x1,y1,data);
		 } else {
			 Float x2 = Float.parseFloat(posxy[2]);
			 Float y2 = Float.parseFloat(posxy[3]);
			 touchPinch(s,x1,y1,x2,y2,data);
		 }
	 }
	 
	 
	 public void downStart(Screen s,JSONObject data) {
		String username = s.getUserName();
		if (username!=null && !username.equals("")) {
			//SensoApplication app = (SensoApplication)screen.getApplication();
			s.getModel().setProperty("/app/requesters/"+username+"/state","askpermition");
		}
	 }
	 
	 public void downStop(Screen s,JSONObject data) {
		 System.out.println("stop down "+s);
		String username = s.getUserName();
		if (username!=null && !username.equals("")) {
			//SensoApplication app = (SensoApplication)screen.getApplication();
			s.getModel().setProperty("/app/requesters/"+username+"/state","");
		}
	 }
	
	 public void touchStart(Screen s,JSONObject data) {
		 System.out.println("TOUCH START");
		 if (lasttouch==-1) {
			 lasttouch = new Date().getTime();
		 } else {
			 long nowtouch =  new Date().getTime();
			 if ((nowtouch-lasttouch) < 500) {
				pointermode = !pointermode;
				 System.out.println("TOGGLE "+pointermode);
			 }
			 lasttouch = -1;
		 }
	 }

	
	 public void startDrag(Screen s,JSONObject data) {
//		 System.out.println("STARTDRAG");
		 indrag = true;
	 }
	 
	 public void stopDrag(Screen s,JSONObject data) {
	//	 System.out.println("STOPDRAG");
		 indrag = false;
	 }
	 
	 public void touchEnd(Screen s,JSONObject data) {
	//	 System.out.println("TOUCHEND");
		 if (pinchstart) {
			 pinchstart = false;
			 pinchstartscale = resultpercentage;
		 }
		 if (dragstart) {
			 dragstart = false;
			 startx = resultx;
			 starty = resulty;
			 System.out.println("ENDDRAG!!!");
		 }
	 }
	 
	 public void touchPinch(Screen s,Float x1,Float y1,Float x2,Float y2,JSONObject data) {
			// System.out.println("TOUCH POINT1="+x1+","+y1+" POINT2="+x2+","+y2);
			 Float dx = Math.abs(x1-x2);
			 Float dy = Math.abs(y1-y2);
			 Float dt = (dx*dx)+(dy*dy);
			 Double distance = Math.sqrt(dt.doubleValue());
		     if (!pinchstart) {
		    	 pinchstart = true;
		    	 pinchstartdistance = distance;
		     }
		     double scalepercentage = distance/pinchstartdistance;
		     resultpercentage = pinchstartscale * scalepercentage;
		     
			 System.out.println("TOUCH DELTA="+dx+","+dy+" scale="+pinchstartscale+" result="+resultpercentage+" lscale="+scalepercentage+" startdistance="+pinchstartdistance+" distance="+distance);
			 s.getModel().setProperty("/app/results/mainscreen/scalepos",""+resultpercentage);
			 
			 
			 FsPropertySet ps = new FsPropertySet(); // send them as a set so we get 1 event
			 ps.setProperty("x",""+resultx); // we should support auto convert
			 ps.setProperty("y",""+resulty);
			 ps.setProperty("scale",""+resultpercentage);
			 System.out.println("PINCH SET SHARED PROPERTIES="+path+" "+resultpercentage);
			 s.getModel().setProperties(path,ps);
			 
	 }
	 

	 public void touchDrag(Screen s,float x,float y,JSONObject data) {
	     if (!dragstart) {
	    	 dragstart = true;
	    	 dragstartx = x;
	    	 dragstarty = y; 	 
	     }
	     float dx = (x-dragstartx)*2;
	     float dy = (y-dragstarty)*2;	
	     
	     resultx = startx + dx;
	     resulty = starty + dy;
	     

		 FsPropertySet ps = new FsPropertySet(); // send them as a set so we get 1 event
		 ps.setProperty("x",""+resultx); // we should support auto convert
		 ps.setProperty("y",""+resulty);
		 ps.setProperty("scale",""+resultpercentage);
		 System.out.println("DRAG SET SHARED PROPERTIES="+path+" "+resultpercentage);
		 s.getModel().setProperties(path,ps);
	 }

}
