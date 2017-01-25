package org.springfield.lou.controllers.interactivevideo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.json.simple.JSONObject;
import org.springfield.fs.FsNode;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.model.ModelEvent;
import org.springfield.lou.screen.Screen;

public class QuestionController extends Html5Controller{
	
	int countdown = 0;
	String questionpath;
	FsNode questionnode;
	
	public QuestionController(){
	}
	
	public void attach(String sel) {
		selector = sel;
		String languageCode = model.getProperty("@userlanguage");
		questionpath = model.getProperty("/screen['vars']/fullquestionpath");
		
		questionnode = model.getNode(questionpath);
		
		Collection<JSONObject> anwers = new ArrayList<JSONObject>();
		JSONObject data = new JSONObject();
		data.put("question", questionnode.getProperty("question"));
		data.put("duration", questionnode.getProperty("duration"));
		
		JSONObject a = new JSONObject();
		String a1 =questionnode.getProperty("answer1");
		if (a1!=null && !a1.equals("")) {
			a.put("name", a1);
			a.put("value", "1");
			anwers.add(a);
		}
		String a2 =questionnode.getProperty("answer2");
		System.out.println("A2="+a2);
		a = new JSONObject();
		if (a2!=null && !a2.equals("")) {
			a.put("name", a2);
			a.put("value", "2");
			anwers.add(a);
		}
		String a3 =questionnode.getProperty("answer3");
		a = new JSONObject();
		if (a3!=null && !a3.equals("")) {
			a.put("name", a3);
			a.put("value", "3");
			anwers.add(a);
		}
		String a4 =questionnode.getProperty("answer4");
		a = new JSONObject();
		if (a4!=null && !a4.equals("")) {
			a.put("name", a4);
			a.put("value", "4");
			anwers.add(a);
		}
		data.put("answers", anwers);
		
		model.setProperty("/screen['vars']/correctanswer",questionnode.getProperty("correctanswer"));
		
		screen.get(selector).render(data);
		
	
		screen.get(".interactivevideo_answer").on("click", "onAnswer", this);
		
		countdown = 9;
		model.onNotify("/shared[timers]/1second","on1SecondTimer",this); 
	}
	
	public void on1SecondTimer(ModelEvent event) {
		countdown--;
		if (countdown<0) countdown=0; // just in case looks ugly;
		screen.get("#interactivevideo_questiontimer").html(""+countdown+" SEC");
	}

	
	public void onAnswer(Screen s, JSONObject data){
		String answer = (String)data.get("id");
		answer = answer.substring(6);
		System.out.println("answer given: " + answer);
		String ap = questionpath+"/correctanswers";
		String tap = questionpath+"/totalanswers";

		try {
			int score = Integer.parseInt(model.getProperty("/screen['vars']/score"));
			if (answer.equals(model.getProperty("/screen['vars']/correctanswer"))) {
				screen.get("#interactivevideo_questionfeedback").html("Goed beantwoord!!");
				score++;
				model.setProperty("/screen['vars']/score",""+score);
				Object cs = model.getProperty(ap);
				if (cs!=null) {
					String currentscorestring = (String)cs;
					if (!currentscorestring.equals("")) {
						int currentscore = Integer.parseInt(currentscorestring);
						model.setProperty(ap,""+(currentscore+1));
					} else {
						model.setProperty(ap,"1");
					}
				} else {
					model.setProperty(ap,"1");	
				}
			} else {
				screen.get("#interactivevideo_questionfeedback").html("Sorry fout beantwoord");
			}
			Object cs = model.getProperty(tap);
			if (cs!=null) {
				String currenttotalstring = (String)cs;
				if (!currenttotalstring.equals("")) {
					int currenttotal = Integer.parseInt(currenttotalstring);
					model.setProperty(tap,""+(currenttotal+1));
					System.out.println("TA1");
				} else {
					model.setProperty(tap,"1");
					System.out.println("TA2");
				}
			} else {
				model.setProperty(tap,"1");
				System.out.println("TA3");
			}
			screen.get("#answer1").hide();
			screen.get("#answer2").hide();
			screen.get("#answer3").hide();
			screen.get("#answer4").hide();
			screen.get("#interactivevideo_questionfeedback").show();
		
			// update the score on the main panel
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
