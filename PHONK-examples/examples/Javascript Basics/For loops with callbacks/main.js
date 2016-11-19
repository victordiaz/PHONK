/*
 * \\\ Example: For Loop with callbacks
 *
 * Javascript has a strange behaviour when attaching
 * a callback inside a loop. It always attach the last
 *
 * We need to do it the following way using a 'closure'
 */

 ui.addTitle(app.name)

var txt = ui.addText('Check the source code to see a snippet of loops with callbacks', 0.1, 0.15, 0.8, -1)
txt.props.textSize = 20

for (var i = 0; i < 5; i++) {
  (function (i) {
    ui.addButton(i, 0.1, 0.4 + 0.12 * i, 0.8, 0.1).onClick(function () {
      console.log(i)
    })
  })(i);
}
