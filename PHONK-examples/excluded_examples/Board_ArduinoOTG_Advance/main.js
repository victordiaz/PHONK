/*
*	Upload .hex files directly from your phone
*	using Physicaloid 
*
*   These are the supported boards 
*   ARDUINO_UNO, ARDUINO_DUEMILANOVE_328, ARDUINO_DUEMILANOVE_168
*   ARDUINO_NANO_328, ARDUINO_NANO_168, ARDUINO_MEGA_2560_ADK
*   ARDUINO_MEGA_1280, ARDUINO_MINI_328, ARDUINO_MINI_168
*   ARDUINO_ETHERNET, ARDUINO_FIO, ARDUINO_BT_328
*   ARDUINO_BT_168, ARDUINO_LILYPAD_328, ARDUINO_LILYPAD_168
*   ARDUINO_PRO_5V_328, ARDUINO_PRO_5V_168, ARDUINO_PRO_33V_328
*   ARDUINO_PRO_33V_168, ARDUINO_NG_168, ARDUINO_NG_8
*   BALANDUINO, POCKETDUINO, PERIDOT
*   FREADUINO, BQ_ZUM
*/
var l = fileio.listFiles(".", ".hex");
var hexFileSelected;

ui.addText("Upload a blinking sketch to the arduino", 10, 10);
ui.addChoiceBox(20, 100, 200, 200, l, function(selected) {
   hexFileSelected = selected;
   ui.backgroundColor(255, 255, 255);
});
var progressBar = ui.addProgressBar(20, 300, 450, 1, 100);

ui.addButton("Upload", 250, 100, 200, 200).onClick(update);

function update() {
    var arduino = boards.startArduino(function(status) {
        console.log("connected " + status)
    });

    arduino.upload(arduino.ARDUINO_BT_328, hexFileSelected, function(p) {
        console.log(p);
        progressBar.setProgress(p);
        if (p == 100) { 
            ui.backgroundColor(0, 255, 0);
            
        }
    });
}