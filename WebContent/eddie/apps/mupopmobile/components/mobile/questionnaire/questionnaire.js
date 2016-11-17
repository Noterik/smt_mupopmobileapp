var QuestionController = function(options) {}; // needed for detection

QuestionController.update = function(vars, data){
    

};

function startTimer() {
    console.log("STARTING TIMER");
    var display = $("#question_timer");
    var start = Date.now(),
        diff,
        minutes,
        seconds;
    function timer() {
        console.log("TIMER TICK!!!");
        var duration = $("#question_timer_conainer").attr("data-time")/1000;
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
            diff = 0;
            clearInterval(intervalid);
        }
    };
    // we don't want to wait a full second before the timer starts
    timer();

    intervalid = setInterval(timer, 1000);

}

startTimer();