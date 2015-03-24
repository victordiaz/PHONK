/*
*	
* Events
* A simple way to obtain MVC patterns using 
* Protocoder events
*
*/


sensors.accelerometer.onChange(function(x, y, z) {
    var o = {};
    o.x = x;
    o.y = y;
    o.z = z;
    
    app.sendEvent("e1", o);
});


var id = app.listenEvent("e1", function(obj) {
    console.log(obj.x);
});

var txt = ui.addText("", 10, 10);
txt.textSize(85);

var id = app.listenEvent("e1", function(obj) {
    txt.text(Math.round(obj.x * 100) / 100);
});


//app.removeEvent(id);
