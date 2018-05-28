/*
 * Phonk Example: 	HTTP GET
 *
 * Do an HTTP GET request and get its content
 */

ui.addTitle(app.name)

var result = ui.addText(0.1, 0.15, 0.8, 0.6)
result.props.textSize = 10

ui.addButton('GET ROBOTS.TXT', 0.1, 0.85, 0.8, 0.1).onClick(function () {
  network.httpGet('http://www.google.es/robots.txt', function (data) {
    result.text(data.status + ' ' + data.response);
  })
})
