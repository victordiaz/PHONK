/*
 * PHONK Example:	Video playback
 */

ui.addTitle(app.name)

var player = media.createVideoPlayer()

player.load('phonk.mkv')
player.onLoaded(function () {
  var playerWidth = 0.8
  var playerHeight = playerWidth / player.getClipAspectRatio() + 'w'
  ui.add(player.getPreview(), 0.1, 0.15, playerWidth, playerHeight)
  player.loop(true)
  player.play()
})

util.loop(500, function () {
  slider.value(player.position() / player.duration())
}).start()


ui.addButton('Play', 0.05, 0.85, 0.45, 0.1).onClick(function() {
  player.play()
})

ui.addButton('Pause', 0.55, 0.85, 0.40, 0.1).onClick(function() {
  player.pause()
})

var slider = ui.addSlider(0.05, 0.75, 0.9, 0.05).range(0, 1).onChange(function (e) {
  var pos = e.value * player.duration()
  player.seekTo(pos)
})
