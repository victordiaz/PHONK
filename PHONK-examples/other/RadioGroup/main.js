/*
 * \\\ Example: RadioGroup
 *
 */

 var radioGroup = ui.addRadioButtonGroup(0, 0.8)
 radioGroup.add('Option 1')
 radioGroup.add('Option 2')
 radioGroup.onSelected(function (d) {
   ui.toast('selected: ' + d)
 })
