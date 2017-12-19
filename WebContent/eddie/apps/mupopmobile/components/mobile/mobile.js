var MobileController = function(options) { }; // needed for detection

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
			$("#audiop").trigger("pause");
			break;
		case "play":
			$("#audiop")[0].src = data['src'];
			$("#audiop").trigger("play");
			break;
		case "playonnew": 
			if ($("#audiop")[0].src != data['src']) {		
				$("#audiop")[0].src = data['src'];
				$("#audiop").trigger("play");
			}
			break;
		case "load":
			$("#audiop")[0].src = data['src'];
			$("#audiop").trigger("load");
			break;
		case "wantedtime":
			var duration = $("#audiop")[0].duration;
			var curtime = (new Date).getTime();

		    var wt = data['wantedtime'].split(',');
		    //var endsAt = data['endsAt'] + 1000;
		    var realtime = wt[0];
		    var streamtime = wt[1];
			
			var timeDif = realtime - curtime;


			var audiotime = $("#audiop")[0].currentTime*1000;
			var curtime = (new Date()).getTime();
			curtime = curtime - eddie.getTimeOffset(); 
			console.log("curtime - offset = "+curtime);
			// console.log("----------------------");

			// console.log("realtime = " + realtime);
			// console.log("streamtime = " + streamtime);

			realtime = parseInt(realtime);
			streamtime = parseInt(streamtime);

			// console.log("int realtime = " + realtime);
			// console.log("int streamtime = " + streamtime);

			// console.log("curtime = "+curtime);
			curtime = parseInt(curtime);
			// console.log("int curtime = "+curtime);
			// console.log("audiotime= " + audiotime);

			// so in realtime-curtime we want to be at streamtime !
			var timegap = realtime-curtime;
			
			// console.log("timegap = " + timegap);
			// where will we really be ?
			var expectedtime = audiotime + timegap;

			
			// so how far are we off ?
			var delta = streamtime - expectedtime;
			
			 console.log('RT='+realtime+' ST='+streamtime+' CT='+curtime+' AT='+audiotime+' TG='+timegap+' ET='+expectedtime+" DELTA="+delta);
			
			// lets act on it 
			if (delta<-250 || delta>250) {
				var newtime = ((audiotime+delta)+200)/1000;
				console.log('seekto='+newtime+' duration='+duration);
				$("#audiop")[0].currentTime = newtime;
	
			} else {
				
			    var speedup = 1;
			    if (delta<0) {
			    	// console.log('neg='+delta);
			    	if (delta<-200) {
						speedup = 0.97;
					} else if (delta<-100) {
						speedup = 0.98;
					} else if (delta<-50) {
						speedup = 0.99;
					} else {
						speedup = 1;
					}
			    } else {    
			    	// console.log('pos='+delta);
					if (delta<50) {
						speedup = 1.00;
					} else if (delta<200) {
						speedup = 1.01;
					} else if (delta<400) {
						speedup = 1.02;
					} else {
						speedup = 1.03;
					}
				}
				
			    console.log('speedup='+speedup);
			   // so if are within 500ms lets speedup and see if we can catch it
			   //$("#audiop")[0].playbackRate = speedup;
			   
			}		
			break;
		case "seek":
			// console.log('s='+data['seekingvalue'] / 1000);
			$("#"+targetid)[0].currentTime = data['seekingvalue'] / 1000;
			break;
		case "autoplay":
			$("#"+targetid)[0].autoplay = true;
			break;
		case "volumechange":
			$("#"+targetid)[0].volume  = data['volume'] / 100;
			break;
		case "newvideo":
			$("#videosrc").attr("src","http://imxxxx.noterik.com/videoremote/"+data['mp4']+".mp4");
			$("#"+targetid)[0].load();
			// console.log('mp4='+data['mp4']);
			break;
		default:
			break;
	}
}

$("#mobile").on("click", initAudio);