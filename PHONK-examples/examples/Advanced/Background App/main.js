/*
 * This is an example of background processes.
 * Create a project appending "_service" to the name
 * such as MyProject_service
 *
 * Protocoder will treat it automatically as a background service
 */

util.loop(5000, function () {
  console.log(':)')
	device.vibrate(500)
}).start()
