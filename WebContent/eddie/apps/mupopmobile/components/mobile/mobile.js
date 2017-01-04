var MobileController = function(options) {
}; // needed for detection


function initAudio() {
	console.log("play triggered real");
	$("#audiop").trigger("play");
	$("#mobile").unbind( "click" );
}


MobileController.update = function(vars, data){
	console.log("new mobile command");
	var action = data['action'];
	console.log("action="+action);
	switch (action) {
		case "pause":
			$("#audiop").pause();
			break;
		case "play":
			$("#audiop")[0].src = data['src'];
			$("#audiop").trigger("play");
		}
}

$("#mobile").on("click", initAudio);