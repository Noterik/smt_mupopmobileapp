var AudioTestRemoteController = function(options) {}; // needed for detection

AudioTestRemoteController.update = function(vars, data){		
	$("#audioplayer").on("loadedmetadata", loadedMetadata);
	$("#audioplayer").on("timeupdate", updateTime);
	$("#audioplayer").on('play', function() {
		$("#play").addClass("fa-pause-circle");
		$("#play").removeClass("fa-play-circle");
	});
      
	$("#audioplayer").on('pause', function() {
		$("#play").addClass("fa-play-circle");
		$("#play").removeClass("fa-pause-circle");
	});
	
	$("#play").on('click', function() {
		 if ($("#audioplayer")[0].paused) {
			 $("#audioplayer")[0].play();
		 } else {
			 $("#audioplayer")[0].pause();
		 }
	});
};

function loadedMetadata() {
	$("#currenttime").text(formatTime(0));
	$("#totaltime").text(formatTime($("#audioplayer")[0].duration));
}

function updateTime() {
	$("#currenttime").text(formatTime($("#audioplayer")[0].currentTime));
	$("#seekbar").val((100 / $("#audioplayer")[0].duration) * $("#audioplayer")[0].currentTime);
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