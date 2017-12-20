/*
 * \\\ Example:	Video playback
 *
 * video of cityfireflies by uncoded.es
 * Sergio Galan and Victor Diaz
 */

ui.addTitle(app.name)

var player = media.createVideoPlayer()
ui.add(player.getPreview(), 0.1, 0.15, 0.8, 0.4)

player.load('cityfireflies.m4v')
player.play()

util.loop(500, function () {
  console.log(player.position() + ' of ' + player.duration())
}).start()


ui.addButton('Play', 0.05, 0.85, 0.45, 0.1).onClick(function() {
  player.play()
})

ui.addButton('Pause', 0.55, 0.85, 0.40, 0.1).onClick(function() {
  player.pause()
})

ui.addSlider(0.05, 0.75, 0.9, 0.05).range(0, 1).onChange(function (e) {
  var pos = e.value * player.duration()
  console.log(pos)
  player.seekTo(pos)
})
