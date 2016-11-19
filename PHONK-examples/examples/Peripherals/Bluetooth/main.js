/*
 * \\\ Example: Bluetooth serial
 *
 * If you want to connect a bluetooth serial module to Arduino this is a
 * good start
 * https://learn.sparkfun.com/tutorials/using-the-bluesmirf/all
 */

var txt = ui.addText(0, 0, 1, 0.5)

// scan bluetooth networks
ui.addButton('Scan bluetooth', 0, 0.5, 1, 0.2).onClick(function () {
  network.bluetooth.scanNetworks(function(data) {
    txt.append(data.name + ' ' + data.mac + ' ' + data.strength + '\n')
  })
})

// connect to a bluetooth peripheral using a dialog
// if you know the mac address you can connect using:
// network.bluetooth.connectSerial(macAddess, function(status) {})
var btClient
ui.addButton('Connect to bluetooth', 0, 0.7, 0.5, 0.2).onClick(function() {
  btClient = network.bluetooth.connectSerial(function(status) {
    console.log('connected ' + status)
  })

  // here is we get the received data
  btClient.onNewData(function(data) {
    txt.text(data + '\n')
  })
})

ui.addButton('Disconnect', 0.5, 0.7, 0.5, 0.2).onClick(function() {
  btClient.disconnect()
})

// send bluetooth messages
var input = ui.addInput('message', 0, 0.9, 0.7, 0.2)
var send = ui.addButton('Send', 0.7, 0.9, 0.3, 0.3).onClick(function() {
  btClient.send(input.text() + '\n')
})
