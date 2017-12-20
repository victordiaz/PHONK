/*
 * \\\ Example:	Text To Speech
 *
 * Speak using the system's TextToSpeech
 */

ui.addTitle(app.name)

var tts = media.createTextToSpeech() // initialize te TTS engine

ui.addButton('Speak!', 0.1, 0.15, 0.8, -1).onClick(function () {
	tts.speak('Hello potato')
})
