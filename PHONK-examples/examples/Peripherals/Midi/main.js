/*
* \\\ Example: Midi controller
*
* Midi controllers will only work if your device has support of USB OTG
* If your device does not work create an issue in github indicating
* your model and vendor id :)
*
*/

ui.addTitle(app.name)
ui.addSubtitle('Connect a USB MIDI device. You can send and receive notes.')

var midiDevice = media.connectMidiDevice()

midiDevice.onChange(function (data) {
  console.log(data.cable, data.channel, data.function, data.value)
})


ui.addToggle('note 1', 0.1, 0.25, 0.8, 0.2).onChange(function (e) {
  if (e.checked)  midiDevice.sendNoteOn(0, 0, 50, 25)
  else midiDevice.sendNoteOff(0, 0, 50, 25)
})

ui.addToggle('note 2', 0.1, 0.5, 0.8, 0.2).onChange(function (e) {
  if (e.checked)  midiDevice.sendNoteOn(0, 0, 52, 25)
  else midiDevice.sendNoteOff(0, 0, 52, 25)
})

var prevX = 0
sensors.accelerometer.onChange(function (e) {
  // console.log(e.x)
  prevX = e.x
  midiDevice.sendNoteOff(0, 0, 60 + prevX, 255)
  midiDevice.sendNoteOn(0, 0, 60 + e.x, 255)
}).start()
