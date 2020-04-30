/*
 * Phonk Example: Voice recognition
 *
 */

ui.addTitle(app.name)
ui.addSubtitle('Using the Android built-in voice recognizer\n' +
              'it will show and play back the recognized text' +
              'You might have internet connection on certain Android devices')

ui.addButton('Press to start the recognizer', 0.1, 0.35, 0.8, 0.4).onClick(function (e) {
  media.startVoiceRecognition(function (e) {
    console.log(e)
    txt.text('You said: ' + e.results[0])
  })
})

var txt = ui.addText('', 0.1, 0.8, 0.8, 0.2)
