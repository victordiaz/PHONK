/* 
* 	Voice recognition example 
*	It reads back whatever is recognized 
*	you might have internet connection on certain android versions
*
*/ 

media.voiceRecognition(function(text) { 
    console.log(text);
    media.textToSpeech(text);
});