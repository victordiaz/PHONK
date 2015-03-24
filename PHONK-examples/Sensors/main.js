/* 
*  Sensors
*
*  depending on your device, you will have access to certain 
*  sensors, check which ones work on your device!
* 
*/ 

var accelerometer   = ui.addText(20, 20, 600, 100);
var orientation     = ui.addText(20, 120, 600, 100);
var light           = ui.addText(20, 220, 600, 100);
var gyroscope       = ui.addText(20, 320, 600, 100);
var magnetic        = ui.addText(20, 420, 600, 100);
var barometer       = ui.addText(20, 520, 600, 100);
var proximity       = ui.addText(20, 620, 600, 100);


sensors.accelerometer.onChange(function(x, y, z) {
    accelerometer.setText("acc: " + x + ", " + y + ", " + z);
});

sensors.orientation.onChange(function(pitch, roll, yaw) {
   orientation.setText("orientation: " + pitch + ", " + roll + ", " + yaw);
});

sensors.lightIntensity.onChange(function(intensity) {
   light.setText("light: " + intensity);
});

sensors.gyroscope.onChange(function(x, y, z) {
   gyroscope.setText("gyro: " + x + ", " + y + ", " + z);
});

sensors.magnetic.onChange(function(x, y, z) {
   magnetic.setText("magnetic: " + x);
});

sensors.pressure.onChange(function(x) {
   barometer.setText("barometer " + x);
});

sensors.proximity.onChange(function(intensity) {
   proximity.setText("proximity: " + intensity);
});

sensors.stepDetector.onChange(function() {
    console.log("step detected");
});


//Protocoder automagically stops all the sensors 
//when you exit your script, but here you have a 
//handy function to stop them all :)
function stopAll() {
    sensors.accelerometer.stop();
    sensors.orientation.stop();
    sensors.lightIntensity.stop();
    sensors.gyroscope.stop();
    sensors.magnetic.stop();
    sensors.pressure.stop();
    sensors.proximity.stop();
    sensors.stepDetector.stop();
}