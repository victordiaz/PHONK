/*
 * Phonk Example: Location
 *
 * Get geoposition
 */

ui.addTitle(app.name)

var txt = ui.addText('waiting to get location signal (GPS, Galileo, Glonass, etc)', 0.1, 0.15, 0.8, 0.5)
txt.props.background = '#000000'
txt.props.padding = 20
txt.props.textSize = 12

sensors.location.onChange(function (data) {
// for each location update the image and values are changed
  txt.clear()
  txt.append('Latitude : ' + data.latitude)
  txt.append('\nLongitude : ' + data.longitude)
  txt.append('\nAltitude : ' + data.altitude)
  var distance = sensors.location.distance(data.latitude, data.longitude, 37.177336, -3.598557)
  txt.append('\n\nYour are ' + distance / 1000 + ' km far away from Granada, Spain')
})

sensors.location.onSatellitesChange(function (data) {
  console.log(data)
})

sensors.location.start()
