/*
 * \\\ Example: DataList
 *
 * create and display a list of data
 */

var mydata = []

// lets fill the array with random data
for (var i = 0; i < 1000; i++) {
  mydata.push({'text': 'hola ' + i})
}

var list = ui.addList(0, 0, 1, 1, mydata,
  function () { // on create view
    return ui.newButton('')
  }, function (o) { // data binding
    var t = mydata[o.position].text
    o.view.setText(t)
    o.view.onClick(function () {
    ui.toast('pressed ' + t )
  })
})
