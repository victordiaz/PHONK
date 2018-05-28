/*
 * \\\ Example: Buttons
 *
 */

 var btn = ui.addButton('Button', 0, 0, 1, 0.1).onClick(function () {

 })


 // Add an image button with a background
 ui.addImageButton(400, 1050, 300, 300,'patata2.png', false).onClick(function (val){
   console.log(val)
 })
