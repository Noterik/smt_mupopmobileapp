package org.springfield.lou.controllers.interactivevideo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.json.simple.JSONObject;
import org.springfield.fs.FsNode;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.screen.Screen;

public class QuestionController extends Html5Controller{
	
	public QuestionController(){
	}
	
	public void attach(String sel) {
		selector = sel;
		String languageCode = model.getProperty("@userlanguage");
		
		String questionpath = model.getProperty("/screen['vars']/fullquestionpath");
		System.out.println("QUESTIONPATH="+questionpath);
		
		FsNode questionnode = model.getNode(questionpath) ;
		
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
		screen.get(selector).render(data);
		
		
		/*
		String question_text = model.getNode(question_data.getPath()+"/text/1").getSmartProperty(languageCode, "value");
		String duration = model.getNode(question_data.getPath()).getProperty("duration");
		JSONObject data = new JSONObject();
		data.put("duration", duration);
		data.put("question", question_text);
		System.out.println(question_data.getPath()+"/text/1");
		System.out.println(question_data.getPath()+"/text/1");
		Iterator<FsNode> answers = model.getList(question_data.getPath()+"/answer").getNodes().iterator();
		
		
		while(answers.hasNext()){
			FsNode answer = answers.next();
			JSONObject q = new JSONObject();
			System.out.println(answer.getSmartProperty(languageCode, "value"));
			q.put("name", answer.getSmartProperty(languageCode, "value"));
			q.put("value", answer.getId());
			anwers.add(q);
		}

		data.put("answers", anwers);
		
		screen.get(selector).parsehtml(data);
		screen.get(selector).loadScript(this);
		screen.get(".answer").on("click", "onAnswer", this);
		
		
		
		JSONObject nd = new JSONObject();
		nd.put("action","setCountdown");
		nd.put("duration", duration);
		screen.get(selector).update(nd);
		*/
	}
	
	public void onAnswer(Screen s, JSONObject data){
		System.out.println("answer given: " + data.get("id"));
		screen.get(selector).remove();
		screen.get("#interactivevideoremote").show();
	}
}
