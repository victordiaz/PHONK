/*
 * \\\ Example: Voice recognition
 *
 *	Using the Android built in voice recognizer
 *  it will show and play back the recognized text
 *
 *	You might have internet connection on certain Android devices
 */

ui.addTitle(app.name)

ui.addButton('Recognize voice', 0.2, 0.3, 0.6, 0.4).onClick(function (e) {

  media.voiceRecognition(function (text) {
    console.log(text)
    media.textToSpeech(text)
    txt.text('You said: ' + text)
  })

})

var txt = ui.addText('Press to start the recognizer', 0.1, 0.8, 0.8, 0.2)
