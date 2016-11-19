/*
 * \\\ Example:	Text To Speech
 *
 * Speak using the system's TextToSpeech
 */

ui.addTitle(app.name)

ui.addButton('Speak!', 0.1, 0.15, 0.8, -1).onClick(function () {
	media.textToSpeech('Hello potato')
})
