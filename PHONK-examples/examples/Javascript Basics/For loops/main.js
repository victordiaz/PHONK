/*
 * \\\ Example: For Loop
 */

ui.addTitle(app.name)

var txt = ui.addText('Check the source code to see how for loops work', 0.1, 0.15, 0.8, -1)
txt.props.textSize = 30

// for loops have 3 parts
// init; condition; update
// the code inside the block will exectute as many times as the condition satisfies
for (var i = 0; i < 100; i++) {
  console.log('smile ' + i + ' times')
}
