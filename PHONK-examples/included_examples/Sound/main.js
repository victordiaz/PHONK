/* 
*	Play a sound, supports wav, mp3, and ogg files 
*	although ogg is highly recommended 
*/ 

media.volume(100);
ui.enableVolumeKeys(true);

ui.addButton("Meow", 0, 0, 500, 200).onClick(function() { 
	media.playSound("meow.ogg");
});

ui.addButton("Record", 0, 200, 500, 200).onClick(function() {
	media.recordAudio("recording.mp4", true);
})