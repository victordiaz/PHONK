/*
* Works with ioio boards, you have to pair them using the 
*   android bluetooth settings and input the code 4545 
*/

var led; 
var input;
var ioioConnected = false;
var ioio;

//the phone will vibrate and speak when connected to the ioio 
ui.addButton("Start ioio", 0, 0, ui.screenWidth, 200).onClick(function() { 
  ioio = boards.startIOIO(function() { 
    //this function is executed when the ioio board is ready
    led = ioio.openDigitalOutput(0);
    input = ioio.openAnalogInput(31);
    device.vibrate(500);  
    media.textToSpeech("ioio connected");
    ioioConnected = true;
  });
});

util.loop(500, function() {
  if(ioioConnected == true) { 
    console.log("the reading is " + input.getVoltage());
  }
})

ui.addButton("On", 0, 220, ui.screenWidth / 2, 300).onClick(function() { 
  if (ioioConnected == true) led.write(false);
});

ui.addButton("Off", ui.screenWidth / 2, 220, ui.screenWidth / 2, 300).onClick(function() { 
  if (ioioConnected == true)   led.write(true);
});


//power off the ioio  
ui.addButton("Stop ioio", 0, 550, ui.screenWidth, 100).onClick(function() { 
  if (ioioConnected == true) {
    ioio.stop();
    ioioConnected = false
  }
});