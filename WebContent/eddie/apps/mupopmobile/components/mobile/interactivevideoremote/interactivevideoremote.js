var InteractiveVideoRemote = function(options) {}; // needed for detection


InteractiveVideoRemote.update = function(vars, data){
	// get out targetid from our local vars
	var targetid = data['target'];
	$("#"+targetid).attr('autoplay',data['autoplay']);
	$("#"+targetid).attr('controls',data['controls']);
	$("#" + targetid).parent().append("<div id=\"notificationbox\" style=\"position: absolute; top: 0;\"></div>");

	//console.log(window);

	var action = data['action'];
	console.log(data['action']);
	console.log(data);	
	switch (action) {
	case "pause":
		$("#"+targetid)[0].pause();
		break;
	case "play":
		$("#"+targetid)[0].play();
		break;
	case "wantedtime":
		var duration = $("#audiop")[0].duration;
		var curtime = (new Date).getTime();


	    var wt = data['wantedtime'].split(',');
	    var realtime = wt[0];
	    var streamtime = wt[1];
		
		var timeDif = realtime - curtime;
		var remainingTime = duration - (streamtime - timeDif)/1000;

		console.log("Remaining Time:: " + remainingTime);
		setDisplayTime(remainingTime);

		var audiotime = $("#audiop")[0].currentTime*1000;
		var curtime = (new Date()).getTime();
		curtime = curtime - eddie.getTimeOffset(); 
		// console.log("curtime - offset = "+curtime);
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
		
		// console.log('RT='+realtime+' ST='+streamtime+' CT='+curtime+' AT='+audiotime+' TG='+timegap+' ET='+expectedtime+" DELTA="+delta);
		
		// lets act on it 
		if (delta<-1000 || delta>1000) {
			var newtime = ((audiotime+delta)+200)/1000;
			// console.log('seekto='+newtime);
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
			
			// console.log('speedup='+speedup);
		   // so if are within 500ms lets speedup and see if we can catch it
		   $("#audiop")[0].playbackRate = speedup;
		   
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
		$("#videosrc").attr("src","http://images1.noterik.com/videoremote/"+data['mp4']+".mp4");
		$("#"+targetid)[0].load();
		// console.log('mp4='+data['mp4']);
		break;
	default:
		break;
	}
};

function setDisplayTime(duration) {
    // get the number of seconds that have elapsed since 
    // startTimer() was called
    diff = duration;

    // does the same job as parseInt truncates the float
    minutes = (diff / 60) | 0;
    seconds = (diff % 60) | 0;

    minutes = minutes < 10 ? "0" + minutes : minutes;
    seconds = seconds < 10 ? "0" + seconds : seconds;
    console.log("updating time for counter");
    $("#audio_counter").text(minutes + ":" + seconds); 

    if (diff <= 0) {
        // add one second so that the count down starts at the full duration
        // example 05:00 not 04:59
        start = Date.now() + 1000;
    }
};
