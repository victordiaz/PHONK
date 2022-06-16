/*
 * PHONK Example: Websockets
 *
 * Server and client
 */

ui.addTitle(app.name)

// creates a server in the port 2525
var server = network.createWebsocketServer(2525).start()
server.onNewData(function (event) {
  console.log(event.status)
  txtServer.add('server receive > '	+ event.data)
  event.socket.send('pong')
})

// connects to the server we just created
var client = network.connectWebsocket('ws://127.0.0.1:2525')
client.onNewData(function (event) {
  console.log(event.status)
  txtClient.add('client receive > ' + event.data)
})

var num = 0
// send a websocket message with and array as parameters
ui.addButton('Send', 0.1, 0.8, 0.8, 0.15).onClick(function () {
  var msg = 'ping ' + num++
  txtClient.add('client send > ' + msg)
  client.send(msg)
})

var props = {
  background: '#000000',
  textColor: '#ffffff',
  padding: 30,
  textSize: 15
}

var txtServer = ui.addTextList(0.05, 0.1, 0.9, 0.30).autoScroll(true)
txtServer.setProps(props)
txtServer.add('Server')

var txtClient = ui.addTextList(0.05, 0.45, 0.9, 0.30).autoScroll(true)
txtClient.setProps(props)
txtClient.props.background = ui.theme.primary
txtClient.props.textColor = ui.theme.background
txtClient.add('Client')

console.log(ui.theme)

/*
 *  other useful methods
 * server.stop()
 * client.isConnected()
 * client.close()
 */
