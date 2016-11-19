/*
 * \\\ Example:	Video playback
 *
 * video of cityfireflies by uncoded.es
 * Sergio Galan and Victor Diaz
 */

ui.addTitle(app.name)

var video = ui.addVideo('cityfireflies.m4v', 0.1, 0.2, 0.8, 0.35)

video.onLoaded(function() {
  video.play()
})

video.onUpdate(function(ms, totalDuration) {
  // console.log(ms + ' ' + totalDuration)
})


ui.addButton('Play', 0.05, 0.85, 0.45, 0.1).onClick(function() {
  video.play()
})

ui.addButton('Pause', 0.55, 0.85, 0.40, 0.1).onClick(function() {
  video.pause()
})

ui.addSlider(0.05, 0.75, 0.9, 0.05).range(0, 1).onChange(function (e) {
  var pos = e.value * video.getDuration()
  console.log(pos)
  video.seekTo(pos)
})
