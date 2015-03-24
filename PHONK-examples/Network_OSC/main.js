/* 
*	OSC networking protocol, pretty handy to connect different 
*	devices / softwares together 
*/ 

var oscServer = network.createOSCServer(9000).onNewData(function(name, data) { 
    console.log(name + " " + data);
}); 

var client;
ui.addButton("Connect", 0, 0, 500, 200).onClick(function() { 
    client = network.connectOSC("127.0.0.1", 9000);
});


ui.addButton("Send", 0, 200, 500, 200).onClick(function() { 
    var o = new Array();
    o.push("hola");
    o.push(2);
    client.send("hello", o);
});