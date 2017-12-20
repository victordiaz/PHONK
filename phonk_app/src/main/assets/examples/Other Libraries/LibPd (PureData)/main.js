/*
 * \\\ Example: LibPd (Pure Data)
 *
 * Using LibPd (Pure Data) sound engine
 *
 * Just transfer your pd patch and send values to it
 * with the sendFloat, sendBang, etc methods
 *
 * Have a look to the original patch
 *
 * More Info about Pure Data
 * http://puredata.info
 */

ui.addTitle(app.name)
ui.addSubtitle('Load a PureData patch and interact with it. Check the .pd patch or check the attached image')

var pd = media.initLibPd()
pd.loadPatch('sinwave.pd')

// add plot setting the limits from -12 to 12
var plot = ui.addPlot(0.1, 0.25, 0.8, 0.1)

sensors.accelerometer.onChange(function (data) {
  pd.sendFloat('myvalue', 82 + Math.round(data.x))
  plot.update(data.x)
})

// here we say to receive a named object
pd.listenTo('info')

// here is where we get the data that receives the data
pd.onNewData(function (data) {
  console.log(data)
})

// start / stop accelerometer
ui.addToggle('ON', 0.1, 0.4, 0.2, 0.1).onChange(function (o) {
  if (o.checked) {
    sensors.accelerometer.start()
    pd.start()
  } else {
    sensors.accelerometer.stop()
    pd.stop()
  }
})

ui.addImage('puredata_patch.png', 0.1, 0.55, 0.8, 0.35)
