/*
 * \\\ Example: Websockets
 *
 * Server and client
 */

ui.addTitle(app.name)

// creates a server in the port 2525
var server = network.createWebsocketServer(2525).start()
server.onNewData(function (event) {
	if (event.status === 'message')	{
	  txtServer.add('server > received from client: '	+ event.data)
	  event.socket.send('pong')
	}
})

// connects to the server we just created
var client = network.connectWebsocket('ws://127.0.0.1:2525').onNewData(function (event) {
	if (event.status === 'message') {
	  txtClient.add('client > received from server: ' + event.data)
	}
})

var num = 0
// send a websocket message with and array as parameters
ui.addButton('Send', 0.1, 0.50, 0.8, 0.1).onClick(function () {
  var msg = 'ping ' + num++
  txtClient.add('client > ' + msg)
	client.send(msg)
})

var txtServer = ui.addTextList(0.05, 0.15, 0.9, 0.30).autoScroll(true)
txtServer.props.background = '#000000'
txtServer.props.textColor = '#ffffff'
txtServer.props.textSize = 15

var txtClient = ui.addTextList(0.05, 0.65, 0.9, 0.30).autoScroll(true)
txtClient.props.background = '#ffffff'
txtClient.props.textSize = 15


/*
 *  other useful methods
 * server.stop()
 * client.isConnected()
 * client.close()
 */
