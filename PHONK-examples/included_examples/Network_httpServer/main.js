/*
*	SimpleHttpServer 
*/

var server = network.createSimpleHttpServer(1111);


server.onNewRequest(function(url, method, header, parms, files) {
    console.log(url, method, header);
    
    //serves pure text to a given GET command
    if (url == "/helloworld") {
        console.log("got helloworld GET petition!");
        
        //if you want to do some UI call you must use as follows 
        app.runOnUiThread(function() {
             ui.toast("this shows on the ui!", 200); 
        });
        
        return server.response("hello world back!");
    //return files in the current project folder
    } else {
        return server.serveFile(url, header);;
    }
});
