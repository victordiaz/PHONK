/*
*	Timer Executes a task repeatedly 
*   Delay Delays a function for a especific time 
*/

var txt = ui.addText("", 10, 10, ui.screenWidth - 20, ui.screenHeight);

var l1 = util.loop(1000, function () { 
    txt.append("repeating every 1000 ms \n");
}).start(); 

//this is how you stop a looper 
l1.stop();

var l2 = util.loop(5000, function () { 
    txt.append("repeating every 5000 ms \n");
}).start();

//this is how you change the speed of the looper
l2.speed(5000);

util.delay(1000, function() {
   txt.append("delayed 1000 ms \n"); 
});

util.delay(2000, function() {
   txt.append("delayed 2000 ms \n"); 
}); 