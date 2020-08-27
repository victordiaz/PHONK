/*
 *  PHONK Example: Styling & Theming
 *
 *  Changing the theme will apply the style to all subsequent added views
 *  For more granularity each view can be styled individual
 *
 *  As you could see this project has a custom icon. You just have to include
 *  a not too big PNG image named icon.png inside the project and that's all!
 */

ui.setTheme({
  background: '#000055',
  primary: '#FF0000',
  secondary: '#00FF00',
  textPrimary: '#FFFF00',
  animationOnViewAdd: true
})
console.log(ui.getTheme()) // print the theme properties

ui.addTitle(app.name)

var slider = ui.addSlider(0.1, 0.5, 0.8, 0.2).mode('drag').onChange(function (e) {
  console.log(e)
})

console.log(slider.getProps()) // print all the slider properties
slider.props.slider = '#00FF00' // change individual property

var btn = ui.addButton('Click me', 0.1, 0.2, 0.8, 0.2).onClick(function () {
  console.log('clicked')
})

// change many properties at once
btn.setProps({
  background: '#bbbb00',
  textSize: 35
})
