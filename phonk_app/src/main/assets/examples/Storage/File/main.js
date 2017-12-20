/*
 * \\\ Example:	File Storage
 *
 * Simple File Write and Read operation
 */

ui.addTitle(app.name)
ui.addSubtitle('Store and load data')

// create an Arrary with the data we want to save
var data = []
data.push('hello 1')
data.push('hello 2')
data.push('hello 3')
data.push('hello 4')
data.push('hello 5')

// saving data in file.txt
fileio.saveTextToFile(data, 'file.txt')

// read data and store it in readData
var readData = fileio.loadTextFromFile('file.txt')

var txt = ui.addTextList(0.1, 0.35, 0.8, 0.5)
txt.add(readData)
