/* 
*	Uses android text to speech 
*/ 

ui.addButton("Speak!", 0, 0, 500, 200).onClick(function() { 
	media.textToSpeech("hola amigos"); 
});