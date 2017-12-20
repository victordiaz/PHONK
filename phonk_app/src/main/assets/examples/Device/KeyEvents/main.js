/*
 * \\\ Example: KeyEvents
 */

ui.addTitle(app.name)
ui.addSubtitle('Press a Key on your device or in an attached controller (BT / USB)')

device.onKeyUp(function (data) {
  log(data.key)
})

device.onKeyDown(function (data) {
  log(data.key)
})

device.onKeyEvent(function (data) {
  log(data.key)
})

var txt =  ui.addText(0.1, 0.35, 0.8, -1)
txt.props.textSize = 50

function log(msg) {
  txt.text('Key Pressed: ' + msg)
  console.log(msg)
}
