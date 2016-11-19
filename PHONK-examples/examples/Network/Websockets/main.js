/*
 * \\\ Example: Websockets
 *
 * Server and client
 */

// creates a server in the port 2525
var server = network.createWebsocketServer(2525).onNewData(function (event) {
	if (event.status === 'message')	{
	  console.log('from client -> '	+ event.data)
	  event.socket.send('pong')
	}
})

// connects to the server we just created
var client = network.connectWebsocket('ws://127.0.0.1:2525').onNewData(function (event) {
	if (event.status === 'message') {
	  console.log('from server -> ' + event.data)
	}
})

ui.addButton('send data', 0, 0, 1, 0.2).onClick(function () {
	client.send('ping!')
})

// other useful methods
// server.stop()
// client.isConnected()
// client.close()
