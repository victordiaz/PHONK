/* 
*	Pure Data http://puredata.info sound engine example 
*
*   Pure Data is an awesome sound engine, and Android users
*   we have to be really glad that it works in this environment 
*
*	Just transfer your pd patch and send values to it 
* 	with the sendFloat, sendBang methods 
*/

ui.screenOrientation("portrait");

var pd = media.initPdPatch("sinwave.pd");

pd.onNewData(function(data) { 
    console.log(data);
});

//add plot setting the limits from -12 to 12 
var plot = ui.addPlot(0, 200, ui.screenWidth, 250, -12, 12); 

sensors.accelerometer.onChange(function(x, y, z) {
   //console.log("accelerometer " + x + ", " + y + ", " + z);
    pd.sendFloat("value", 82 + Math.round(x));
    plot.update(x);
});