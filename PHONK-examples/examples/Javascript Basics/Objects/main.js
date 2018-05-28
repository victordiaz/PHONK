/*
 * Phonk Example: Objects
 */

ui.addTitle(app.name)
ui.addSubtitle('Check the source code to see how data objects work')

var myobject = { key1: 'val1', key2: 'val2'}
myobject.key3 = 'val3'

console.log(JSON.stringify(myobject))
