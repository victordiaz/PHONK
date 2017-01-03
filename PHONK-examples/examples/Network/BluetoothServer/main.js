/*
 *  \\\ Example: BluetoothServer
 */

ui.addTitle(app.name)
ui.addSubtitle('Start a bluetooth "Chat" server')

ui.addButton('Connect to bluetooth', 0.1, 0.72, 0.38, 0.1).onClick(function() {
  btServer.start()
})

ui.addButton('Disconnect', 0.52, 0.72, 0.38, 0.1).onClick(function () {
  btServer.stop()
})

var btServer = network.bluetooth.createServer('PROTOCODER_CHAT')
btServer.onNewConnection(function (e) {
  console.log('new connection ' + e.device.mac)
  e.device.send('yep! you are connected!\n')
})

btServer.onNewData(function (e) {
  console.log('from :' + e.device.mac + ' ' + e.data)
})
