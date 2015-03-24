/*
*  Email example 
*
*
*/ 

var networkSettings = network.createEmailSettings();
networkSettings.host = "";
networkSettings.user = "";
networkSettings.password = "";
networkSettings.port = "";
networkSettings.auth = "";
networkSettings.ttl = "";


var from = "me@thisisaninventedmail.com";
var to = "you@thisisanotherinventedmail.com"
var subject = "From Protocoder with luv"; 
var text = "Hey, I'm sending this with Protocoder!";

network.sendEmail(from, to, subject, text, networkSettings);