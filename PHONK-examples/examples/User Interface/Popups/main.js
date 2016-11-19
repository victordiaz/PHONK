/*
 * \\\ Example: Dialogs
 */ 
 
var p1 = ui.popup()
  .title('This is a title')
  .description('and this is a description, or not...')
  .ok('yei')
  .cancel('nei')
  // .multiChoice(['1', '2', '3', '4', '5'], [false, true, false, false, false])
  // .size(2, 2)
  .onAction(function (data) {
    ui.toast(data.answer)
  })
  
ui.addButton("Show Popup", 0, 0, 1, 1).onClick(function () {
  p1.show()
})


/*
ui.popupInput(title, function (data) {
  ui.toast(data.answer)
})
*/
