/*
 * Phonk Example: Date object
 */

ui.addTitle(app.name)

var d = new Date()

var txt = ui.addText('', 0.1, 0.15, 0.8, 0.5)
txt.props.background = '#000000'
txt.props.padding = 20
txt.props.textSize = 15
txt.append('full date: ' + d.toString())
txt.append('\ndate: ' + d.getDate())
txt.append('\nmonth: ' + d.getMonth())
txt.append('\nyear: ' + d.getFullYear())
