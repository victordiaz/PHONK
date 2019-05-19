/*
 * Phonk Example: Record and Playback a Sound
 */

ui.addTitle(app.name)

var recorder = media.createRecorder()

var btnRecord = ui.addToggle('Record', 0.1, 0.15, 0.8, 0.35).onChange(function (o) {
  if (o.checked) recorder.record('recording.mp4')
  else recorder.stop()
})

var btnPlay = ui.addButton('Play', 0.1, 0.55, 0.8, 0.35).onClick(function () {
  var player = media.createSoundPlayer()
  player.load('recording.mp4').play()
})
btnPlay.props.background = '#2ee248'
btnPlay.props.backgroundPressed = '#BB2ee248'
