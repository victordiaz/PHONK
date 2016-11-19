/*
 * \\\ Example: Canvas
 *
 * Create your custom Widgets or draw whatever you want
 * using this component
 */

var canvas = ui.addCanvas(0, 0, 1, 1);

var d = 20
canvas.autoDraw(true)
canvas.drawInterval(30)
canvas.mode(false)

canvas.draw = function (c) {
  c.fill(0, 0, 0, 50)
  c.rect(0, 0, 1000, 100)
  gridOf(c, function (x, y) {
    c.fill(255, 255, 255, 255 * Math.random())
    var d1 = d + Math.random() * 35 + mx / 10
    c.ellipse(0, 0, d1, d1)
    // c.fill(0)
    // c.ellipse(0, 0, c - d, c - d)
    // c.fill(255)
    // p.ellipse(0, 0, 12, 12)
    // p.arc(0, 0, 20, 20,  p.random(0, 10), p.random(p.TWO_PI))
  }, 20, 20, c.width, c.height, 10, 10)

}

var mx = 0
var my = 0
ui.onTouch(canvas, function (o) {
  mx = o.x
  my = o.y
})

function gridOf(p, fn, x, y, w, h, c, r) {
  var deltaX = w / c
  var deltaY = h / r

  p.push()
  p.translate(x, y)
  for (var i = 0; i < w; i += deltaX) {
    for (var j = 0; j < h; j += deltaY) {
      p.push()
      p.translate(i, j)
      fn(i, j)
      p.pop()
    }
  }
  p.pop()
}
