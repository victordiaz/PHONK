/*
 * \\\ Example: Objects
 */

ui.addTitle(app.name)

var txt = ui.addText('Check the source code to see how javascript objects work', 0.1, 0.15, 0.8, -1)
txt.props.textSize = 30

var myobject = { key1: 'val1', key2: 'val2'}
myobject.key3 = 'val3'

console.log(JSON.stringify(myobject))
