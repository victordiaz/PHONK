/*
 * PHONK Example: Bluetooth serial
 *
 * If you want to connect a bluetooth serial module to Arduino this is a
 * good start
 * https://learn.sparkfun.com/tutorials/using-the-bluesmirf/all
 */

ui.addTitle(app.name)
ui.addSubtitle('You can connect to other Android devices, Computers, Arduinos, etc')

var txt = ui.addTextList(0.1, 0.25, 0.8, 0.3).autoScroll(true)
txt.props.textSize = 15

// scan bluetooth networks
ui.addButton('Scan bluetooth', 0.1, 0.6, 0.8, 0.1).onClick(function () {
  network.bluetooth.scanNetworks(function (e) {
    txt.add(e.name + ' ' + e.mac + ' ' + e.strength)
  })
})

ui.addToggle('Connect to bluetooth', 0.1, 0.72, 0.80, 0.1).onChange(function (e) {
  if (e.checked) btClient.connectSerialUsingMenu()
  else btClient.disconnect()
})

// send bluetooth messages
var input = ui.addInput(0.1, 0.85, 0.58, 0.1).hint('message')
var send = ui.addButton('Send', 0.7, 0.85, 0.2, 0.1).onClick(function () {
  btClient.send(input.text() + '\n')
})

// create a client
var btClient = network.bluetooth.createClient()

btClient.onConnected(function (e) {
  txt.add('status: ' + e.status + ' ' + e.name + ' ' + e.mac)
})

// here is we get the received data
btClient.onNewData(function (e) {
  txt.add('received: ' + e.data)
})
