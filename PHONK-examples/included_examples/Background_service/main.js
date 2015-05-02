/*
* This is an example of background processes.
* Have in mind that is quite experimental (like whole Protocoder :P)
*
* Create a project appending "_service" to the name
* such as MyProject_service
* 
* Protocoder will treat it automatically as a background service
*
* Currently only one service can be active. More than one might lead 
* to some problems :)
*
*/

util.loop(5000, function() {
	device.vibrate(500);
}).start();