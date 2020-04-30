/*
 * Phonk Example: Processing
 *
 * This is a tiny bit modified version of Processing.org
 * for Android.
 * The usage its pretty similar, you just need to prepend
 * the processing object (p.) to the methods to access them
 * example: p.rect(x, y, w, h)
 *
 */

ui.addTitle(app.name)
ui.addSubtitle('Touch the screen to paint thingies')

var processing = ui.addProcessingCanvas(0, 0, 1, 1)

// everything we add after the Processing Canvas will be on Top of it
var type = 'circle'
var btn = ui.addButton('□', 0.1, 0.8, 0.35, 0.1).onClick(function () {
  type = 'square'
})

var btn = ui.addButton('◯', 0.5, 0.8, 0.35, 0.1).onClick(function () {
  type = 'circle'
})

processing.settings(function (p) {
  p.size(p.displayWidth, p.displayHeight, p.P3D)
})

var size

processing.setup(function (p) {
  p.background(0)
  p.frameRate(25)
  c = 100
})

processing.draw(function (p) {
  p.noStroke()
  p.fill(0, 20)
  p.rect(0, 0, p.width, p.height)

  p.stroke(255)

  // we use dp units and convert them to pixels
  // this will make things adapt better to different screens
  p.strokeWeight(util.dpToPixels(1))
  p.fill(255, 20)

  size = p.dist(p.pmouseX, p.pmouseY, p.mouseX, p.mouseY)

  if (type === 'circle') {
    p.ellipse(p.mouseX, p.mouseY, size, size)
  } else {
    p.rect(p.mouseX, p.mouseY, size, size)
  }
})
