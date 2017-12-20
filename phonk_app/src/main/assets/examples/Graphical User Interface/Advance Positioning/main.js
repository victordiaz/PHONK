/*
 * \\\ Example: Absolute Positioning
 *
 * By default all the widgets are positioned
 * using normalized units. If you need more control
 * You can change de positioning mode to
 *
 * dp - density independent units
 * px - pixels
 * normalized - 0 to 1 normalized values
 */

ui.addTitle(app.name)
ui.addSubtitle('Position widgets using dp, px and normalized units')

ui.addButton('px', '200px', '500px', '100px', '100px')
ui.addButton('dp', '100dp', '250dp', '100dp', '100dp')
ui.addButton('norm', 0.8, 0.5, 0.1, 0.1)

ui.addButton('h', 0.2, 0.7, '0.2h', '0.2h')
ui.addButton('w', 0.6, 0.7, '0.2w', '0.2w')
