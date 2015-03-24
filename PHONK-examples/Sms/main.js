/*
*	This example might charge you some money since it uses 
*   the sms functionality
*   Uncomment the lines if you want to try it
*
*/


device.onSmsReceived(function(number, msg) { 
   console.log(number + " " + msg); 
});

//uncomment this lines to send sms  
//var phoneNumber = "12345"
//device.smsSend(phoneNumber, "hello");
