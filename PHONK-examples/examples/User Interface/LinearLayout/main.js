/*
 * \\\ Example: LinearLayout
 *
 * Using LinearLayout widgets autoadapt vertically or horizontally
 */

ui.background(0, 0, 255)

function fn () {
  ui.toast('hola')  
}

var btn1 = ui.newButton('btn 1').onClick(fn)
var btn2 = ui.newButton('btn 2').onClick(function () {
  ui.toast('hola 2 ')
})
var toggle = ui.newToggle('toggle')

var layout = ui.addLinearLayout(0, 0, 1, 1)
layout.background(255, 0, 0)
layout.orientation('horizontal')
layout.add(btn1)
layout.add(btn2)
layout.add(toggle)
layout.alignViews("center", "center")
// layout.padding(100, 10, 10, 10)
// layout.clear()