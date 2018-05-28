/*
 * Phonk Example: Dialogs
 */

ui.addTitle(app.name)
ui.addSubtitle('Shows a popup with different modes')

var p1 = ui.popup()
  .title('Information Dialog')
  .description('this is a description')
  .ok('OK')
  .cancel('NO')
  .onAction(function (data) {
    ui.toast(data.accept)
    console.log(data)
  })

var p2 = ui.popup()
  .title('Choose one option')
  .choice(['1', '2', '3'])
  .onAction(function (data) {
    ui.toast(data.answer)
    console.log(data)
  })

var p3 = ui.popup()
  .title('Choose one option')
  .ok('OK')
  .multiChoice(['1', '2', '3', '4', '5'], [false, true, false, false, false])
  .onAction(function (data) {
    console.log(data)
  })

var p4 = ui.popup()
  .title('How many people live on earth?')
  .ok('ok')
  .input('Write something here')
  .onAction(function (data) {
    ui.toast(data.answer)
    console.log(data)
  })


ui.addButton("Information Dialog", 0.1, 0.2, 0.8, 0.15).onClick(function () {
  p1.show()
})

ui.addButton("Choice Dialog", 0.1, 0.4, 0.8, 0.15).onClick(function () {
  p2.show()
})

ui.addButton("Multiple Choice Dialog", 0.1, 0.6, 0.8, 0.15).onClick(function () {
  p3.show()
})

ui.addButton("Input Text Dialog", 0.1, 0.8, 0.8, 0.15).onClick(function () {
  p4.show()
})
