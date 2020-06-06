/*
 * Phonk Example: LinearLayout
 *
 * Using LinearLayout widgets autoadapt vertically or horizontally
 */

ui.addTitle(app.name)
ui.addSubtitle('Layout that organizes the widgets horizontal or vertically')

var img1 = ui.newView('image').load('patata.png')
var btn2 = ui.newView('button').text('btn 2')
var btn3 = ui.newView('button').text('btn 3')

var layout = ui.addLinearLayout(0, 0.4, 1, 0.1)
layout.props.background = '#22FFFFFF'
layout.orientation('horizontal')
layout.add(img1, 0.2)
layout.add(btn2, 0.4)
layout.add(btn3, 0.4)

layout.alignViews('center', 'center')
// layout.padding(2, 20, 200, 2)
// layout.clear()
