var CoverFlowRemoteController = function(options) {}; // needed for detection

var audioPlayer;
var audioQueued = false;

CoverFlowRemoteController.update = function(vars, data){
	if (!vars["loaded"]) {
		console.log("initing");
		resize();
		init();
		startHelp();
		vars["loaded"] = true;		
	}	
}

function startHelp() {
	setTimeout(function(){
		$("#help-text1").hide();
		$("#swipe-animation").hide();
		$("#help-text2").show();
		$("#touch-animation").show();
		setTimeout(function() {
			$("#help-text2").hide();
			$("#touch-animation").hide();
			$("#help-button").show();
		}, 4000);
	}, 5000);
}

$("#help-button").on('touchstart click', function () {
	$("#help-button").hide();
	$("#help-text1").show();
	$("#swipe-animation").show();
	startHelp();
});

//Handling fitting of trackpad
var $wrapper = jQuery('.trackpad-wrapper');
var $trackpad = jQuery('.trackpad');
var ratio = 4 / 3;

function resize() {
	console.log("reszing trackpad");
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

jQuery(window).on('resize', resize);

function init() {
	console.log("init");
	resize();
	
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
	
	hTrackpad.on('swipeleft', function(event){
		console.log("swipe left");
		var message = 'event(trackpad/swiperight,{"id":"trackpad","targetid":"trackpad"})';
		sendMessage(message, true);
	})
	
	hTrackpad.on('swiperight', function(event){
		console.log("swipe right");
		var message = 'event(trackpad/swipeleft,{"id":"trackpad","targetid":"trackpad"})';
		sendMessage(message, true);
	})
	
	hTrackpad.on('doubletap', function(event){
		console.log("double tap");
		  var message = 'event(trackpad/enter,{"id":"trackpad","targetid":"trackpad"})';
		  sendMessage(message, true);
	})
}