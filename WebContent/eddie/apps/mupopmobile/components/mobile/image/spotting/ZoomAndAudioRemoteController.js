var ZoomAndAudioRemoteController = function(options) {}; // needed for detection

var audioPlayer;
var audioQueued = false;

ZoomAndAudioRemoteController.update = function(vars, data){
	console.log(data);
	
	var command = data['command'];
	var targetId = '#'+data['targetid']; 
	
	audioPlayer = audioPlayer == undefined ? $(targetId+" > audio") : audioPlayer;
	
	if (command == "update") {		
		if (!audioQueued && $($(targetId+" > audio > source")[0]).attr("src") != data['src']) {
			console.log("no audio queued or different src  detected");
			$($(targetId+" > audio > source")[0]).attr("src", data['src']);
			
			audioQueued = true;
			$("#photoinfospotsremote_trackarea").on("touchend.photoinfospots", playAudio());
		}		
	}
};

function playAudio() {
	console.log("play audio function called");
	$("#photoinfospotsremote_trackarea").off("touchend.photoinfospots");
	
	if (audioQueued) {		
		console.log("playing audio");
		//audioPlayer[0].pause();
		audioPlayer[0].load();
		
		audioPlayer[0].oncanplaythrough = audioPlayer[0].play();
		audioQueued = false;
	}
}

