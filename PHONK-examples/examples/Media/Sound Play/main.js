/*
 * Phonk Example: Play a Sound
 *
 * Supports wav, mp3, and ogg (recommended) files
 */

ui.addTitle(app.name)

ui.addButton('Play a Meow', 0.1, 0.3, 0.8, 0.5).onClick(function () {
  var player = media.createSoundPlayer()
  player.load('meow.ogg')
  player.play()
})
