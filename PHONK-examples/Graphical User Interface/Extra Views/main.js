/*
 *  Phonk More Views
 *
 */

ui.addTitle(app.name)

var touchPad = ui.addTouchPad(0.1, 0.2, 0.8, 0.3).onTouch(function (e) {
  console.log(e)
})

var knob = ui.addKnob(0.7, 0.55, 0.2, 0.2).onChange(function (o) {
  console.log(o.value)
})

var matrix = ui.addMatrix(0.05, 0.7, '0.45w', '0.45w', 10, 10).onChange(function (o) {
  console.log(o.x, o.y, o.selected)
})
