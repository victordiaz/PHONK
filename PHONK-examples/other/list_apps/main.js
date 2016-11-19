
var apps = device.listInstalledApps()

var y = 0.3
var d = 0.0
for (var i = 0; i < 10 /* apps.size() */; i++) {
  console.log(apps.get(i).iconBitmap)
  var img = ui.addImage(0, y, 0.05, 0.05).load(apps.get(i).iconBitmap)
  img.setAlpha(0)
  img.animate().x(200).setDuration(1000).alpha(1.0).setStartDelay(d)
  d += 50

  y += 0.05
}
