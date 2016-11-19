/*
* \\\ Example: Midi controller
*
* Midi controllers will only work if your device has support of USB OTG
* If your device does not work create an issue in github indicating
* your model and vendor id :)
*
*/

var midiDevice = media.connectMidiDevice()

midiDevice.onChange(function (data) {
  console.log(data.cable, data.channel, data.function, data.value)
})
