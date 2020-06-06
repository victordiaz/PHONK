/*
 * Phonk Example: Canvas
 *
 * Create your custom Widgets or draw whatever you want
 * using this component
 */

ui.addTitle(app.name)
ui.addSubtitle('Example of a custom view. LED component')

var LED = function (x, y, w, h) {
  var that = this
  this.x = x
  this.y = y
  this.w = w
  this.h = h
  this.isOn = false

  this.canvas = ui.addCanvas(x, y, w, h)

  this.canvas.draw = function (c) {
    c.clear()
    c.cornerMode(false)

    c.stroke(255, 255, 255, 255)
    c.strokeWidth(10)

    if (that.isOn) c.fill(0, 255, 0, 255)
    else c.fill(255, 0, 0, 255)

    var size = c.width > c.height ? c.height : c.width
    c.ellipse(c.width / 2 + 5, c.height / 2 + 5, size - 20, size - 20)
  }
}

LED.prototype.on = function () {
  this.isOn = true
  this.canvas.invalidate()
}

LED.prototype.off = function () {
  this.isOn = false
  this.canvas.invalidate()
}

var led1 = new LED(0.1, 0.4, 0.1, 0.1)
var led2 = new LED(0.45, 0.4, 0.1, 0.1)
var led3 = new LED(0.8, 0.4, 0.1, 0.1)

led1.on()
led2.off()

util.loop(1000, function () {
  if (led3.isOn) led3.off()
  else led3.on()
}).start()
