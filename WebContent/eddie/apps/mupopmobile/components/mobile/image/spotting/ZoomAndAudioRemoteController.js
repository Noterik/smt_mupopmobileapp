var ZoomAndAudioRemoteController = function(options) {}; // needed for detection

var audioPlayer;
var audioQueued = false;

ZoomAndAudioRemoteController.update = function(vars, data){
	console.log(data);
	
	var command = data['command'];
	var targetId = '#'+data['targetid']; 
	
	audioPlayer = audioPlayer == undefined ? $(targetId).find("audio") : audioPlayer;
	
	if (command == "update") {		
		if (!audioQueued && $($(targetId).find("audio > source")[0]).attr("src") != data['src']) {
			console.log("no audio queued or different src  detected");
			$($(targetId).find("audio > source")[0]).attr("src", data['src']);
			
			audioQueued = true;
			$("#trackpad").on("touchend.photoinfospots", playAudio());
		}		
	}
};

function playAudio() {
	console.log("play audio function called");
	$("#trackpad").off("touchend.photoinfospots");
	
	if (audioQueued) {		
		console.log("playing audio");
		//audioPlayer[0].pause();
		audioPlayer[0].load();
		
		audioPlayer[0].oncanplaythrough = audioPlayer[0].play();
		audioQueued = false;
	}
}

startHelp();

function startHelp() {
	setTimeout(function(){
		$("#help-text").hide();
		$("#move-pointer-animation").hide();
		$("#help-button").show();
	}, 7500);
}

$("#help-button").on('touchstart click', function () {
	$("#help-button").hide();
	$("#help-text").show();
	$("#move-pointer-animation").show();
	startHelp();
});

//Handling fitting of trackpad
var $wrapper = jQuery('.trackpad-wrapper');
var $trackpad = jQuery('.trackpad');
var ratio = 4 / 3;

function resize(){
	var maxWidth = $wrapper.width();
	var maxHeight = $wrapper.height();
	
	var width = maxWidth;
	var height = maxHeight;
	
	if(width > height){
		width = height * ratio;
		while(width > maxWidth){
			width--;
			height = width / ratio;
		}
	}else if(height > width){
		height = width / ratio;
		while(height > maxHeight){
			height--;
			width = height * ratio;
		}	
	}	
	
	$trackpad.width(width);
	$trackpad.height(height);
	
	var wRemain = maxWidth - width;
	var hRemain = maxHeight - height;
			
	$trackpad.css('top', hRemain / 2);
	$trackpad.css('left', wRemain / 2);
}

resize();
jQuery(window).on('resize', resize);


