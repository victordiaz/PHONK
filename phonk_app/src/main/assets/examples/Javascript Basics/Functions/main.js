/*
 * \\\ Example: Functions
 */

ui.addTitle(app.name)
ui.addSubtitle('Check the source code to see how functions work')

// define a function with optional parametes
function saySomething (msg) {
  device.vibrate(100)
  ui.toast(msg)
}

// calling the function will execute the code block inside it
saySomething('Hola Mundo!')
