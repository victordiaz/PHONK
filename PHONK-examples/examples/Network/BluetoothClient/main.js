/*
 * \\\ Example: Bluetooth serial
 *
 * If you want to connect a bluetooth serial module to Arduino this is a
 * good start
 * https://learn.sparkfun.com/tutorials/using-the-bluesmirf/all
 */

ui.addTitle(app.name)
ui.addSubtitle('You can connect to other Android devices, Computers, Arduinos, etc')

var txt = ui.addText('', 0.1, 0.2, 0.8, 0.3)
txt.props.background = '#000000'
txt.props.padding = 20
txt.props.textSize = 15

// scan bluetooth networks
ui.addButton('Scan bluetooth', 0.1, 0.6, 0.8, 0.1).onClick(function () {
  network.bluetooth.scanNetworks(function (e) {
    txt.append(e.name + ' ' + e.mac + ' ' + e.strength + '\n')
  })
})

ui.addButton('Connect to bluetooth', 0.1, 0.72, 0.38, 0.1).onClick(function () {
  btClient.connectSerial()
})

ui.addButton('Disconnect', 0.52, 0.72, 0.38, 0.1).onClick(function () {
  btClient.disconnect()
})

// send bluetooth messages
var input = ui.addInput('message', 0.1, 0.85, 0.58, 0.1)
var send = ui.addButton('Send', 0.7, 0.85, 0.2, 0.1).onClick(function () {
  btClient.send(input.text() + '\n')
})

// create a client
var btClient = network.bluetooth.createClient()

btClient.onConnected(function (e) {
  txt.append('connected ' + e.status + '\n')
})

// here is we get the received data
btClient.onNewData(function (e) {
  txt.append('received ' + e.data + '\n')
})
