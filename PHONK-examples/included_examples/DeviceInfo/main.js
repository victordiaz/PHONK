/*
*	
* Device info example
*
*/
var batteryTxt = ui.addText("battery", 10, 10, 200, 50);

//this gets battery when we want 
batteryTxt.setText("battery: " + device.battery());

//this is a callback that triggers everytime the battery changes
device.battery(function (e) {
    batteryTxt.setText("battery " + e.level);
});

var tabletLabel = ui.addText("tablet " + device.type(), 10, 60, 200, 50);
var brightnessLabel = ui.addText("brightness " + device.brightness(), 10, 110, 200, 50);

var info = device.info();
var infoText = "screenDpi " + info.screenDpi + " versionRelease" + info.versionRelease;
var infoLabel = ui.addText(infoText, 10, 160, 500, 50);
var wifiTxt = network.ipAddress() + " " + network.wifiInfo().getSSID();
var ipTxt = ui.addText(wifiTxt, 10, 210);