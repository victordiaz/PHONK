/*
 *  \\\ Example: QR Codes
 */

ui.addTitle(app.name)
ui.addSubtitle('Generate a QR code from text')

var img = ui.addImage(0.2, 0.2, 0.6, 0.6)

var bmp = media.generateQRCode('this text is encoded in a QR image')
img.load(bmp)
