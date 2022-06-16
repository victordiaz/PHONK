/*
 * PHONK Example IOIO board
 *
 * Make sure that you use the latest IOIO firmware
 * If you use the BT connection use the pin 4545
 */

var led
var input

var ioio = boards.createIOIO()

  // this function is executed when the ioio board is ready
ioio.onConnect(function () {
  console.log('connected')
  led = ioio.openDigitalOutput(0)
  input = ioio.openAnalogInput(31)
  device.vibrate(500)
})

// connect
ui.addButton('Start IOIO', 0.1, 0.5, 0.35, 0.2).onClick(function () {
  ioio.connect()
})

// stop connection
ui.addButton('Stop IOIO', 0.55, 0.5, 0.35, 0.2).onClick(function () {
  ioio.stop()
})

// turn on / off LED
ui.addToggle(['LED ON', 'LED OFF'], 0.1, 0.75, 0.8, 0.18).onChange(function (e) {
  led.write(e.checked)
})
