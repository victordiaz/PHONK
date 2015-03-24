/*
*	HTTP GET petition
*
*/

network.httpGet("http://www.google.es", function(status, response) { 
    console.log(status + " " + response);   
});