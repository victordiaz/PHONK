/*
 * \\\ Example: Notifications
 */

ui.addTitle(app.name)
ui.addSubtitle('Show app notifications, still not fully functioning')

ui.addButton('show notification', 0.1, 0.4, 0.8, 0.1).onClick(function (e) {

  app.notification.show({
    'id': 12,
    'ticker': 'ticker text',
    'title' : 'MyTitle 2',
    'description': 'MyDescription',
    'subtext': 'qq',
    'icon': 'patata2.png',
    'color': '#FFFF00',
    'autocancel': false
  })

})

// cancel the notification using the same id, in this case 12
ui.addButton('cancel', 0.1, 0.55, 0.8, 0.1).onClick(function (e) {
  app.notification.cancel(12)
})

ui.addButton('cancel all', 0.1, 0.7, 0.8, 0.1).onClick(function (e) {
  app.notification.cancelAll()
})
