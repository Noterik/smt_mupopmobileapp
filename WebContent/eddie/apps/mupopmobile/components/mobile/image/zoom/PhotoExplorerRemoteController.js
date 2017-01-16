var PhotoExplorerRemoteController = function(options) {}; // needed for detection

var $wrapper;
var $trackpad;
var ratio = 4 / 3;
var timeout = null;
var interval = 25;

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
		
		$("#audiop").on("loadedmetadata", loadedMetadata);
		$("#audiop").on("timeupdate", updateTime);


		$("#audiop").on('play', function() {
			$("#play").addClass("fa-pause-circle");
			$("#play").removeClass("fa-play-circle");
		});
	      
		$("#audiop").on('pause', function() {
			$("#play").addClass("fa-play-circle");
			$("#play").removeClass("fa-pause-circle");
		});
		
		$("#play").on('click', function() {
			 if ($("#audiop")[0].paused) {
				 $("#audiop")[0].play();
			 } else {
				 $("#audiop")[0].pause();
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
	}
	
	var command = data['command'];
	var targetId = '#'+data['targetid']; 
		
	if (command == "update") {		
			
	}
};

function loadedMetadata() {
	$("#currenttime").text(formatTime(0));
	$("#totaltime").text(formatTime($("#audiop")[0].duration));
}

function updateTime() {
	$("#currenttime").text(formatTime($("#audiop")[0].currentTime));
	$("#seekbar").val((100 / $("#audiop")[0].duration) * $("#audiop")[0].currentTime);
}

function startHelp() {
	setTimeout(function(){
		$("#help-text1").hide();
		$("#pinch-zoom-animation").hide();
		$("#help-text2").show();
		$("#swipe-animation").show();
		setTimeout(function() {
			$("#help-text2").hide();
			$("#swipe-animation").hide();
			$("#help-button").show();
		}, 4000);
	}, 5000);
}

$("#help-button").on('touchstart click', function () {
	$("#help-button").hide();
	$("#help-text1").show();
	$("#pinch-zoom-animation").show();
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

	var hTrackpad = new Hammer.Manager(trackpad);

	hTrackpad.add( new Hammer.Tap());
	hTrackpad.add( new Hammer.Swipe());
	hTrackpad.add( new Hammer.Pinch());
	//hTrackpad.add( new Hammer.Pan());

	/**
	 * Tap 
	 */
	hTrackpad.on('tap', function(event){
		var data = {
			id: 'photoexplorer-trackpad',
			targetid: 'photoexplorer-trackpad',
			origin: {
				x: 0,
				y: 0
			},
			action: 'scale',
			value: 1.0
		};
			
		scale = 1;
		lastScale = 1;
		
		var message = 'event(photoexplorer-trackpad/pinch, '+JSON.stringify(data)+')';
		sendMessage(message);
	});
	
	/** 
	 * Swipe gestures
	 */
	hTrackpad.on('swipeleft', function(event) {
		event.preventDefault();
		var message = 'event(photoexplorer-trackpad/swiperight,{"id":"photoexplorer-trackpad","targetid":"photoexplorer-trackpad"})';
		sendMessage(message, true);
	});
	
	hTrackpad.on('swiperight', function(event) {
		event.preventDefault();
		var message = 'event(photoexplorer-trackpad/swipeleft,{"id":"photoexplorer-trackpad","targetid":"photoexplorer-trackpad"})';
		sendMessage(message, true);
	});
	
	/**
	 * Handle pinching gesture
	 */
	
	hTrackpad.on('pinchstart', function(event){
		pinching = true;
	});
	
	hTrackpad.on('pinchend', function(event){
		lastScale = scale;
	});
	
	hTrackpad.on('pinchin pinchout', function(event){
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
			id: 'photoexplorer-trackpad',
			targetid: 'photoexplorer-trackpad',
			origin: {
				x: Math.round(xPercentage),
				y: Math.round(yPercentage)
			},
			action: 'scale',
			value: scale
		};
			
		var message = 'event(photoexplorer-trackpad/pinch, '+JSON.stringify(data)+')';
		sendMessage(message);
	});
	
	hTrackpad.on('pinchend', function(event){
		pinching = false;
	});
	
	/*hTrackpad.on('pan', function(event){
		var rect = trackpad.getBoundingClientRect();
		var x = ( pointer.clientX - rect.left ) / (rect.right - rect.left) * 100;
		var y = ( pointer.clientY - rect.top ) / (rect.bottom - rect.top) * 100;
		 
		points.x = x;
		points.y = y;
		
		var data = {
			id: 'photoexplorer-trackpad',
			targetid: 'photoexplorer-trackpad',
			origin: {
				x: Math.round(xPercentage),
				y: Math.round(yPercentage)
			},
			action: 'scale',
			value: scale
		};
	      
		var message = 'event(photoexplorer-trackpad/move, '+JSON.stringify(data)+')';
		sendMessage(message);
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
