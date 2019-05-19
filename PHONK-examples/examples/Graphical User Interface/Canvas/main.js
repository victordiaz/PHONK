/*
 * Phonk Example: Canvas
 *
 * Create your custom Widgets or draw whatever you want
 * using this component
 */

ui.addTitle(app.name)

var canvas = ui.addCanvas(0, 0, 1, 1)

canvas.draw = function (c) {
  c.clear()
  c.mode(false)
  c.fill(255, 255, 255, 150)

  for (var i = 0; i < 100; i++) {
    x = canvas.width * Math.random()
    y = canvas.height * Math.random()
    w = 100 * Math.random()
    h = 100 * Math.random()

    c.rect(x, y, w, h, 2, 2)
  }
}

ui.addButton('randomize!', 0.05, 0.85, 0.9, 0.1).onClick(function () {
  canvas.invalidate()
})
