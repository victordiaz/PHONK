/*
 * \\\ Example: LibPd (Pure Data)
 *
 * Using LibPd (Pure Data) sound engine
 *
 * Just transfer your pd patch and send values to it
 * with the sendFloat, sendBang, etc methods
 *
 * More Info about Pure Data
 * http://puredata.info
 */

ui.addTitle(app.name)

var pd = media.initPdPatch('sinwave.pd')

pd.onNewData(function(data) {
  console.log(data)
})

// add plot setting the limits from -12 to 12
var plot = ui.addPlot(0.1, 0.15, 0.8, 0.1)

sensors.accelerometer.onChange(function (data) {
  pd.sendFloat('value', 82 + Math.round(data.x))
  plot.update(data.x)
})

// start / stop accelerometer
ui.addToggle('ON', 0.1, 0.3, 0.2, 0.1).onChange(function (o) {
  if (o.checked) sensors.accelerometer.start()
  else sensors.accelerometer.stop()
})
