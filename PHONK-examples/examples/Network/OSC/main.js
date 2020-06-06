/*
 * Phonk Example: OSC network protocol
 *
 * pretty protocol "to connect" different
 * devices / software together
 */

ui.addTitle(app.name)

// create a osc server and listen to incoming messages
var oscServer = network.createOSCServer(9000).onNewData(function (event) {
  console.log(event.data)
  txtServer.add('server > ' + event.name + ', ' + event.data[0] + ', ' + event.data[1] + ', ' + event.data[2])
})

var client
ui.addButton('Connect', 0.05, 0.50, 0.4, 0.1).onClick(function () {
  client = network.connectOSC('127.0.0.1', 9000)
})

var num = 0
// send a osc message with and array as parameters
ui.addButton('Send', 0.55, 0.50, 0.4, 0.1).onClick(function () {
  var tag = '/tag'
  var o = ['value 1', 'value 2', num++]
  client.send(tag, o)
  txtClient.add('client > ' + tag + ' ' + o)
})

var txtServer = ui.addTextList(0.05, 0.15, 0.9, 0.30).autoScroll(true)
txtServer.props.background = '#000000'
txtServer.props.textColor = '#ffffff'
txtServer.props.textSize = 15

var txtClient = ui.addTextList(0.05, 0.65, 0.9, 0.30).autoScroll(true)
txtClient.props.background = '#ffffff00'
txtClient.props.textColor = '#000000'
txtClient.props.textSize = 15
