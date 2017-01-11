var AudioTestRemoteController = function(options) {}; // needed for detection

AudioTestRemoteController.update = function(vars, data){		
	
	
	$("#audiotest-audioplayer").on("loadedmetadata", loadedMetadataAudioTest);
	$("#audiotest-audioplayer").on("timeupdate", updateTimeAudioTest);
	
	$("#audiotest-audioplayer").on('play', function() {
		$("#audiotest-play").addClass("fa-pause-circle");
		$("#audiotest-play").removeClass("fa-play-circle");
	});
      
	$("#audiotest-audioplayer").on('pause', function() {
		$("#audiotest-play").addClass("fa-play-circle");
		$("#audiotest-play").removeClass("fa-pause-circle");
	});
	
	$("#audiotest-play").on('click', function() {
		 if ($("#audiotest-audioplayer")[0].paused) {
			 $("#audiotest-audioplayer")[0].play();
		 } else {
			 $("#audiotest-audioplayer")[0].pause();
		 }
	});
};

function loadedMetadataAudioTest() {
	$("#audiotest-audioplayer")[0].currentTime = 0;
	$("#audiotest-currenttime").text(formatTime(0));
	$("#audiotest-totaltime").text(formatTime($("#audiotest-audioplayer")[0].duration));
}

function updateTimeAudioTest() {
	$("#audiotest-currenttime").text(formatTime($("#audiotest-audioplayer")[0].currentTime));
	$("#audiotest-seekbar").val((100 / $("#audiotest-audioplayer")[0].duration) * $("#audiotest-audioplayer")[0].currentTime);
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