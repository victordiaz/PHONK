/*
 * \\\ Example: Buttons
 *
 */

 ui.addText('text', 0, 0.1, 1, 0.1)


 var htmlText = ui.addText('lala', 0, 2100, 500, 100)
 htmlText.html('This is a <strong> HTML </strong> text')

 var font = util.loadFont('visitor2.ttf')
 var label = ui.addText('hola fonts', 20, 2200, 300, 200)
 label.textSize(80)
 label.color('#222222')
 label.font(font)
