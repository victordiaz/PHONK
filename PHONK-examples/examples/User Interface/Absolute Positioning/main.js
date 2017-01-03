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

ui.positionMode('px')
ui.addButton('px', 200, 500, 100, 100)

ui.positionMode('dp')
ui.addButton('dp', 100, 250, 100, 100)

ui.positionMode('normalized') // default
ui.addButton('norm', 0.8, 0.8, 0.1, 0.1)
