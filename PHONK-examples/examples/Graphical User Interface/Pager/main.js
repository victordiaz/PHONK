/*
 *  PHONK Example: View Pager
 *
 */

ui.addTitle(app.name)

var area1 = ui.newArea()
area1.addButton('hi!', 0.3, 0.35, 0.4, 0.2).onClick(function (e) {
  ui.toast('hou!')
})

var area2 = ui.newArea()
area2.addKnob(0.35, 0.3, 0.3, 0.3).range(0, 125).onChange(function (e) {
  area2.background(0, e.value, 0)
})

var p = ui.addPager(0, 0, 1, 1)
p.add(area1.getView())
p.add(area2.getView())
p.page(0)

ui.addButton('Button Above', 0, 0.9, 1.0, 0.1).onClick(function (e) {
  console.log('o_o')
})
