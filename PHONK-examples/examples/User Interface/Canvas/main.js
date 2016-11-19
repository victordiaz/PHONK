/*
 * \\\ Example: Canvas
 *
 * Create your custom Widgets or draw whatever you want
 * using this component
 */

var canvas = ui.addCanvas(0, 0, 1, 0.8)

var x = 1000
var y = 100

canvas.draw = function (c) {
  c.clear()
  c.mode(false)
  c.fill(0, 0, 0, 50)
  c.rect(x, y, 50, 50)
}

ui.addButton('randomize!', 0, 0.8, 1, 0.2).onClick(function () {
  x = Math.random() * canvas.width
  y = Math.random() * canvas.height
  canvas.invalidate()
})
