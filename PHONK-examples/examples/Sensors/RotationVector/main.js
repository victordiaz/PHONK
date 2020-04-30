/*
 * Phonk Example: Rotation Vector
 */

ui.addTitle(app.name)

var plotX = ui.addPlot(0.1, 0.15, 0.8, 0.1).name('x').range(-1, 1)
var plotY = ui.addPlot(0.1, 0.30, 0.8, 0.1).name('y').range(-1, 1)
var plotZ = ui.addPlot(0.1, 0.45, 0.8, 0.1).name('z').range(-1, 1)

sensors.rotationVector.onChange(function (data) {
  plotX.update(data.x)
  plotY.update(data.y)
  plotZ.update(data.z)
})

// start / stop rotation vector
ui.addToggle(['ON', 'OFF'], 0.1, 0.6, 0.2, 0.1).onChange(function (o) {
  if (o.checked) sensors.rotationVector.start()
  else sensors.rotationVector.stop()
})
