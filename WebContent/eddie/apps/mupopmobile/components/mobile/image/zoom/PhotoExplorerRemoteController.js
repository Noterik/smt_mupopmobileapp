var PhotoExplorerRemoteController = function(options) {}; // needed for detection

var $wrapper;
var $trackpad;
var ratio = 4 / 3;
var timeout = null;

var pinching = false;
var scale = null;
var lastScale = null;

PhotoExplorerRemoteController.update = function(vars, data){		
	//init - this is also handled when returning on a page
	if (!vars["loaded"]) {	
		//Handling fitting of trackpad
		$wrapper = jQuery('.trackpad-wrapper-spotting');
		$trackpad = jQuery('#trackpad');

		init_photoexplorer();
		startHelp();

		vars["loaded"] = true;	
		
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
		
		$("#text").on('click', function() {
			if ($("#trackpad").is(":visible")) {
				$("#trackpad").hide();
				$("#textreader").show();
				$("#text").addClass("fa-square-o");
				$("#text").removeClass("fa-book");
			} else {
				$("#textreader").hide();
				$("#trackpad").show();
				$("#text").addClass("fa-book");
				$("#text").removeClass("fa-square-o");
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
			$("#textreader_text").text(data['text']);
			$("#audioplayer").trigger("load").trigger("play");
			
			$("#audioplayer").on('canplaythrough', audioLoaded);

			// If the video is in the cache of the browser,
			// the 'canplaythrough' event might have been triggered
			// before we registered the event handler.
			if ($("#audioplayer")[0].readyState > 3) {
			  audioLoaded();
			}
		}	 else {
			//triggered same audio again, this prevents infinite spinner
			audioLoaded();
		}	
	}
};

function initAudio() {
	$("#audioplayer").trigger("play");
}

function loadedMetadata() {
	$("#currenttime").text(formatTime(0));
	$("#totaltime").text(formatTime($("#audioplayer")[0].duration));
}

function updateTime() {
	$("#currenttime").text(formatTime($("#audioplayer")[0].currentTime));
	$("#seekbar").val((100 / $("#audioplayer")[0].duration) * $("#audioplayer")[0].currentTime);
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

function audioLoaded() {	
	var message = 'event(audioplayer/loaded,{"id":"audioplayer","targetid":"audioplayer"})';
	sendMessage(message, true);
}

function sendMessage(msg, force){
	if(!timeout || force){
		eddie.putLou('', msg);
		timeout = setTimeout(function(){
			timeout = null;
		}, interval);
	}
}

jQuery(window).on('resize', resize);

function init_photoexplorer() {
	resize();
	
	var hTrackpad = new Hammer(trackpad);
	//var hTrackpad = new Hammer.Manager(trackpad);

	//hTrackpad.add( new Hammer.Pinch());
	//hTrackpad.add( new Hammer.Swipe());
	
	/*hTrackpad.get('pinch').set({ enable: true });*/
	
	hTrackpad.on('swipeleft', function(event) {
		var message = 'event(photoexplorer-trackpad/swiperight,{"id":"photoexplorer-trackpad","targetid":"photoexplorer-trackpad"})';
		sendMessage(message, true);
		event.preventDefault();
	})
	
	hTrackpad.on('swiperight', function(event) {
		var message = 'event(photoexplorer-trackpad/swipeleft,{"id":"photoexplorer-trackpad","targetid":"photoexplorer-trackpad"})';
		sendMessage(message, true);
		event.preventDefault();
	})
	/*
	hTrackpad.on('pinchin', function(event) {
		var message = 'event(photoexplorer-trackpad/pinchin,{"id":"photoexplorer-trackpad","targetid":"photoexplorer-trackpad"})';
		sendMessage(message, true);
		event.preventDefault();
	})
	
	hTrackpad.on('pinchout', function(event) {
		var message = 'event(photoexplorer-trackpad/pinchout,{"id":"photoexplorer-trackpad","targetid":"photoexplorer-trackpad"})';
		sendMessage(message, true);
		event.preventDefault();
	})*/
	
	/**
	 * Handle pinching gesture
	 */
	/*
	hTrackpad.on('pinchstart', function(event){
		//if(getMode() === "transformer")
			pinching = true;
	});
	
	hTrackpad.on('pinchend', function(event){
		lastScale = scale;
	});
	
	hTrackpad.on('pinchin pinchout', function(event){
		//if(getMode() === "transformer"){
			var rect = trackpad.getBoundingClientRect();
			
			var pinchRect = getTouchRect(event.pointers);
			
			var pinchXOrigin = pinchRect.left + (pinchRect.right - pinchRect.left) / 2;
			var pinchYOrigin = pinchRect.top + (pinchRect.bottom - pinchRect.top) / 2;
			
					
			var relativeXOrigin = pinchXOrigin - rect.left;
			var relativeYOrigin = pinchYOrigin - rect.top;
			
			var rectWidth = rect.right - rect.left;
			var rectHeight = rect.bottom - rect.top;
					
			var xPercentage = relativeXOrigin / rectWidth * 100;
			var yPercentage = relativeYOrigin / rectHeight * 100;		
			
			scale = lastScale ? event.scale * lastScale : event.scale;
			
			var data = {
				origin: {
					x: Math.round(xPercentage),
					y: Math.round(yPercentage)
				},
				action: 'scale(' + scale + ')'
			};
			
			var message = "transformSlide(" + JSON.stringify(data) + ")";
			
			sendMessage(message);
		//}
	});
	
	hTrackpad.on('pinchend', function(event){
		//if(getMode() === "transformer")
			pinching = false;
	});*/
}

function getTouchRect(touches){
	var rect = {
		left: null,
		right: null,
		top: null,
		bottom: null
	}
	touches.forEach(function(touch){
		rect.left = rect.left === null || touch.clientX < rect.left ? touch.clientX : rect.left;
		rect.right = rect.right === null || touch.clientX > rect.right ? touch.clientX : rect.right;
		rect.top = rect.top === null || touch.clientY < rect.top ? touch.clientY : rect.top;
		rect.bottom = rect.bottom === null || touch.clientY > rect.bottom ? touch.clientY : rect.bottom;
	});
	
	return rect;
}
