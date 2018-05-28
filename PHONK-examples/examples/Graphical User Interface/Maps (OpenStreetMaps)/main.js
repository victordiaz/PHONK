/*
 * Phonk Example: Open Street Maps
 *
 * Basic operations to show and move around the maps
 */

ui.addTitle(app.name)

var map = ui.addMap(0, 0,  1, 1)

map.moveTo(100 * Math.random(), 100 * Math.random())
map.center(1, 12)
map.zoom(3)
map.showControls(true)

var p = map.createPath('#0000FF')
p.addGeoPoint(100 * Math.random(), 100 * Math.random())
p.addGeoPoint(100 * Math.random(), 100 * Math.random())
p.addGeoPoint(100 * Math.random(), 100 * Math.random())

var m1 = map.addMarker({
  lon: 100 * Math.random(),
  lat: 100 * Math.random(),
  icon: 'marker1.png',
  title: 'Marker 1',
  description: 'Marker 1 description'
})

var m2 = map.addMarker({
  lon: 100 * Math.random(),
  lat: 100 * Math.random(),
  icon: 'marker2.png',
  title: 'Marker 2',
  description: 'Marker 2 description'
})

var m3 = map.addMarker({
  lon: 100 * Math.random(),
  lat: 100 * Math.random(),
})
