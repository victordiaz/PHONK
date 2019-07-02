/*
 * Phonk Example: Magnetic Sensor
 */

ui.addTitle(app.name)

var plotX = ui.addPlot(0.1, 0.15, 0.8, 0.1).name('x')
var plotY = ui.addPlot(0.1, 0.30, 0.8, 0.1).name('y')
var plotZ = ui.addPlot(0.1, 0.45, 0.8, 0.1).name('z')

sensors.magneticField.onChange(function (data) {
  plotX.update(data.x)
  plotY.update(data.y)
  plotZ.update(data.z)
})

// start / stop magnetic sensor
ui.addToggle('ON', 0.1, 0.6, 0.2, 0.1).onChange(function (o) {
  if (o.checked) sensors.magneticField.start()
  else sensors.magneticField.stop()
})
