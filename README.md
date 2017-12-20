Protocoder
==========
[![Build Status](https://travis-ci.org/Protocoder/Protocoder.svg?branch=develop)](https://travis-ci.org/Protocoder/Protocoder)

Protocoder is a self-contained coding environment + framework in Javascript for quick prototyping on Android devices.

Just install the app in your Android device and access the web IDE from your computer using the minicloud created in your phone.
Code in javascript using the protocoder framework. No needs to write dozends of lines to access sensors or write an UI, simple to use, fast to code.

```
//how to get sensor data
sensors.accelerometer.onChange(function(x, y, z)) { 
	console.log(x + " " + " " + y + " " + z); 
}

//send and sms
android.sendSMS(number, "text");

//play a video
ui.addVideoView("fileName", 0, 0, 500, 200);
```

It uses a webserver and a websockets server inside the project to do some of the magic.

It has support of most android hardware functionality, networking using OSC and websockets, audio synthesis and processing using Pure Data though libPd, OSMmaps, IOIO support and muuuuuuuch more.

How to compile 
--------------
Clone the project, import it in Android Studio, make sure the API version you are using to compile is 21 or greater.