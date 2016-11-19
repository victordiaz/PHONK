/*
 * \\\ Example: App Lifecycle
 *
 *  Each method is called in each app state
 */

 ui.addTitle(app.name)

function onCreate() {
  log('onCreate')
}

function onPause() {
  log('onPause')
}

function onResume() {
  log('onResume')
}

function onDestroy() {
  log('onDestroy')
}

var txt = ui.addText('', 0.1, 0.15, 0.8, 0.3)
txt.props.background = '#000000'
txt.props.padding = 20
txt.props.textSize = 15

function log(msg) {
  txt.append(msg + '\n')
  console.log(msg)
}
