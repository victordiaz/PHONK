/* 
 * \\\ Example Serial communication
 *
 * Serial communication
 * Works great with Arduino and similar
 *
 * Your device must support OTG connectivity
 * If you want a quick working setup, 
 * load the Arduino.ino firmware in your Arduino
 */

var dataLabel = ui.addText('', 0, 0, 1, 0.5)

// start serial connection
var serial
ui.addButton('START', 0, 0.5, 0.5, 0.2).onClick(function () {
  
  // connect
  serial = boards.connectSerial(9600, function (connected) {
  	console.log('connected ' + connected)
	})

  // incomming data
	serial.onNewData(function (data) {
		dataLabel.text('Data : '+ data)
	})
})

// stop connection
ui.addButton('STOP', 0.5, 0.5, 0.5, 0.2).onClick(function () {
	serial.stop()
})

// write to the serial led on
ui.addButton('LED ON', 0, 0.7, 0.5, 0.2).onClick(function () {
	serial.write('ledon')
})

// turn off the led
ui.addButton('LED OFF', 0.5, 0.7, 0.5, 0.2).onClick(function () {
	serial.write('ledoff')
})

