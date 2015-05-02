/* 
*	GPS example 
* 
* 	Protocoder works with real GPS, therefore
*	you need be outdoor   
*/ 

// Labels to hold lat, lng & city name values of current location
var latTxt = ui.addText("Latitude : ", 10, 100, 500, 100);
var lonTxt = ui.addText("Longitude : ", 10, 200, 500, 100);
var altTxt = ui.addText("City : ", 10, 300, 500, 100);

//as demo purposes we are going to use google static maps 
//where for each update it will show an image of your current location 

//we start in latitude and longitude 0, 0
var map	= ui.addImage("https://maps.googleapis.com/maps/api/staticmap?center=0,0&zoom=20&size=700x500&sensor=false", 0, 400, 700, 500);

//for each GPS update the image and values are changed 
sensors.gps.onChange(function (lat, lon, alt, speed, bearing) { 
    latTxt.setText("Latitude : " + lat);
    lonTxt.setText("Longitude : " + lon);
    altTxt.setText("Altitude : " + alt);
    
    var url = "https://maps.googleapis.com/maps/api/staticmap?center="+lat+","+lon+"&zoom=20&size=700x500&sensor=false";
    map.setImage(url)
});