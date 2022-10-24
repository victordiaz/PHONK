/*	
 *  Description simple midi controller to control a soft synth on a computer.
 *  Android 6 is required and the android device should be midi compatible.
 *  See https://github.com/victordiaz/PHONK/pull/111
 */

// globals
const redColor = '#FF0000';

var buttonPositionX;
var buttonPositionY;
const buttonSize = 0.19;
const buttonSpacing = 0.01;

// start program
const midi = media.startMidiController();
const inputs = midi.findAvailableMidiInputs();

if (!inputs.length) {
  ui.toast('no midi input available :(');
  app.close();
} else if (inputs.length === 1) {
  const selectedDeviceInputId = inputs[0].deviceInputId;
  midi.setupMidi(selectedDeviceInputId);
  displayUi();
} else {
  // Select a midi input to send data to the computer if multiple midi inputs are found
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
}

function displayUi() {
  buttonPositionX = 0;
  buttonPositionY = 0;
  
  // chord using midi pitches
  midi.ui.addNoteButton(buttonPositionX, buttonPositionY, buttonSize, buttonSize)
    .withText('C maj from pitches')
    .withNotePitches(60, 64)
    .addNotePitch(67);  // for the sake of the demo, could be in `withNotePitches()`
  movePositionForNextButton();
  
  // chord using note names
  midi.ui.addNoteButton(buttonPositionX, buttonPositionY, buttonSize, buttonSize)
    .withText('Dm7 from note names')
    .withNoteNames('D4', 'F4', 'A4')
    .addNoteName('C5');  // for the sake of the demo, could be in `withNoteNames()`
  movePositionForNextButton();
  
  // complex chord using all note parameters
  midi.ui.addNoteButton(buttonPositionX, buttonPositionY, buttonSize, buttonSize)
    .withText("E4 ch2, G4 ch2 velo 20, B4 ch1")
    .withNotes(
      {
        name: 'E4',
        channel: 1
      },
      {
        name: 'G4',
        channel: 1,
        velocity: 20
      })
    .addNote({name: 'B4'});  // for the sake of the demo, could be in `withNotes()`
  movePositionForNextButton();
  
  // 5th degree chord from a mixolydian mode of C major
  midi.ui.addNoteButton(buttonPositionX, buttonPositionY, buttonSize, buttonSize)
    .withDegreeAndMode({rootName: 'C4', degree: 'V', mode: 'mixolydian', chordSize: 4});
  movePositionForNextButton();

  // F sharp major seventh sharp five Chord :-D
  midi.ui.addNoteButton(buttonPositionX, buttonPositionY, buttonSize, buttonSize)
    .withChord(
      {
        name: 'F#maj7s5',
        octave: 4,
        velocity: 100,
        channel: 1  // not affected by pitch bend as it's channel 1
      });

  movePositionToNewlineForNextButton();
  
  // standard major chord progression (in F here)
  ['I', 'V', 'VI', 'IV'].forEach(function (chordDegree) {
    midi.ui.addNoteButton(buttonPositionX, buttonPositionY, buttonSize, buttonSize)
      .withDegreeAndMode({rootName: 'F3', degree: chordDegree, mode: 'major'})
      .withBackgroundColorWhenPlayed(redColor);
    movePositionForNextButton();
  });
  
  movePositionToNewlineForNextButton();
  
  // chord progression of the final part of "hey jude" from the Beetles: mixolydian mode F major scale
  ['VII', 'IV', 'I'].forEach(function (chordDegree) {
    midi.ui.addNoteButton(buttonPositionX, buttonPositionY, buttonSize, buttonSize)
      .withDegreeAndMode({rootName: 'F4', degree: chordDegree, mode: 'mixolydian'})
      .withBackgroundColorWhenPlayed(redColor);
    movePositionForNextButton();
  });

  movePositionToNewlineForNextButton();
  
  // "where is my mind" from the Pixies chord progression
  midi.ui.addNoteButton(buttonPositionX, buttonPositionY, buttonSize, buttonSize)
    .withChord({name: 'E', octave: 4});

  movePositionForNextButton();
  
  midi.ui.addNoteButton(buttonPositionX, buttonPositionY, buttonSize, buttonSize)
    .withChord({name: 'C#m', octave: 4});
    
  movePositionForNextButton();
  
  midi.ui.addNoteButton(buttonPositionX, buttonPositionY, buttonSize, buttonSize)
    .withChord({name: 'G#', octave: 4});
    
  movePositionForNextButton();
  
  midi.ui.addNoteButton(buttonPositionX, buttonPositionY, buttonSize, buttonSize)
    .withChord({name: 'A', octave: 4});

  movePositionToNewlineForNextButton();

  // Controls
  midi.ui.addModulationWheelSlider(buttonPositionX, buttonPositionY, buttonSize, buttonSize);
  movePositionForNextButton();
  
  midi.ui.addCcSlider(buttonPositionX, buttonPositionY, buttonSize, buttonSize, 74)
    .withChannel(3)
    .withText('filter');
  movePositionForNextButton();
    
  midi.ui.addCcSlider(buttonPositionX, buttonPositionY, buttonSize, buttonSize, 71)
    .withText('timbre');
  movePositionForNextButton();

  midi.ui.addPitchBendSlider(buttonPositionX, buttonPositionY, buttonSize, buttonSize);
  movePositionForNextButton();
  
  midi.ui.addPitchBendSlider(buttonPositionX, buttonPositionY, buttonSize, buttonSize)
    .withText('bend ch1')
    .withChannel(1);
  movePositionForNextButton();
}

function movePositionForNextButton() {
  // Buttons are displayed from left to right and go to newline when overflowing
  if (buttonPositionX + buttonSize + buttonSpacing >= 1) {
    buttonPositionX = 0;
    buttonPositionY += buttonSize + buttonSpacing;
  } else {
    buttonPositionX += buttonSize + buttonSpacing;    
  }
}

function movePositionToNewlineForNextButton() {
  buttonPositionX = 2;  // force overflow :D
  movePositionForNextButton();
}
