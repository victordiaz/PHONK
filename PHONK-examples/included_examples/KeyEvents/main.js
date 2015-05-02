/* 
*	KeyEvents are really awesome it seems that only work 
* 	pressing buttons such as Volume UP & Down but the events 
* 	are triggered as well using Bluetooth keyboards, Makey Makeys 
*   and some game controllers  
*	
*/ 

var txt = ui.addText("", 10, 10);

ui.onKeyDown(function(key) {
    txt.setText("pressed key " +  key);
});