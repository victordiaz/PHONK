/*
 *  Phonk Example: MQTT Client
 */

ui.addTitle(app.name)
ui.addSubtitle('MQTT client example. Edit the values to connect to your server.')

var connectionData = {
  clientId: 'phonk',
  broker: 'tcp://192.168.10.100:1883',
  username: null,
  password: null
}

var c = network.createMQTTClient()
c.onNewData(function (e) {
  console.log(e)
})

ui.addButton('connect and subscribe', 0.1, 0.45, 0.35, 0.1).onClick(function (e) {
  c.connect(connectionData)
  c.subscribe('debug')
})

ui.addButton('publish', 0.55, 0.45, 0.35, 0.1).onClick(function (e) {
  c.publish('debug', 'hello world', 2, false) // params: topic, data, qos, retain
})
