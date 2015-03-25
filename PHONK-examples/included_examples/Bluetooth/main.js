/*
* Bluetooth serial example 
*
* If you want to connect a bluetooth serial module to Arduino this is a 
* good start 
* https://learn.sparkfun.com/tutorials/using-the-bluesmirf/all
*/

var txt = ui.addText(10, 450, ui.screenWidth, -1);

ui.addButton("Scan bluetooth", 10, 0).onClick(function() {
    network.bluetooth.scanNetworks(function(n, m, s) { 
        console.log("hola", n, m, s);
        txt.append(n + " " + m + " " + s + "\n");
    });
});

var btClient;
ui.addButton("Connect to bluetooth", 10, 150).onClick(function() {
    //if you want to use the Bluetooth Address, use 
    //network.bluetooth.connectSerial(macAddess, function(status) {});
    btClient = network.bluetooth.connectSerial(function(status) {
        console.log("connected " + status);
    });
    
    btClient.onNewData(function(data) {
        txt.text(data + "\n");
    });
});

ui.addButton("Disconnect", 380, 150).onClick(function() {
    btClient.disconnect();
});

var input = ui.addInput("message", 10, 300, 200, 100);
var send = ui.addButton("Send", 210, 300, 150, 100).onClick(function() {
    btClient.send(input.getText() + "\n");
});