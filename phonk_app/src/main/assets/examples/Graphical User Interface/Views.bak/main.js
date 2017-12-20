/*
* \\\ Example: More Widgets
*
* More widgets available in Protocoder
*/

ui.addChoiceBox(['1', '2'], 0, 0, 1, 0.2).onSelected(function (data) {
  console.log(data.selected)
})

ui.addNumberPicker(0, 10, 0, 0.2, 1, 0.5).onSelected(function (data) {
  console.log(data.selected)
})

ui.addImageButton("patata2.png", 0, 0.7, 0.2, 0.2)
.pressed('patata2_on.png')
//.noBackground()
.onClick(function (data) {
  ui.toast(data)
})


ui.addInput('write here yout text', 0, 0.2, 1, 0.1)
ui.addCheckbox('check me', 0, 0.3, 1, 0.1)
ui.addToggle('toggle', 0, 0.4, 1, 0.1)
ui.addSwitch('Switch', 0, 0.5, 1, 0.1)
ui.addProgressBar(0, 0.7, 1, 0.1).progress(50)



// Add a label with text
ui.addText('I love ice cream', 0, 0.2, 1, 0.1)

// Add an edit text

// Add a toggle button
ui.addToggle('I am toggleable', 0, 0.4, 1, 0.1, true).onChange(function (val) {
  console.log(val)
})



// Add an image
