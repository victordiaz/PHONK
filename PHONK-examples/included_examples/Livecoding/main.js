/*
* Livecoding feedback example 
* Within the webIde select a line of code while 
* executing the script and press Cmd + Shift + X in Mac 
* or Control + Shift + X in Windows to live execute the line 
* With this feedback you will what is been executed in the device 
*/

var l = app.liveCodingFeedback()
			.autoHide(true)
			.textSize(25)
			.write("qqw")
			.backgroundColor("#55000055")
			.show(true);