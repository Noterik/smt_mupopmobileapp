var ZoomAndAudioRemoteController = function(options) {}; // needed for detection

var audioPlayer;
var audioQueued = false;

var $wrapper;
var $trackpad;
var ratio = 4 / 3;

ZoomAndAudioRemoteController.update = function(vars, data){	
	audioPlayer = audioPlayer == undefined ? $("#audioplayer") : audioPlayer;
	
	//init - this is also handled when returning on a page
	if (!vars["loaded"]) {	
		//Handling fitting of trackpad
		$wrapper = jQuery('.trackpad-wrapper-spotting');
		$trackpad = jQuery('#trackpad');

		init_spotting();
		startHelp();

		vars["loaded"] = true;	
		
		audioPlayer.on("loadedmetadata", loadedMetadata);
		audioPlayer.on("timeupdate", updateTime);
		audioPlayer.on('play', function() {
			$("#play").addClass("fa-pause-circle");
			$("#play").removeClass("fa-play-circle");
		});
	      
		audioPlayer.on('pause', function() {
			$("#play").addClass("fa-play-circle");
			$("#play").removeClass("fa-pause-circle");
		});
		
		$("#play").on('click', function() {
			 if (audioPlayer[0].paused) {
				 audioPlayer[0].play();
			 } else {
				 audioPlayer[0].pause();
			 }
		});
		
		//IOS init for automatically playing audio
		$("#trackpad").on("touchstart", initAudio);
	}
	
	var command = data['command'];
	var targetId = '#'+data['targetid']; 
		
	if (command == "update") {		
		if ($("#audiosrc").attr("src") != data['src']) {		
			$("#audiosrc").attr("src", data['src']);
			$("#audioplayer").trigger("load").trigger("play");
		}		
	}
};

function initAudio() {
	$("#audioplayer").trigger("play");
}

function loadedMetadata() {
	$("#currenttime").text(formatTime(0));
	$("#totaltime").text(formatTime(audioPlayer[0].duration));
}

function updateTime() {
	$("#currenttime").text(formatTime(audioPlayer[0].currentTime));
	$("#seekbar").val((100 / audioPlayer[0].duration) * audioPlayer[0].currentTime);
}

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

function formatTime(time) {
	var hours, minutes, seconds;
	
	hours = Math.floor(time / 3600);
	minutes = Math.floor((time % 3600) / 60);
	seconds = Math.floor((time % 60));
	
	formattedTime = "";
	if (hours > 0) {
		if (hours < 10) {
			formattedTime += "0";
		}
		formattedTime += hours+":";
	}
	if (minutes < 10) {
		formattedTime += "0";
	}
	formattedTime += minutes+":";
	if (seconds < 10) {
		formattedTime += "0";
	}
	formattedTime += seconds;
	
	return formattedTime;
}

jQuery(window).on('resize', resize);

function init_spotting() {
	resize();
}
