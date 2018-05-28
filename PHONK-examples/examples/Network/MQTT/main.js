/*
 *  Phonk Example: MQTT Client
 */

ui.addTitle(app.name)
ui.addSubtitle('MQTT client example. Edit the values to connect to your server.')

var clientId = 'proto'
var host = '192.168.43.172'
var port = 1883
var username = null
var password = null

var c = network.createMQTTClient()
c.onNewData(function (e) {
  console.log(e)
})

ui.addButton('connect and subscribe', 0.1, 0.45, 0.35, 0.1).onClick(function (e) {
  c.connect(clientId, host, port, username, password)
  c.subscribe('debug')
})

ui.addButton('publish', 0.55, 0.45, 0.35, 0.1).onClick(function (e) {
  c.publish('debug', 'cuakcuak')
})
