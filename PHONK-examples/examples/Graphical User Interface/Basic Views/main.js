/*
 *  PHONK Example: Basic Views
 */

ui.addTitle(app.name)
ui.addSubtitle('Basic views')

var btn = ui.addButton('button', 0.05, 0.2, 0.5, 0.1).onClick(function () {
  console.log('clicked')
})

var text = ui.addText('hello world!', 0.6, 0.2)

var input = ui.addInput(0.05, 0.35, 0.8, 0.1).hint('write here something')
input.onChange(function (e) {
  console.log(e.text)
  text.text(e.text)
})

var slider = ui.addSlider(0.05, 0.5, 0.6, 0.1).onChange(function (o) {
  console.log(o.value)
})
slider.value(0.2)

var toggle = ui.addToggle(['ON', 'OFF'], 0.7, 0.5, 0.2, 0.1).onChange(function (o) {
  console.log(o.checked)
})

var image = ui.addImage('patata.png', 0.55, 0.7, 0.35, 0.25)
var imageButton = ui.addImageButton('patata.png', 0.1, 0.7, 0.35, 0.25).onClick(function (e) {
  console.log(e)
})
// image.load('patata.png')
// image.load('http://')

/*
var slider2 = ui.addSlider(0.05, 0.5, 0.6, 0.1).mode('vertical').showValue(true).onChange(function (o) {
  console.log(o.value)
})
slider.value(0.2)
*/
