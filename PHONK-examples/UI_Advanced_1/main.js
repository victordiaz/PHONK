/*
*   This is the first of a series containing more advanced UI elements 
*   and layouting 
*
*/

// this line must be at the very beginning of the script
ui.screenMode("immersive");

// forces portrait mode 
ui.screenMode("portrait");

// prevents the app to scroll vertically which is nice when
// you have some inner widgets that might scroll on touch events 
ui.allowScroll(false); 

//adding a card, basically they group rows of n elements 
var card = ui.addCard("label", 0, 0, ui.screenWidth, 200); 
var r = card.addRow(2); // add a row to the card with two slots 
r.addView(ui.newButton("show").onClick(function() { ui.show(pad, true) })); // add firt view
r.addView(ui.newButton("hide").onClick(function() { ui.show(pad, false); })); // add second view

//this is a touchable pad 
var pad = ui.addXYPad(10, 210, ui.screenWidth - 20, 400, function(e) {
    for (var i = 0; i < e.length; i++) {
        console.log(e[i].id + " " + e[i].x + " " + e[i].y);
    }
});



// add an inner absolute layout 
var al = ui.addAbsoluteLayout(10, 620, 520, 220);
al.backgroundColor("#550000FF");

al.addView(ui.newButton("btn1").onClick(function() {
    ui.popupInfo("title", "description", "yes", "no", function(e) {
        console.log("you pressed " + e);
    });
}), 0, 0, 150, 150);

al.addView(ui.newButton("btn2").onClick(function() { 
    ui.move(al, Math.random() * ui.screenWidth, Math.random() * ui.screenHeight);
}), 150, 0, 150, 150);


// gesture detector, in this example we move the pad when 
// scrolling on the added absolute layout 
var x = 0.0;
ui.gestureDetector(al, function(m) {
    console.log(m.type);
    
    if (m.type == "scroll") {
        var m = JSON.parse(m.data).distanceX;
        console.log(m);
        x += m;
        pad.setX(x);
    }
});
