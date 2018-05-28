/*
 * Phonk Example: Orientation
 */

ui.addTitle(app.name)

var plotA = ui.addPlot(0.1, 0.15, 0.8, 0.1).name('azimuth')
var plotP = ui.addPlot(0.1, 0.30, 0.8, 0.1).name('pitch')
var plotR = ui.addPlot(0.1, 0.45, 0.8, 0.1).name('roll')

sensors.orientation.onChange(function (data) {
  plotA.update(data.azimuth)
  plotP.update(data.pitch)
  plotR.update(data.roll)
})

// start / stop orientation sensor
ui.addToggle('ON', 0.1, 0.6, 0.2, 0.1).onChange(function (o) {
  if (o.checked) sensors.orientation.start()
  else sensors.orientation.stop()
})
