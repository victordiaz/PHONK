/*
*	
*	SocketIO client
*
*/

var url = "http://127.0.0.1:2828";

var client = network.connectSocketIO(url);

client.onNewData(function(status, socket, data) {
    console.log(status, socket, data);
});

ui.addButton("Ping", 10, 10, 200, 200).onClick(function() {
    client.emit("ping");
});

//other useful methods 
//client.disconnect();