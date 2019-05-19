/*
 * Phonk Example: App Lifecycle
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

var txt = ui.addTextList(0.1, 0.15, 0.8, 0.3)
txt.props.textSize = 15

function log(msg) {
  txt.add(msg + '\n')
  console.log(msg)
}
