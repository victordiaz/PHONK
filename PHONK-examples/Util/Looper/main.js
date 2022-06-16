/*
 * PHONK Example: Looper / Repeat Interval
 *
 * Executes a function every T milliseconds
 */

ui.addTitle(app.name)

var txt = ui.addTextList(0.1, 0.15, 0.8, 0.45).autoScroll(true)
txt.props.background = '#000000'
txt.props.padding = 20
txt.props.textSize = 15

// this value is in milliseconds
var repeatingSpeed = 2000

var i = 0
var loop = util.loop(repeatingSpeed, function () {
  txt.add(i++ + ') eat more veggies!')
})

// start / stop orientation sensor
ui.addToggle(['ON', 'OFF'], 0.1, 0.65, 0.2, 0.1).onChange(function (o) {
  if (o.checked) loop.start()
  else loop.stop()
})

// this is how you change the speed of the looper
ui.addSlider(0.1, 0.8, 0.8, 0.05).range(500, 5000).value(repeatingSpeed).onRelease(function (e) {
  loop.speed(e.value)
  txtSpeed.text('repeating every ' + e.value.toFixed(0) + ' ms')
})

var txtSpeed = ui.addText('repeating every ' + repeatingSpeed + ' ms', 0.1, 0.9, 0.8, -1)
