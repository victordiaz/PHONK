var notification = app.notification(12345, 'img.png', 'title', 'description')

notification.onClick(function (e) {
  console.log('notification ' + e.id + ' is clicked ')
}
