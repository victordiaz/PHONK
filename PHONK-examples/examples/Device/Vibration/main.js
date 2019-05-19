/*
 * Phonk Example: Vibration
 */

ui.addTitle(app.name)

ui.addButton('brbrbrbrbrbrrr', 0.1, 0.2, 0.8, 0.7).onClick(function () {
  device.vibrate(500)
})
