ui.addInput('write here yout text', 0, 0.2, 1, 0.1)
ui.addCheckbox('check me', 0, 0.3, 1, 0.1)
ui.addToggle('toggle', 0, 0.4, 1, 0.1)
ui.addSwitch('Switch', 0, 0.5, 1, 0.1)
ui.addSlider(0, 0.6, 1, 0.1)
ui.addProgressBar(0, 0.7, 1, 0.1).progress(50)



// Add a label with text
ui.addText('I love ice cream', 0, 0.2, 1, 0.1)

// Add an edit text

// Add a toggle button
ui.addToggle('I am toggleable', 0, 0.4, 1, 0.1, true).onChange(function (val) {
  console.log(val)
})

// Add an image
