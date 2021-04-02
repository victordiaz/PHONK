/*
 * PHONK Example Serial communication
 *
 * Serial communication
 * Works great with Arduino and similar
 *
 * Your device must support OTG connectivity
 * If you want a quick working setup,
 * load the Arduino.ino firmware in your Arduino
 */

ui.addTitle(app.name)

var dataList = ui.addTextList(0.1, 0.1, 0.8, 0.55).autoScroll(true)
dataList.props.background = '#000000'
dataList.props.textColor = '#FFFFFF'
dataList.props.padding = 30

// start serial connection
var serial = boards.createSerial(19200)

serial.onConnectionStatus(function (e) {
  dataList.add('status: ' + e.status)
})

// incomming data
serial.onNewData(function (e) {
  dataList.add('data: ' + e.data)
})

ui.addToggle(['Connect', 'Disconnect'], 0.1, 0.70, 0.8, 0.1).onChange(function (e) {
  if (e.checked) serial.connect()
  else serial.stop()
})

// write to the serial led on
ui.addButton('LED ON', 0.1, 0.85, 0.35, 0.1).onClick(function () {
  serial.write('ledon\n')
})

// turn off the led
ui.addButton('LED OFF', 0.55, 0.85, 0.35, 0.1).onClick(function () {
  serial.write('ledoff\n')
})
