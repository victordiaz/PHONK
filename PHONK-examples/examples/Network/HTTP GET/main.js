/*
 * \\\ Example: 	HTTP GET
 *
 * Do an HTTP GET request and get its content
 */

ui.addTitle(app.name)

network.httpGet('http://www.google.es/robots.txt', function (data) {
  console.log(data.status + ' ' + data.response);
})
