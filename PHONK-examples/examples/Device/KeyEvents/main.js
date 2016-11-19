/*
 * \\\ Example: KeyEvents
 */

ui.addTitle(app.name)

device.onKeyUp(function (data) {
  log(data.key)
})

device.onKeyDown(function (data) {
  log(data.key)
})

device.onKeyEvent(function (data) {
  log(data.key)
})

var txt = ui.addText('Press a Key on your device or in an attached controller (BT / USB) ', 0.1, 0.15, 0.8, -1)
txt.props.textSize = 50

function log(msg) {
  txt.text('Key Pressed: ' + msg)
  console.log(msg)
}
