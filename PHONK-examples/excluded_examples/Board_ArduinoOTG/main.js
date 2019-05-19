/* Arduino example 
*
* Works with Android devices with OTG support and Arduinos 
* such as Uno, load the Arduino.ino in your Arduino to the 
* the example working 
*/

var text = ui.addText("", 0, 0);

var arduino = boards.startArduino(9600, function(status) {
	console.log("connected " + status)
}); 

arduino.onNewData(function(data) {
    text.setText(data);
});

ui.addButton("ledon", 0, 100, 200, 100).onClick(function() {
    arduino.write("ledon\n");
});

ui.addButton("ledoff", 250, 100, 200, 100, function() {
    arduino.write("ledoff\n");
});