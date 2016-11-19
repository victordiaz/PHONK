/*
 * \\\ Example: NFC
 */

ui.addTitle(app.name)

var infoTxt = ui.addText('tap on a NFC to get the id and content or press the button and get close to the NFC to write it', 0.1, 0.15, 0.8, -1)
infoTxt.props.textSize = 30

// when tapping on a nfc the id and content will be displayed on the label
device.nfc.onNewData(function (id, data) {
  console.log('the nfc id is: ' + id, data)
  infoTxt.html('<strong>id: </strong>' + id + '<br /> <strong>data: </strong>' + data)
})

// when we click
// the next touched nfc will be written with the data
ui.addButton('Write to NFC', 0, 0.8, 1, 0.2).onClick(function (){
	device.nfc.write('this is a test', function () {
		infoTxt.text('data written')
	})
})
