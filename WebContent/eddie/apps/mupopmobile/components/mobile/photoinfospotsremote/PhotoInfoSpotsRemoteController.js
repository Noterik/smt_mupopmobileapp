var PhotoInfoSpotsRemoteController = function(options) {}; // needed for detection

PhotoInfoSpotsRemoteController.update = function(vars, data){
	console.log(data);
	
	var command = data['command'];
	var targetId = '#'+data['targetid']; 

	if (command == "update") {		
			
	}
};
