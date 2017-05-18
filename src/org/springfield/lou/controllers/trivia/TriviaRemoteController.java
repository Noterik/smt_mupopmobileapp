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
package org.springfield.lou.controllers.trivia;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

import org.json.simple.JSONObject;
import org.springfield.fs.FSList;
import org.springfield.fs.FsNode;
import org.springfield.lou.controllers.Html5Controller;
import org.springfield.lou.model.ModelEvent;
import org.springfield.lou.screen.Screen;

public class TriviaRemoteController extends Html5Controller {
	
	String correctanswer = "";
	boolean feedback = false;
	String timeout;

	public TriviaRemoteController() { }
	

	public void attach(String sel) {
		selector = sel;

		JSONObject data = new JSONObject();
		String playername = model.getProperty("@playername");

		// no player name set
		if (playername == null || playername.equals("")) {
			data.put("enter-username", "true");
			screen.get(selector).render(data);
			screen.get("#usernamecheck").on("mouseup", "username", "onUserName", this);
		} else { // player name set, now we can really start
			data.put("username", playername);
			screen.get(selector).render(data);

			FsNode message = new FsNode("message",screen.getId());
			message.setProperty("playername", playername);	
			message.setProperty("command", "join");	
			model.notify("@stationevents/totriviaserver", message);

			model.onNotify("@appstate", "onAppStateChange", this);
		}
	}

	public void onAppStateChange(ModelEvent e) {
		FsNode message = e.getTargetFsNode();
		
		String command = message.getProperty("command");
		System.out.println("CLIENT COMMAND="+command);
		if (command.equals("newquestion")) {
			//only target device that is addressed
			if (!message.getProperty("screenid").equals(screen.getId())) {
				return;
			}
			model.setProperty("@itemid", message.getProperty("itemid")); // why is this needed not in shared space?
			model.setProperty("@itemquestionid", message.getProperty("questionid"));
			if (message.getProperty("feedback").equals("true")) {
				feedback=true;
			} else {
				feedback=false;	
			}
			fillPage();
		} else if (command.equals("remove")) {
			//only target device that is addressed
			if (!message.getProperty("screenid").equals(screen.getId())) {
				return;
			}
			//player didn't answer, ask to join again
			removePlayer();
		} else if (command.equals("timer")) {
			timeout = message.getProperty("timer");
			screen.get("#trivia-timer").html(timeout);
			screen.get("#trivia-feedback-timer").html(timeout);
			System.out.println("CLIENTTIMER="+timeout);
		} else if (command.equals("feedbackstate")) {
			if (message.getProperty("feedback").equals("true")) {
				feedback=true;
			} else {
				feedback=false;	
			}
			fillPage();
		}
	}

	private void fillPage() {
		model.setProperty("@contentrole", "mainapp");

		FsNode item = model.getNode("@item");

		JSONObject data = new JSONObject();
		if (feedback) {
			data.put("feedback","true");
		}
		data.put("timer",timeout);

		String playername = model.getProperty("@playername");
		data.put("username", playername);

		ArrayList<JSONObject> answers = new ArrayList<JSONObject>();

		FsNode questionnode = model.getNode("@itemquestion");

		data.put("question", questionnode.getProperty("question"));
		data.put("duration", questionnode.getProperty("duration"));

		JSONObject a = new JSONObject();

		String a1 = questionnode.getProperty("answer1");
		if (a1 != null && !a1.equals("")) {
			a.put("name", a1);
			a.put("value", "1");
			answers.add(a);
		}
		String a2 = questionnode.getProperty("answer2");
		a = new JSONObject();
		if (a2 != null && !a2.equals("")) {
			a.put("name", a2);
			a.put("value", "2");
			answers.add(a);
		}
		String a3 = questionnode.getProperty("answer3");
		a = new JSONObject();
		if (a3 != null && !a3.equals("")) {
			a.put("name", a3);
			a.put("value", "3");
			answers.add(a);
		}
		String a4 = questionnode.getProperty("answer4");
		a = new JSONObject();
		if (a4 != null && !a4.equals("")) {
			a.put("name", a4);
			a.put("value", "4");
			answers.add(a);
		}

		//answers = shuffleAnswers(answers); 

		data.put("answers", answers);
		
		correctanswer = questionnode.getProperty("correctanswer");
		data.put("correctanswer", correctanswer);
		screen.get(selector).render(data);

		screen.get(".trivia-answer").on("mouseup", "onAnswer", this);
	}

