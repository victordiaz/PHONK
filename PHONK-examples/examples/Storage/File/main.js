/*
 * \\\ Example:	File Storage
 *
 * Simple File Write and Read operation
 */

ui.addTitle(app.name)

// create an Arrary with the data we want to save
var data = []
data.push('hello 1')
data.push('hello 2')
data.push('hello 3')
data.push('hello 4')
data.push('hello 5')

// saving data in file.txt
fileio.saveStrings('file.txt', data)

// read data and store it in readData
var readData = fileio.loadStrings('file.txt')

var txt = ui.addText('stored and loaded data: \n\n', 0.1, 0.15, 0.8, 0.5)
txt.props.background = '#000000'
txt.props.padding = 20
txt.props.textSize = 15

// show in the console the data
for(var i = 0; i < readData.length; i++) {
  console.log(readData[i])
  txt.append(readData[i] + '\n')
}
