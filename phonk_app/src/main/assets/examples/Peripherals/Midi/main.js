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

var midi = media.startMidi()

midi.onDeviceEvent(function (e) {
  console.log(e.type)
  if (e.type === 'output') midiOutputDevice = e.device
})

midi.onMidiEvent(function (e) {
  txt.text(e.cable + ' ' + e.channel + ' ' + e.function + ' ' + e.value)
})

ui.addToggle('note 1', 0.1, 0.25, 0.8, 0.2).onChange(function (e) {
  if (e.checked)  midiOutputDevice.sendMidiNoteOn(0, 0, 50, 25)
  else midiOutputDevice.sendMidiNoteOff(0, 0, 50, 25)
})

ui.addToggle('note 2', 0.1, 0.5, 0.8, 0.2).onChange(function (e) {
  if (e.checked)  midiOutputDevice.sendMidiNoteOn(0, 0, 52, 25)
  else midiOutputDevice.sendMidiNoteOff(0, 0, 52, 25)
})

var txt = ui.addText(0.1, 0.7, 0.8, 0.2)
