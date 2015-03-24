/*
*	Description SimpleHttpServer 
*/

var server = network.startSimpleHttpServer(1111, function(url, method, header, parms, files) {
    console.log(url, method, header);
    
    //serves pure text to a given GET command
    if (url == "/helloworld") {
        console.log("got helloworld get petition!");
        return server.respond("hello world back!");
    
    //return files in the current project folder
    } else {
        return server.serveFile(url, header);;
    }
    
});