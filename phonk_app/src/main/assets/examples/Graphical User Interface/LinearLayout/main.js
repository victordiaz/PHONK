/*
 * \\\ Example: LinearLayout
 *
 * Using LinearLayout widgets autoadapt vertically or horizontally
 */

ui.addTitle(app.name)
ui.addSubtitle('Layout that organizes the widgets horizontal or vertically')

var btn1 = ui.newImage('patata2.png')
var btn2 = ui.newButton('btn 2')
var btn3 = ui.newButton('btn 3')

var layout = ui.addLinearLayout(0, 0.4, 1, 0.1)
layout.props.background = '#22FFFFFF'
layout.orientation('horizontal')
layout.add(btn1, 0.2)
layout.add(btn2, 0.4)
layout.add(btn3, 0.4)

layout.alignViews("center", "center")
// layout.padding(2, 20, 200, 2)
// layout.clear()
