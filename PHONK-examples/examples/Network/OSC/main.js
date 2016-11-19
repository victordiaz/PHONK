/*
 * \\\ Example: OSC network protocol
 *
 * pretty protocol "to connect" different
 * devices / software together
 */

 ui.addTitle(app.name)

// create a osc server and listen to incoming messages
var oscServer = network.createOSCServer(9000).onNewData(function (event) {
  txtServer.append(event.name + ' ' + event.data + '\n')
})

var client
ui.addButton('Connect', 0.05, 0.50, 0.4, 0.1).onClick(function () {
  client = network.connectOSC('127.0.0.1', 9000)
})

// send a osc message with and array as parameters
ui.addButton('Send', 0.55, 0.50, 0.4, 0.1).onClick(function () {
  var o = ['text', 'more text', 2]
  client.send('/hello', o)
  txtClient.append('sending' + '\n')
})


var txtServer = ui.addText('server > \n', 0.05, 0.15, 0.9, 0.30)
txtServer.props.background = '#000000'
txtServer.props.padding = 20
txtServer.props.textSize = 15

var txtClient = ui.addText('client > \n', 0.05, 0.65, 0.9, 0.30)
txtClient.props.background = '#000000'
txtClient.props.padding = 20
txtClient.props.textSize = 15
