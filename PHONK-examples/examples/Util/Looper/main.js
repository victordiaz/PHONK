/*
 * Phonk Example: Looper / Repeat Interval
 *
 * Executes a function every T milliseconds
 */

ui.addTitle(app.name)

var txt = ui.addText('', 0.1, 0.15, 0.8, 0.45)
txt.props.background = '#000000'
txt.props.padding = 20
txt.props.textSize = 15

var loop = util.loop(5000, function () {
  txt.append('eat more veggies!\n')
})

// start / stop orientation sensor
ui.addToggle(['ON', 'OFF'], 0.1, 0.65, 0.2, 0.1).onChange(function (o) {
  if (o.checked) loop.start()
  else loop.stop()
})

// this is how you change the speed of the looper
ui.addSlider(0.1, 0.8, 0.8, 0.05).range(1000, 5000).onChange(function (e) {
  loop.speed(e.value)
  txtSpeed.text('repeating every ' + e.value.toFixed(0) + ' ms')
})

var txtSpeed = ui.addText('repeating every 5000 ms', 0.1, 0.85, 0.8, -1)
