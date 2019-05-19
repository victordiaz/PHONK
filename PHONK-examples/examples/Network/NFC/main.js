/*
 * Phonk Example: NFC
 */

ui.addTitle(app.name)
ui.addSubtitle('tap on a NFC to get the id and content or press the button and get close to the NFC to write it')

var infoTxt = ui.addText('', 0.1, 0.35, 0.8, -1)
infoTxt.props.textSize = 30

// when tapping on a nfc the id and content will be displayed on the label
var nfc = network.startNFC()

nfc.onNewData(function (e) {
  infoTxt.html('<strong>id: </strong>' + e.id + '<br /> <strong>data: </strong>' + e.data)
})

// when we click the next touched nfc will be written with the data
ui.addButton('Write to NFC', 0, 0.8, 1, 0.2).onClick(function (){
	nfc.write('this is a test').onDataWritten(function () {
		infoTxt.text('data written')
	})
})
