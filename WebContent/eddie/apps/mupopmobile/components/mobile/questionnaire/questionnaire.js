var QuestionController = function(options) {}; // needed for detection

QuestionController.update = function(vars, data){
    // console.log("QuestionController IS LOADED");
    var targetid = data['target'];
    var action = data['action'];

    console.log("got action: " +action);
    switch (action) {
    case "setCountdown":
        var dur = data['duration'];
        startTimer(dur);
        console.log("duration: " + dur );
        
        break;
    default:
        break;
    }

};

function startTimer(duration) {
    console.log("STARTING TIMER");
    var display = $("#question_timer");
    var start = Date.now(),
        diff,
        minutes,
        seconds;
        duration = parseInt(duration) + 1000; 
    function timer() {
        duration = duration - 1000;
        diff = duration/1000;
        minutes = (diff / 60) | 0;
        seconds = (diff % 60) | 0;

        minutes = minutes < 10 ? "0" + minutes : minutes;
        seconds = seconds < 10 ? "0" + seconds : seconds;

        display.text(minutes + ":" + seconds); 

        if (diff <= 0) {
            diff = 0;
            clearInterval(intervalid);
        }
    };
    // we don't want to wait a full second before the timer starts
    timer();

    intervalid = setInterval(timer, 1000);
}