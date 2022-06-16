/*
 * PHONK Example: WIFI scan
 *
 * Scans wifi networks giving a bunch of params for each one 
 */

ui.addTitle(app.name)
ui.addSubtitle('Scan WIFI networks')

var txt = ui.addTextList(0.1, 0.15, 0.8, 0.65).autoScroll(true)

ui.addButton('Scan Networks', 0.1, 0.85, 0.8, 0.1).onClick(function (e) {
  
  network.wifiScan(function (data) {
    for (var i = 0; i < data.networks.length; i++) {
      console.log(data.networks[i])
      txt.add(data.networks[i])
    }
  })
  
})
