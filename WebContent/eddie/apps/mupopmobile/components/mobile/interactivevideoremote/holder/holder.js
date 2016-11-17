var InteractiveVideoHolderController = function(options) {}; // needed for detection


function initAudio() {
	console.log("play triggered");
	$("#audiop").trigger("play");
	$("#audiop")[0].muted = true;
}

$("#mobile").unbind( "click" );
$("#mobile").on("click", initAudio);