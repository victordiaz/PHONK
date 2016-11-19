/*
 * \\\ Example: More Widgets
 *
 * More widgets available in Protocoder
 */
 
ui.addChoiceBox(['1', '2'], 0, 0, 1, 0.2).onSelected(function (data) {
  ui.toast(data.selected)
})

ui.addNumberPicker(0, 10, 0, 0.2, 1, 0.5).onSelected(function (data) {
  ui.toast(data.selected)
})

ui.addImageButton("patata2.png", 0, 0.7, 0.2, 0.2)
  .pressed('patata2_on.png')
  //.noBackground()
  .onClick(function (data) {
    ui.toast(data)
  })
