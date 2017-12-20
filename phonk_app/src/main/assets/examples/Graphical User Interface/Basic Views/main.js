/*
 *  \\\ Example: Basic Views
 */

ui.addTitle(app.name)
ui.addSubtitle('Basic views')

var btn = ui.addButton('button', 0.05, 0.2, 0.5, 0.1).onClick(function () {
  console.log('clicked')
})

var text = ui.addText('hello world!', 0.6, 0.2)

var input = ui.addInput('input text', 0.05, 0.35, 0.8, 0.1)

var slider1 = ui.addSlider(0.05, 0.5, 0.6, 0.1).onChange(function (o) {
  console.log(o.value)
})

var toggle = ui.addToggle('ON', 0.7, 0.5, 0.2, 0.1).onChange(function (o) {
  console.log(o.checked)
})

var image = ui.addImage('patata2.png', 0.55, 0.7, 0.35, 0.25)
var imageButton = ui.addImageButton('patata2.png', 0.1, 0.7, 0.35, 0.25).onClick(function (e) {
  console.log(e)
})
// image.load('patata2.png')
// image.load('http://www.osnews.com/images/icons/28.gif')
