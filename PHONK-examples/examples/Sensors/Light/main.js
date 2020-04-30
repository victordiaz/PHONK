/*
 * Phonk Example: Light Sensor
 */

ui.addTitle(app.name)

var plot = ui.addPlot(0.1, 0.15, 0.8, 0.1).name('light intensity').range(-2, 10)

sensors.light.onChange(function (data) {
  plot.update(data.intensity)
})

// start / stop light sensor
ui.addToggle(['ON', 'OFF'], 0.1, 0.3, 0.2, 0.1).onChange(function (o) {
  if (o.checked) sensors.light.start()
  else sensors.light.stop()
})
