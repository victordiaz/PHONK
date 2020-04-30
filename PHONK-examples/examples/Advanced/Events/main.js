/*
 * Phonk Example: Events
 *
 * A simple way use data in different parts of the app
 * easy to obtain MVC patterns using
 *
 * you can use app.removeEvent(id) in order to stop listening to the event
 *
 */

ui.addTitle(app.name)
ui.addSubtitle('Listen and send events')

// start accelerometer
sensors.accelerometer.onChange(function (data) {
  // send event
  var e = { 'data': data }
  app.sendEvent('event1', e)
}).start()

// register event
var id = app.listenEvent('event1', function (event) {
  console.log(event.data.x)
})

var txt = ui.addText('', 0.1, 0.3)
txt.textSize(35)
// register an event and display it in the text field
var id = app.listenEvent('event1', function (event) {
  txt.text(event.data.x.toFixed(3))
})

