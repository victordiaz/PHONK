/*
 * \\\ Example: Functions
 */

 ui.addTitle(app.name)

var txt = ui.addText('Check the source code to see how functions work', 0.1, 0.15, 0.8, -1)
txt.props.textSize = 30

// define a function with optional parametes
function saySomething (msg) {
  device.vibrate(100)
  ui.toast(msg)
}

// calling the function will execute the code block inside it
saySomething('Hola Mundo!')
