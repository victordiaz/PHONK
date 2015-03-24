/*
*	Shows which id and message are written in the NFC tag 
*/

var nfcinfo = ui.addText("tap to get NFC id and content", 20, 20, 500, 100);

//when tapping on a nfc the id and content will be displayed 
//on the label 
sensors.onNFC(function (id, data) { 
    console.log("the nfc id is: " + id, data); 
    nfcinfo.html("<strong>id: </strong>" + id + "<br /> <strong>data: </strong>" + data);
});


//when we click 
//the next touched nfc will be written with the data 
ui.addButton("Write to NFC", 0, 200, 500, 200, function(){
	sensors.writeNFC("this is a test", function() {
		nfcinfo.setText("data written");
	});
});