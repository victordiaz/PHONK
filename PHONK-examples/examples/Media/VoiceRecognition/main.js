/*
 * Phonk Example: Voice recognition
 *
 */

ui.addTitle(app.name)
ui.addSubtitle('Using the Android built-in voice recognizer\n' +
              'it will recognize what you say it and play it back.\n' +
              'The voice recognizer depends on your Android vendor (Google, Huawei, Samsung...) and for some you might have internet connection.')

ui.addButton('Press to start the recognizer', 0.1, 0.35, 0.8, 0.4).onClick(function (e) {
  media.startVoiceRecognition(function (e) {
    console.log(e)
    txt.text('You said: ' + e.results[0])
  })
})

var txt = ui.addText('', 0.1, 0.8, 0.8, 0.2)
