/*
 * PHONK example: Share
 *
 * This script shows content provided by the share
 * menu in Android.
 */

ui.addTitle(app.name)

var txt = ui.addText('', 0.1, 0.3, 0.8, 0.5)

// Check if we receive some data when the
// script launches
if (app.intentData.shareType) {
  txt.append('\n' + app.intentData.shareType)
  txt.append('\n' + app.intentData.shareContent)
} else {
  txt.text('No data shared')
}