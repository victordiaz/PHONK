/*
* Phonk Example: Device info
*
* Shows different information about the device
*/

ui.addTitle(app.name)

var txt = ui.addTextList(0.1, 0.15, 0.8, 0.5).autoScroll(true)
txt.props.textSize = 15
txt.add('battery: ' + device.battery())
txt.add('brightness: ' + device.brightness())

var info = device.info()
txt.add('device: ' + info.type)
txt.add('screenDpi ' + info.screenDpi + ' versionRelease ' + info.versionRelease)
txt.add('network: ' + network.networkInfo().ip)

// this is a callback that triggers everytime the battery changes
device.battery(function (e) {
  txt.add('battery update: ' + e.level)
})
