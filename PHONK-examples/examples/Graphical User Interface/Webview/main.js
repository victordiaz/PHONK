/*
 * Phonk Example: Webview
 *
 * The Webview can load html files, html text or open a url
 */

ui.addTitle(app.name)

// load the content
var webview = ui.addWebView(0.1, 0.1, 0.8, 0.25)
// html content to load
var content = '\
  <html> \
    <body style = "background: white">\
      <h1>Inline HTML</h1>\
      <p>lalallalalalal</p>\
    </body>\
  </html>'
webview.loadData(content)

// load html file
var webview2 = ui.addWebView(0.1, 0.4, 0.8, 0.25)
webview2.loadFile('hello.html')

// load Url
var webview3 = ui.addWebView(0.1, 0.7, 0.8, 0.25)
webview3.loadUrl('http://www.protocoder.org')
