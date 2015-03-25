/*
*	With this code you are able to upload multipart content to a webserver
*	An example of a php file that will handle the upload is included 
*
*/

var url = "http://www.yoururl.domain/upload.php"

var opt = [];
opt.push({ "name" : "field_1", "content" : "hello", "type" : "text" })
opt.push({ "name" : "field_2", "content" : "world", "type" : "text" })
opt.push({ "name" : "userfile", "content" : "8-rocket.png", "type" : "file"})

network.httpPost(url, opt, function(e) {
    console.log(e);
}); 