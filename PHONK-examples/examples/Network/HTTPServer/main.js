/*
 *	PHONK Example: HttpServer
 */

ui.addTitle(app.name)
ui.addSubtitle('Creating a webserver in the port 1111')

var server = network.createHttpServer(1111)

server.onNewRequest(function (e) {
  console.log(e.uri + ' ' + e.method) //, e.header, e.params, e.files)

  // serves pure text to a given GET command
  switch (e.uri) {

    case '/':
      return server.response('PHONK!')

    case '/helloworld':
      console.log('got helloworld GET petition!')

      // if you want to do some UI call you must use as follows
      app.runOnUiThread(function () {
        ui.toast('this shows on the ui!')
      })

      return server.response('hello world back!')

    // return files in the current project folder
    default:
      return server.serveFile(e.uri)
  }
})

server.start()
