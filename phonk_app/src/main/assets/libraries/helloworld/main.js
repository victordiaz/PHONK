/*
*   Title: Class example 
*   Description: Couple of ways to say hello
*   Author: Victor Diaz
*   Usage: 
*           var obj = new Com();
*           obj.method1(100);
*           obj.method2();
*
*/

//class
var MyClass = function() {
  
    //vars 
    this.salutation = "hola";
    
    //methods 
    this.method1 = function(value) {
        var context = Packages.android.content.Context;
        var vibrator = Activity.getSystemService(context.VIBRATOR_SERVICE);
        vibrator.vibrate(value);
    }
    
    this.method2 = function() {
        media.textToSpeech(this.salutation);
    }
}