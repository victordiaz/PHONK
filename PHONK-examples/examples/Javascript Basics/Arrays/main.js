/*
 * \\\ Example: Arrays
 */

ui.addTitle(app.name)

var txt = ui.addText('Check the source code to see how arrays work', 0.1, 0.15, 0.8, -1)
txt.props.textSize = 30

// original array
var myArray = ['one', 'two', 'three']

// add one element at the end
myArray.push('four')

// remove 1 element in the position 3
myArray.splice(3, 1)
