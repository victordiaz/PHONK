/*
*	Open Street Maps 
*
*   You may wonder why OSMMaps are used instead of Google ones, 
*   1) Open Street Maps are awesome 
*   2) Many Android devices cannot use Google maps 
*   3) Maps can be cached and loaded from the sdcard 
*   4) The OSM community is awesome 
*   5) Did I say the Open Street Maps projects is awesome? :) 
*
*	It uses Osmdroid library but will be changed in the future 
*
*/

var map = ui.addMap(0, 0, ui.screenWidth, 500);
map.moveTo(100 * Math.random(), 100 * Math.random());
map.center(1, 12);
map.zoom(3);
map.showControls(true);

var p = map.addPath("#0000FF"); 
map.addPointToPath(p, 100 * Math.random(), 100 * Math.random());
map.addPointToPath(p, 100 * Math.random(), 100 * Math.random());
map.addPointToPath(p, 100 * Math.random(), 100 * Math.random());

//markers cannot be clicked yet :( 
map.addMarker("title", "text", 100 * Math.random(), 100 * Math.random());