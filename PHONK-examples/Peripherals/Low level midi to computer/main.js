/*
 *  Description simple midi controller to control a soft synth on a computer
 */

const midi = media.startMidiController();
const inputs = midi.findAvailableMidiInputs();

if (!inputs.length) {
  ui.toast('no midi input available :(');
  app.close();
}

// globals
const velocity = 127;  // max volume!
const channel = 0;  // midi channel
const greyColor = '#474747';
const redColor = '#FF0000';
const greenColor = '#00FF00';

var buttonPositionX;
var buttonPositionY;
const buttonSize = 0.24;
const buttonSpacing = 0.01;

// Select a midi input to send data to the computer
ui.popup()
  .title('Choose a midi input:')
  .choice(inputs.map(function (input) {
    return input.name;
  }))
  .onAction(function (selectedElement) {
    const selectedElementIndex = selectedElement.answerId;
    const selectedDeviceInputId = inputs[selectedElementIndex].deviceInputId;
    midi.setupMidi(selectedDeviceInputId);
    displayUi();
  })
  .show();

// Display
function displayUi() {
  // major chords (C major)
  buttonPositionX = 0;
  buttonPositionY = 0;

  const notesInMajorScale = [];
  var note = 60;  // middle C
  const majorIntervals = [2, 2, 1, 2, 2, 2, 1];
  majorIntervals.forEach(function (interval, i) {
    notesInMajorScale.push(note);
    note += interval;
  });

  displayChordButtons(notesInMajorScale, redColor);

  // minor chords (A minor)
  buttonPositionX = 0;
  buttonPositionY = 0.5;

  const notesInMinorScale = [];
  note = 57;  // A just below middle C
  const minorIntervals = [2, 1, 2, 2, 1, 2, 2];
  minorIntervals.forEach(function (interval, i) {
    notesInMinorScale.push(note);
    note += interval;
  });

  displayChordButtons(notesInMinorScale, greenColor);
}

function displayChordButtons(scale, colorWhenPlayed) {
  ['I', 'II', 'III', 'IV', 'V', 'VI', 'VII'].forEach(function (chordDegree, i) {
    const button = ui.addButton(chordDegree, buttonPositionX, buttonPositionY, buttonSize, buttonSize);
    button.setProps({
      background: greyColor
    });
    const firstNote = scale[i];
    const secondNote = scale[(i + 2) % 7];  // mod 7 will do inversions
    const thirdNote = scale[(i + 4) % 7];
    button.onPress(function () {
      midi.noteOn(channel, firstNote, velocity);
      midi.noteOn(channel, secondNote, velocity);
      midi.noteOn(channel, thirdNote, velocity);
      button.setProps({
        background: colorWhenPlayed
      });
    });
    button.onRelease(function () {
      midi.noteOff(channel, firstNote, velocity);
      midi.noteOff(channel, secondNote, velocity);
      midi.noteOff(channel, thirdNote, velocity);
      button.setProps({
        background: greyColor
      });
    });

    // Buttons are displayed from left to right and go to newline when overflowing
    if (buttonPositionX + buttonSize + buttonSpacing >= 1) {
      buttonPositionX = 0;
      buttonPositionY += buttonSize + buttonSpacing;
    } else {
      buttonPositionX += buttonSize + buttonSpacing;
    }
  });
}