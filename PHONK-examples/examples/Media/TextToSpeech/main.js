/*
 * Phonk Example:	Text To Speech
 *
 * Speak using the system's TextToSpeech
 */

ui.addTitle(app.name)

var tts = media.createTextToSpeech() // initialize te TTS engine

ui.addButton('Speak!', 0.1, 0.3, 0.8, 0.5).onClick(function () {
  tts.speak('yoyoyo lalala lololo')
})
