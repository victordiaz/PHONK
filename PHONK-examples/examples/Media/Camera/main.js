/*
 * PHONK Example: Camera
 *
 * Show camera view and capture and image when button is clicked
 * if you are in the WebIDE an image will be shown in the console
 */

ui.addTitle(app.name)

// add camera
var camera = media.useCamera('back')
ui.addView(camera, 0, 0, 1, 1)

camera.onPictureTaken(function (data) {
  // saveImage(bitmapData, name, imageType, quality)
  fileio.saveImage(data.bitmap, 'picture.jpg', 'jpg', 50)
  console.logImage(app.path() + 'picture.jpg')
})

// take a picture and save it
ui.addButton('Take pic', 0.2, 0.8, 0.2, 0.1).onClick(function () {
  camera.takePicture()
})

// toggle flash on and off
ui.addToggle('Flash', 0.6, 0.8, 0.2, 0.1).onChange(function (e) {
  camera.flashLight(e.checked)
})
