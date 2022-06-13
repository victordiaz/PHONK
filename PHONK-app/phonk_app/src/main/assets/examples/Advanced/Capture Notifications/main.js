/*
 *  Phonk Notifications
 *  
 */

ui.addTitle(app.name)
ui.addSubtitle('Get notifications from other apps')

device.onNewNotification(function (e) {
  console.log(e)
})
