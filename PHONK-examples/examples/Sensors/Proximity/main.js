/*
 * \\\ Example: Proximity Sensor
 */

ui.addTitle(app.name)

var plot = ui.addPlot(0.1, 0.15, 0.8, 0.1).name('distance')

sensors.proximity.onChange(function (data) {
  plot.update(data.distance)
})

// start / stop proximity sensor
ui.addToggle('ON', 0.1, 0.3, 0.2, 0.1).onChange(function (o) {
  if (o.checked) sensors.proximity.start()
  else sensors.proximity.stop()
})
