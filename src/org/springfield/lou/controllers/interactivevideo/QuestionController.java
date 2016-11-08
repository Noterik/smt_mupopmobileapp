package org.springfield.lou.controllers.interactivevideo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.json.simple.JSONObject;
import org.springfield.fs.FsNode;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.screen.Screen;

public class QuestionController extends Html5Controller{
	
	FsNode question_data;
	
	public QuestionController(FsNode data){
		this.question_data = data;
		
	}
	
	public void attach(String sel) {
		selector = sel;
		String languageCode = model.getProperty("/screen/languagecode");
		Collection<JSONObject> anwers = new ArrayList<JSONObject>();
		String question_text = model.getNode(question_data.getPath()+"/text/1").getSmartProperty(languageCode, "value");
		JSONObject data = new JSONObject();					
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
		
		screen.get(".answer").on("click", "onAnswer", this);
	}
	
	public void onAnswer(Screen s, JSONObject data){
		System.out.println("answer given: " + data.get("id"));
		screen.get(selector).hide();
		screen.get("#interactivevideoremote").show();
	}
}
