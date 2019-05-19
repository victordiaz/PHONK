/*
 * Phonk Example: Livecoding feedback
 *
 * We can execute any line of code whenever we want from the
 * WebIDE, just press
 * Control + Shift + X in Linux or Windows
 * Cmd + Shift + X in Mac to live execute the line
 *
 * Using the following code we can see an overlay in the screen
 */

ui.addTitle(app.name)
ui.addSubtitle('Code can be "live executed" from the WebEditor, selecting a line and pressing Ctrl (Cmd) + Shift + X. \n\nTry it out!')

// adding this we can overlay the lines executed in the app
var l = app.liveCodingOverlay()
						.autoHide(true)
						.textSize(25)
						.write('hello')
						.backgroundColor('#55000055')
						.show(false);

device.vibrate(100) // Execute this line!
