/*
 * \\\ Example: Delay
 *
 * Execute a function after T milliseconds
 */

ui.addTitle(app.name)

var txt = ui.addText('wait 5000 ms... \n\n', 0.1, 0.15, 0.8, 0.5)
txt.props.background = '#000000'
txt.props.padding = 20
txt.props.textSize = 15

// any code inside will be delayed T milliseconds
util.delay(5000, function() {
  txt.append('helloooo \n')
})
