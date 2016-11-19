/*
 * \\\ Example: Events
 *
 * A simple way use data in different parts of the app
 * easy to obtain MVC patterns using
 *
 */

// start accelerometer
sensors.accelerometer.onChange(function (event) {
  // send event
  app.sendEvent('e1', event)
})

// register event
var id = app.listenEvent('e1', function (event) {
  console.log(event.x)
})

var txt = ui.addText('', 0, 0)
txt.textSize(85)

// register an event and display it in the text field
var id = app.listenEvent('e1', function (o) {
  txt.text(Math.round(o.x * 100) / 100)
});

// you can use app.removeEvent(id) in order to stop listening to the event
