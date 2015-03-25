/*
* Execute Shell commands  
*   
*
*/


var txt = ui.addText("shell output", 0, 0); 

var e = app.executeCommand("ping www.google.es", function(d) {
   console.log(d);
   txt.setText(d);
});
    
// e.stop();
// e.run();