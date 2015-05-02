/*
* Show camera view and capture and image when button
* is clicked
* if you are in the editor an image will be shown 
* in the console 
*/ 

//add camera 
var camera = ui.addCameraView("back", 0, 0, 500, 500);

//take a picture and save it 
ui.addButton("Take pic", 0, 500, 500, 100).onClick(function() { 
    camera.takePicture("picture.png", function() {
        console.log('<img src="' + app.servingUrl() + 'picture.png"/>');
    });
});

//toggle flash on and off
ui.addToggle("Flash", 0, 700, 500, 100, false).onChange(function(state) { 
    camera.turnOnFlash(state);
});