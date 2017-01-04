var CoverFlowRemoteController = function(options) {}; // needed for detection

var $wrapper;
var $trackpad;
var ratio = 4 / 3;
var canTap = true;

CoverFlowRemoteController.update = function(vars, data){
	//init - this is also handled when returning on a page
	if (!vars["loaded"]) {	
		//Handling fitting of trackpad
		$wrapper = jQuery('.trackpad-wrapper-selection');
		$trackpad = jQuery('#coverflow-trackpad');

		init_selection();
		startCoverFlowHelp();

		vars["loaded"] = true;	
		
		$("#coverflow-audioplayer").on("loadedmetadata", loadedMetadataCoverflow);
		$("#coverflow-audioplayer").on("timeupdate", updateTimeCoverflow);
		$("#coverflow-audioplayer").on('play', function() {
			$("#coverflow-play").addClass("fa-pause-circle");
			$("#coverflow-play").removeClass("fa-play-circle");
		});
	      
		$("#coverflow-audioplayer").on('pause', function() {
			$("#coverflow-play").addClass("fa-play-circle");
			$("#coverflow-play").removeClass("fa-pause-circle");
		});
		
		$("#coverflow-play").on('click', function() {
			 if ($("#coverflow-audioplayer")[0].paused) {
				 $("#coverflow-audioplayer")[0].play();
			 } else {
				 $("#coverflow-audioplayer")[0].pause();
			 }
		});
		
		
		$("#coverflow-text").on('click', function() {
			if ($("#coverflow-trackpad").is(":visible")) {
				$("#coverflow-trackpad").hide();
				$("#coverflow-textreader").show();
				$("#coverflow-text").addClass("fa-square-o");
				$("#coverflow-text").removeClass("fa-book");
			} else {
				$("#coverflow-textreader").hide();
				$("#coverflow-trackpad").show();
				$("#coverflow-text").addClass("fa-book");
				$("#coverflow-text").removeClass("fa-square-o");
			}
		});
		
		/*prevent zooming on double tap for ios 10 because:
		
			"To improve accessibility on websites in Safari, 
			users can now pinch-to-zoom even when a website sets user-scalable=no 
			in the viewport."
		
		*/
		document.documentElement.addEventListener('touchmove', function (event) {
		    event.preventDefault();      
		}, false);
	}
}

function startCoverFlowHelp() {
	setTimeout(function(){
		$("#help-text1").hide();
		$("#swipe-animation").hide();
		$("#help-text2").show();
		$("#touch-animation").show();
		setTimeout(function() {
			$("#help-text2").hide();
			$("#touch-animation").hide();
			$("#coverflow-help-button").show();
		}, 4000);
	}, 5000);
}

$("#coverflow-help-button").on('touchstart click', function () {
	$("#coverflow-help-button").hide();
	$("#help-text1").show();
	$("#swipe-animation").show();
	startCoverFlowHelp();
});

function resize_trackpad_selection() {
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

jQuery(window).on('resize', resize_trackpad_selection);

function init_selection() {
	resize_trackpad_selection();
	
	var $trackpad = jQuery('.trackpad');
	var trackpad = $trackpad[0];
	var tracking = false;
	
	var points = {x: 0, y: 0};
	var timeout = null; 
	var interval = 50;
	
	var hTrackpad = new Hammer(trackpad);
	
	function sendMessage(msg, force){
		if(!timeout || force){
			eddie.putLou('', msg);
			timeout = setTimeout(function(){
				timeout = null;
			}, interval);
		}
	}
	
	hTrackpad.on('swipeleft', function(event) {
		var message = 'event(coverflow-trackpad/swiperight,{"id":"coverflow-trackpad","targetid":"coverflow-trackpad"})';
		sendMessage(message, true);
		event.preventDefault();
	})
	
	hTrackpad.on('swiperight', function(event) {
		var message = 'event(coverflow-trackpad/swipeleft,{"id":"coverflow-trackpad","targetid":"coverflow-trackpad"})';
		sendMessage(message, true);
		event.preventDefault();
	})
	
	hTrackpad.on('tap', function(event) {
		if (canTap) {
			canTap = false;
			var message = 'event(coverflow-trackpad/enter,{"id":"coverflow-trackpad","targetid":"coverflow-trackpad"})';
			sendMessage(message, true);
			event.preventDefault();
			setTimeout(function() {
				canTap = true;
			}, 1000);
		}
	})
}

function loadedMetadataCoverflow() {
	$("#coverflow-currenttime").text(formatTime(0));
	$("#coverflow-totaltime").text(formatTime($("#coverflow-audioplayer")[0].duration));
}

function updateTimeCoverflow() {
	$("#coverflow-currenttime").text(formatTime($("#coverflow-audioplayer")[0].currentTime));
	$("#coverflow-seekbar").val((100 / $("#coverflow-audioplayer")[0].duration) * $("#coverflow-audioplayer")[0].currentTime);
}