	private void removePlayer() {
		JSONObject data = new JSONObject();
		data.put("player-removed", "true");
		screen.get(selector).render(data);
	}

	public void onAnswer(Screen s, JSONObject data) {
		String id = (String) data.get("id");
		id = id.substring(id.length()-1); //get the last character

		System.out.println("on answer item id = "+model.getProperty("@itemid"));

		FsNode msgnode = new FsNode("msgnode", "1");
		String answer = (String)data.get("id");
		answer = answer.substring(21);
		System.out.println("CORRECT ANSWER="+correctanswer+" ANSWER="+answer);
		if (correctanswer.equals(answer)) {
			msgnode.setProperty("answer","correct");
		} else {
			msgnode.setProperty("answer","false");
		}
		msgnode.setProperty("value", id);
		msgnode.setProperty("playername", model.getProperty("@playername"));
		
		msgnode.setProperty("command", "answer");	
		model.notify("@stationevents/totriviaserver", msgnode);

		JSONObject d = new JSONObject();	
		d.put("command","selectedanswer");
		d.put("answerid", data.get("id"));
		
		screen.get(selector).update(d);
	}

	// Validate and create a user
	public void onUserName(Screen s, JSONObject data) {
		JSONObject response = new JSONObject();

		String username = (String) data.get("username");
		username = username.trim();

		// Check for a valid username that our FS can handle
		// only allow letters, numbers, -, _ and *
		if (!username.matches("^[A-Za-z0-9\\-\\_\\*]{1,15}$")) {
			System.out.println("Invalid username " + username);

			response.put("enter-username", "true");
			response.put("reason", "Invalid username, max 15 characters, only letters, numbers, -, _ and * are allowed");

			screen.get(selector).render(response);
			screen.get("#usernamecheck").on("mouseup", "username", "onUserName", this);

			return;
		}

		FSList players = model.getList("@triviaplayerslist/player");

		if (players.size() > 0) {
			// loop over all players to check if we have already this username
			for (Iterator<FsNode> iter = players.getNodes().iterator(); iter.hasNext();) {
				FsNode player = (FsNode) iter.next();
				String playerName = player.getProperty("name");

				if (playerName.toLowerCase().equals(username.toLowerCase())) {
					System.out.println("Username already taken");

					response.put("enter-username", "true");
					response.put("reason", "Username already taken, please choose another username");

					screen.get(selector).render(response);
					screen.get("#usernamecheck").on("mouseup", "username", "onUserName", this);
					return;
				}
			}
		} else {
			// create players list
			FsNode newPlayerList = new FsNode("players", "list");
			model.putNode("@station", newPlayerList);
		}

		// username available, store and continue
		FsNode newPlayer = new FsNode("player", username.toLowerCase());
		newPlayer.setProperty("name", username);
		newPlayer.setProperty("highscore", "0");

		model.putNode("@triviaplayerslist", newPlayer);
		model.setProperty("@playername", username);

		response.put("username", username);
		screen.get(selector).render(response);

		FsNode message = new FsNode("message",screen.getId());
		message.setProperty("playername", username);
		message.setProperty("command", "join");	
		model.notify("@stationevents/totriviaserver", message);

		model.onNotify("@appstate", "onAppStateChange", this);
	}

	//Randomize answers
	private ArrayList<JSONObject> shuffleAnswers(ArrayList<JSONObject> answers) {
		long seed = System.nanoTime();

		Collections.shuffle(answers, new Random(seed));

		return answers;
	}
}
