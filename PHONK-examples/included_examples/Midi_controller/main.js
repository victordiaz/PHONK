/*
* Midi controller example
*
* Midi controllers will only work if your device has support of usb OTG
* If your device does not work create an issue in github indicating 
* your model and vendor id :)
*
*/


var midiDev = media.connectMidiDevice();

midiDev.onChange(function(cable, ch, f, val) {
    console.log(cable, ch, f, val);
});

