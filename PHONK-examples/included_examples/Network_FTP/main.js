/*
*	
*	FTP client and server 
*	still not fully working, contributions are welcomed 
*
*/

//1h 

if (false) {
    //OK set folder to current project
    //need callback for actions
    var ftpServer = network.createFtpServer(9292, function(info) {
        console.log(info);
    });
    
    ftpServer.addUser("user", "password", "/", true);
    ftpServer.start(); 
    ftpServer.stop();
    
    var ftpClient = network.createFtpConnection();
    
    ftpClient.connect("127.0.0.1", 9292, "user", "password", function(connected) {
       console.log(connected); 
    });
    
    ftpClient.getCurrentDir(function(currentDir) {
        console.log(currentDir);
    });
    
    ftpClient.changeDir("./sdcard", function(state) {
        console.log(state);
    });

    ftpClient.getFileList("./", function(list) {
        for (var i = 0; i < list.size(); i++) {
            console.log(list.get(i).type + " " + list.get(i).name); 
        }
    });
    
    ftpClient.download("./arduino_logo.png", "arduino2.png", function(progress) {
        console.log(progress)
    });
    
    ftpClient.upload("arduino_logo.png", "arduino_uploaded.png", "/", function(progress) {
        console.log(progress);
    });
    
    ftpClient.deleteFile("arduino_uploaded.png", function(status) {
        console.log(status);
    });
    
    ftpClient.disconnect(function(status) {
        console.log(status);
    });
    
}