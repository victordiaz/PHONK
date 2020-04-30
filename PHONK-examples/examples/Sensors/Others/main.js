/*
 * Phonk Example: Other Sensors
 *
 * Barometer and StepDetector
 * Only few devices have them
 */

ui.addTitle(app.name)

var text = ui.addText('', 0.1, 0.15, 0.8, 0.2)
text.props.background = '#000000'
text.props.padding = 20
text.props.textSize = 20
text.text('\nbarometer: ' + sensors.barometer.isAvailable())
text.append('\nstep detector: ' + sensors.stepDetector.isAvailable())

sensors.barometer.onChange(function (data) {
  console.log('barometer ', data.bar)
})

sensors.stepDetector.onChange(function (data) {
  console.log('step detected')
})

// stop accelerometer
ui.addToggle(['ON', 'OFF'], 0.1, 0.4, 0.2, 0.1).onChange(function (o) {
  if (o.checked) {
    sensors.barometer.start()
    sensors.stepDetector.start()
  } else {
    sensors.barometer.stop()
    sensors.stepDetector.stop()
  }
})
