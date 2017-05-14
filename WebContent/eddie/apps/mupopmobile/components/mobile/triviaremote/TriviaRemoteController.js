var TriviaRemoteController = function(options) {}; // needed for detection

TriviaRemoteController.update = function(vars, data){		
	
	var command = data['command'];
	var targetId = '#'+data['targetid']; 
		
	console.log(data);
	
	if (command == "selectedanswer") {	
		$(".trivia-answer").each(function() {
			if (this.id == data['answerid']) {
				$(" > img", this).css("display", "inline");
			} else {
				$(" > img", this).css("display", "none");
			}
		});
	}
};


