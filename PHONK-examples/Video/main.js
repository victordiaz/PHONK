/* 
*	Plays a video located in the same folder 
*   video of cityfireflies by uncoded.es 
*   Sergio Galan and Victor Diaz 
*/ 

var video = ui.addVideo("cityfireflies.m4v", 50, 50, 480, 360);

video.onLoaded(function() {
    video.play();    
});

video.onUpdate(function(ms, totalDuration) { 
    console.log(ms + " " + totalDuration);
});

ui.addButton("Play", 0, 500, ui.screenWidth, 100).onClick(function() { 
    video.play();
});

ui.addButton("Pause", 0, 600, ui.screenWidth, 100).onClick(function() { 
    video.pause();
}); 

ui.addSlider(0, 750, ui.screenWidth, 100, 100, 0).onChange(function(val) {
    var pos = val * video.getDuration() / 100;
    console.log(pos);
    video.seekTo(pos); 
});