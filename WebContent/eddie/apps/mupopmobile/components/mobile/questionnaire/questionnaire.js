var QuestionController = function(options) {}; // needed for detection

QuestionController.update = function(vars, data){
	var targetid = data['target'];
	$("#"+targetid).attr('autoplay',data['autoplay']);
	$("#"+targetid).attr('controls',data['controls']);
	$("#" + targetid).parent().append("<div id=\"notificationbox\" style=\"position: absolute; top: 0;\"></div>");

	console.log(window);

	var action = data['action'];
	console.log(data['action']);
	console.log(data);	
	switch (action) {
	case "questionTime":
		
	}


};


function startTimer(duration, display) {
    var start = Date.now(),
        diff,
        minutes,
        seconds;
    function timer() {
        // get the number of seconds that have elapsed since 
        // startTimer() was called
        diff = duration - (((Date.now() - start) / 1000) | 0);

        // does the same job as parseInt truncates the float
        minutes = (diff / 60) | 0;
        seconds = (diff % 60) | 0;

        minutes = minutes < 10 ? "0" + minutes : minutes;
        seconds = seconds < 10 ? "0" + seconds : seconds;

        display.text(minutes + ":" + seconds); 

        if (diff <= 0) {
            // add one second so that the count down starts at the full duration
            // example 05:00 not 04:59
            start = Date.now() + 1000;
        }
    };
    // we don't want to wait a full second before the timer starts
    timer();
    setInterval(timer, 1000);
}

countDownTimer = function (seconds) {
    display = document.querySelector('#countdown_timer');
    startTimer(seconds, display);
};

startTimer(10);