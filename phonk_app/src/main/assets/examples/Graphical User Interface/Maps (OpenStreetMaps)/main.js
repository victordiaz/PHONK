/*
 *	\\\ Example: Open Street Maps
 *
 * Basic operations to show and move around the maps
 */

ui.addTitle(app.name)

var map = ui.addMap(0, 0,  1, 1)

map.moveTo(100 * Math.random(), 100 * Math.random())
map.center(1, 12)
map.zoom(3)
map.showControls(true)

var p = map.addPath('#0000FF')
map.addPointToPath(p, 100 * Math.random(), 100 * Math.random())
map.addPointToPath(p, 100 * Math.random(), 100 * Math.random())
map.addPointToPath(p, 100 * Math.random(), 100 * Math.random())

map.addMarker('title', 'text', 100 * Math.random(), 100 * Math.random())
