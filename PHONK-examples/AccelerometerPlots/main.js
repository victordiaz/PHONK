/*
*   Plots android accelerometer data locally and remotely 
*
*/

//add android plots 
var plot = ui.addPlot(0, 250, ui.screenWidth, 200, -12, 12); 
var plot2 = ui.addPlot(0, 480, ui.screenWidth, 200, -12, 12); 

//add webplot 
var webPlot = dashboard.addPlot("accelerometer x", 400, 100, 250, 100, -12, 12);
//plot.setThickness(15);
plot.setDefinition(2);

//start button, when press add plots and start accelerometer 
ui.addButton("Start Accelerometer", 0, 0, ui.screenWidth, 150).onClick(function() {
        dashboard.show(true);

        sensors.accelerometer.onChange(function(x,y,z) {
            //update plots
            webPlot.update(x);
            plot.update("x", x);
            plot2.update("y", y);
          }
       );
});

//stop accelerometer 
ui.addButton("Stop Accelerometer", 0, 150, ui.screenWidth, 150).onClick(function() { 
    dashboard.show(false);
    sensors.accelerometer.stop();
});