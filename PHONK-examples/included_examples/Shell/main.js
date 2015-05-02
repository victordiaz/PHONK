/*
* Execute Shell commands  
*   
*
*/


var txt = ui.addText("shell output", 0, 0); 

var cmd = "ping -c 1 www.google.com";

var e = app.executeCommand(cmd, function(d) {
   console.log(d);
   txt.setText(d);
}).start();
    
// e.stop();