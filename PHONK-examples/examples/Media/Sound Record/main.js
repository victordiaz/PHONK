/*
 * \\\ Example: Record and Playback a Sound
 *
 */

ui.addTitle(app.name)

var btnRecord = ui.addButton('Record', 0.1, 0.15, 0.8, 0.35).onClick(function () {
  media.recordSound('recording.mp4', true)
})
btnRecord.props.background = '#e22e2e'
btnRecord.props.backgroundPressed = '#BBe22e2e'


var btnPlay = ui.addButton('Play', 0.1, 0.55, 0.8, 0.35).onClick(function () {
  media.playSound('recording.mp4')
})
btnPlay.props.background = '#2ee248'
btnPlay.props.backgroundPressed = '#BB2ee248'
