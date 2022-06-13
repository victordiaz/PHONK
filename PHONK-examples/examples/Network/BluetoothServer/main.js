/*
 *  PHONK Example: BluetoothServer
 *  It works with just one connected device, if you want to handle more you will have
 *  to adjust the code
 */

ui.addTitle(app.name)
ui.addSubtitle('Start a bluetooth "Chat" server')

var txt = ui.addTextList(0.1, 0.25, 0.8, 0.3).autoScroll(true)
txt.props.textSize = 15

ui.addToggle('Start Bluetooth Server', 0.1, 0.72, 0.80, 0.1).onChange(function (e) {
  if (e.checked) {
    btServer.start()
    txt.add('Server waiting for connections...')
  } else {
    btServer.stop()
    txt.add('Server stopped')
  }
})

// send bluetooth messages
var input = ui.addInput(0.1, 0.85, 0.58, 0.1).hint('message')
var send = ui.addButton('Send', 0.7, 0.85, 0.2, 0.1).onClick(function () {
  connectedDevice.send(input.text() + '\n')
})

var btServer = network.bluetooth.createServer('PHONK_CHAT')
var connectedDevice

btServer.onNewConnection(function (e) {
  txt.add('new connection from ' + e.device.name + ' ' + e.device.mac)
  e.device.send('yep! you are connected!\n')
  connectedDevice = e.device
})

btServer.onNewData(function (e) {
  txt.add('recevided from ' + e.device.mac + ': ' + e.data)
})
