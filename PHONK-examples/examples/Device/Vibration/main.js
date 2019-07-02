/*
 * Phonk Example: Vibration
 */

ui.addTitle(app.name)

ui.addButton('brrrrrr', 0.1, 0.2, 0.8, 0.3).onClick(function () {
  device.vibrate(500)
})

ui.addButton('shhbrbrbrshhhbrbrbr', 0.1, 0.6, 0.8, 0.3).onClick(function () {
  var timeLongSilence = 500
  var timeSilence = 100
  var timeVibration = 50
  var numberOfRepetitions = -1 // if -1 it does only once

  device.vibrate([
    timeLongSilence, timeVibration,
    timeSilence, timeVibration,
    timeSilence, timeVibration,
    timeLongSilence, timeVibration,
    timeSilence, timeVibration,
    timeSilence, timeVibration
  ], numberOfRepetitions)
})