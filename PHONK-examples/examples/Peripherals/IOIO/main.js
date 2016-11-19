/*
 * \\\ Example: IOIO board
 *
 * First pair your IOIO board via bluetooth
 * using the pin 4545
 */

var led
var input
var ioioConnected = false
var ioio

// connect the ioio
ui.addButton('Start ioio', 0, 0.5, 0.5, 0.2).onClick(function () {
  ioio = boards.startIOIO(function () {
    // this function is executed when the ioio board is ready
    led = ioio.openDigitalOutput(0)
    input = ioio.openAnalogInput(31)
    device.vibrate(500)
    ioioConnected = true
  })
})

util.loop(500, function() {
  if(ioioConnected == true) {
    console.log('the reading is ' + input.getVoltage())
  }
})

// power off the ioio
ui.addButton('Stop ioio', 0.5, 0.5, 0.5, 0.2).onClick(function () {
  if (ioioConnected == true) {
    ioio.stop()
    ioioConnected = false
  }
})

ui.addButton('On', 0, 0.7, 0.5, 0.2).onClick(function () {
  if (ioioConnected == true) led.write(false)
})

ui.addButton('Off', 0.5, 0.7, 0.5, 0.2).onClick(function () {
  if (ioioConnected == true) led.write(true)
})
