var SelectionMapRemoteController = function(options) {}; // needed for detection

var $wrapper;
var $trackpad;
var ratio = 4 / 3;
var canTap = true;

SelectionMapRemoteController.update = function(vars, data){
	//init - this is also handled when returning on a page
	if (!vars["loaded"]) {	
		//Handling fitting of trackpad
		$wrapper = jQuery('.trackpad-wrapper-spotting');
		$trackpad = jQuery('#trackpad');

		$("#selectionmap-help-button").hide();
		
		init_selection();
		startCoverFlowHelp();

		vars["loaded"] = true;	

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
		$("#move-animation").hide();
		$("#help-text2").show();
		$("#touch-animation").show();
		setTimeout(function() {
			$("#help-text2").hide();
			$("#touch-animation").hide();
			$("#selectionmap-help-button").show();
		}, 3500);
	}, 3500);
}

$("#selectionmap-help-button").on('touchstart click', function () {
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
	
	hTrackpad.on('tap', function(event) {
		console.log("tap detected");
		if (canTap) {
			canTap = false;
			var message = 'event(trackpad/enter,{"id":"trackpad","targetid":"trackpad"})';
			sendMessage(message, true);
			event.preventDefault();
			setTimeout(function() {
				canTap = true;
			}, 1000);
		}
	})
	
	$trackpad.on('mousedown ', function(event){
		console.log("mousedown detected");  
		event.preventDefault();
		var message = 'event(trackpad/enter,{"id":"trackpad","targetid":"trackpad"})';
		sendMessage(message, true);
	});
}