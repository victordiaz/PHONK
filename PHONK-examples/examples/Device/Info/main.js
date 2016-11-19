/*
* \\\ Example: Device info
*
* Shows different information about the device
*/

ui.addTitle(app.name)

var txt = ui.addText('', 0.1, 0.15, 0.8, 0.5)
txt.props.background = '#000000'
txt.props.padding = 20
txt.props.textSize = 15
txt.append('battery: ' + device.battery())
txt.append('\ndevice: ' + device.type())
txt.append('\nbrightness: ' + device.brightness())

var info = device.info()
txt.append('\nscreenDpi ' + info.screenDpi + ' versionRelease ' + info.versionRelease)
txt.append('\nnetwork: ' + network.ipAddress() + ' ' + network.wifiInfo().getSSID())

// this is a callback that triggers everytime the battery changes
device.battery(function (e) {
  txt.append('\nbattery update: ' + e.level)
})
