/*
 * PHONK Example: HTTP GET
 *
 * Do an HTTP GET request and get its content
 */

ui.addTitle(app.name)

var result = ui.addText(0.1, 0.15, 0.8, 0.6)
result.props.textSize = 10

network.httpRequest({
  method: 'GET',
  url: 'https://en.wikipedia.org/wiki/Antarctica?action=raw'
}).onResponse(function (e) {
  result.text(e.status + ' ' + e.response)
})
