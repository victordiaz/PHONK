/* 
*	Simple File Input and Output operations 
*   
*/ 

ui.toast("saving and loading data");

// create an Arrary with the data we want to save 
var data = new Array();
data.push("hello 1");
data.push("hello 2");
data.push("hello 3");
data.push("hello 4");
data.push("hello 5");

//saving data in file.txt 
fileio.saveStrings("file.txt", data);

//read data and store it in readData
var readData = fileio.loadStrings("file.txt");

var txt = ui.addText("stored and loaded data: \n", 10, 10);
//show in the console the data
for(var i = 0; i < readData.length; i++) { 
  console.log(readData[i]);  
  txt.append(readData[i] + "\n");
} 

//other methods 
//fileio.delete("fileName");
//fileio.moveFileToDir("fileName", "dir");
//fileio.moveDirToDir("dir", "dir");
//fileio.copyFileToDir("fileName", "dir");
//fileio.copyDirToDir("dir", "dir")
//fileio.rename("fileName", "newFileName");
//console.log(fileio.type("fileName"));
//fileio.createEmptyFile("file.txt");