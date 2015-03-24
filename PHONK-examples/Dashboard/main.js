/* 
*   Communication with the dashboard  
*
*   Dashboard is an element in the Web Editor where you 
*   can add widgets that interact with your device 
*   
*   You can remote control it!
*/ 

//---------- PHONE UI ---------------------

//add a plot on the dashboard 
var plot;
ui.addButton("Add plot", 0, 0, 500, 100).onClick(function(){
    plot = dashboard.addPlot("name", 600, 100, 200, 100, 0, 10);
});

//update the plot 
ui.addButton("Update plot randomly", 0, 100, 500, 100).onClick(function(){
    plot.update(10 * Math.random());
});

//show and hide the dashboard 
ui.addToggle("Show Hide dashboard", 0, 200, 500, 100, false).onChange(function(b){
    dashboard.show(b);
});

//change the text  
ui.addButton("hola", 0, 300, 500, 100).onClick(function(){
    textChange.setText("hola");
});

ui.addButton("adios", 0, 400, 500, 100).onClick(function(){
    textChange.setText("adios");
});



//---------- DASHBOARD UI ---------------------
dashboard.backgroundColor("#EE000000");
var text = dashboard.addText("Use the Dashboard to see information and interact remotely with your device", 50, 50, 200, 100, 28, "#FFFFFF");
var textChange = dashboard.addText("This text can change", 50, 250, 200, 100, 28, "#FF00FF");

slider = dashboard.addSlider("name", 50, 400, 200, 100, 0, 100).onChange(function(val) {
    console.log(val);
    device.vibrate(100);
});

input = dashboard.addInput("say it", 50, 450, 200, 100).onSubmit(function(val) {
     console.log(val);
     media.textToSpeech(val);
     device.vibrate(100);
});

//add a button on the webpapp and when clicked will execute the inner function
var webbutton = dashboard.addButton("hola", 310, 100, 150, 50).onClick(function() {
    ui.toast("hola", 200);
    device.vibrate(500);
});

// //add custom html
dashboard.addHtml("<a href = 'http://www.protocoder.org' style='font-size:20px'> This is a link to Protocoder! </a>", 280, 280);
