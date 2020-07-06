/*
 * Phonk Example Serial communication
 *
 * Serial communication
 * Works great with Arduino and similar
 *
 * Your device must support OTG connectivity
 * If you want a quick working setup,
 * load the Arduino.ino firmware in your Arduino
 */

ui.addTitle(app.name)

var dataList = ui.addTextList(0.1, 0.2, 0.8, 0.45)
dataList.props.background = "#000000"
dataList.props.textColor = "#FFFFFF"

// start serial connection
var serial = boards.createSerial(9600)

serial.onConnected(function (e) {
	dataList.add('connected ' + e.status)
})

// incomming data
serial.onNewData(function (e) {
	dataList.add('Data: '+ e.data)
})


ui.addButton('START', 0.1, 0.70, 0.35, 0.1).onClick(function () {
  serial.start()
})

// stop connection
ui.addButton('STOP', 0.55, 0.70, 0.35, 0.1).onClick(function () {
	serial.stop()
})

// write to the serial led on
ui.addButton('LED ON', 0.1, 0.85, 0.35, 0.1).onClick(function () {
	serial.write('ledon')
})

// turn off the led
ui.addButton('LED OFF', 0.55, 0.85, 0.35, 0.1).onClick(function () {
	serial.write('ledoff')
})
