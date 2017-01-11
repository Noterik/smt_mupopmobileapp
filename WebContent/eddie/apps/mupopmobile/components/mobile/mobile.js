var MobileController = function(options) {
}; // needed for detection

function initAudio() {
	//we only need the init to be triggered once
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
			break;
		case "load":
			$("#audiop")[0].src = data['src'];
			$("#audiop").trigger("load");
			break;
	}
}

$("#mobile").on("click", initAudio);