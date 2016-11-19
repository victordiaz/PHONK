/*
 * \\\ Example: Sliders and Knobs
 *
 */

var slider = ui.addSlider(0, 0.1, 1, 0.1, 100, 50).onChange(function (val) {
   console.log(val)
 })
