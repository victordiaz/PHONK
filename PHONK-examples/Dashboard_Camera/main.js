/*
*  Streams the camera preview to the dashboard
*
*/

var camera = ui.addCameraView("back", 0, 0, 1000, 800);
var dashboardCamera = dashboard.addCameraPreview(0, 0, 320, 240);

ui.addButton("Stream to Dashboard", 0, 0).onClick(function() {
    camera.setPreviewSize(320, 240);

    camera.onNewStreamFrame(function(imgData) { 
        dashboardCamera.update(imgData);
    });
});