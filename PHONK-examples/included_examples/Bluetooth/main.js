/*
* Bluetooth serial example 
*
* If you want to connect a bluetooth serial module to Arduino this is a 
* good start 
* https://learn.sparkfun.com/tutorials/using-the-bluesmirf/all
*/

var txt = ui.addText("", 10, 450, ui.screenWidth, -1);

ui.addButton("Scan bluetooth", 10, 0, function() {
    network.scanBluetoothNetworks(function(n, m, s) { 
        console.log("hola", n, m, s);
        txt.append(n + " " + m + " " + s + "\n");
    });
})

ui.addButton("Connect to bluetooth", 10, 150, function() {
    network.connectBluetoothSerialByUi(function(m, data) {
        txt.text(data + "\n");
    });
})

ui.addButton("Disconnect", 380, 150, function() {
    network.disconnectBluetooth();
})

var input = ui.addInput("message", 10, 300, 200, 100);
var send = ui.addButton("Send", 210, 300, 150, 100, function() {
    network.sendBluetoothSerial(input.getText() + "\n");
})




// ---------- other methods 
// network.connectBluetoothSerialByMac("98:D3:31:30:1A:4E", function(m, data) {
//     console.log(data);
// });

// network.connectBluetoothSerialByName("bqZUM_BT328", function(m, data) {
//     console.log(m, data);
// });
// network.enableBluetooth(true);
// network.enableBluetooth(false);
// network.isBluetoothConnected(); 
// network.getBluetoothBondedDevices();
