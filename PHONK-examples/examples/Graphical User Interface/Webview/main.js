/*
 * Phonk Example: Webview
 *
 * The Webview can load html files, html text or open a url
 */

ui.addTitle(app.name)

// load inlined content
var webview = ui.addWebView(0.1, 0.1, 0.8, 0.25)

function test () {
  device.vibrate(100)
  ui.toast('Hi!')
}

var inlineContent = '\
  <html> \
    <script> \
    function t() { console.log(\'cuakcuak\') } \
    </script> \
    <body style = "background: white"> \
      <button onclick="app.eval(\'device.vibrate(100)\');">vibrate</button> \
      <button onclick="app.eval(\'test()\');">test()</button> \
    </body> \
  </html>'
webview.loadData(inlineContent)

// load html file with transparent background
var webview2 = ui.addWebView(0.1, 0.4, 0.8, 0.25)
webview2.loadFile('hello.html')
webview2.props.background = '#00FFFFFF'

// load Url
var webview3 = ui.addWebView(0.1, 0.7, 0.8, 0.25)
webview3.loadUrl('http://www.phonk.app')
