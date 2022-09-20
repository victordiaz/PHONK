/*
 * PHONK Example: Sliders
 *
 * Shows every possible slider configuration.
 * 'direct' slider moves the slider to the position you clicked
 *  while 'drag' slider moves only if you move you finger.
 */

ui.addTitle(app.name)

ui.addSlider(0, 0, 0.24, 0.24)
  .range(0, 100)
  .value(50)
  .text('direct')
  .mode('direct')
  .verticalMode(true)

ui.addSlider(0.25, 0, 0.24, 0.24)
  .range(0, 100)
  .value(50)
  .text('drag')
  .mode('drag')
  .verticalMode(true)

ui.addSlider(0.5, 0, 0.24, 0.24)
  .range(0, 100)
  .text('direct')
  .mode('direct')
  .verticalMode(true)

ui.addSlider(0.75, 0, 0.24, 0.24)
  .range(0, 100)
  .text('drag')
  .mode('drag')
  .verticalMode(true)

ui.addSlider(0, 0.25, 0.49, 0.24)
  .range(0, 100)
  .value(50)
  .text('direct')
  .mode('direct')

ui.addSlider(0.5, 0.25, 0.49, 0.24)
  .range(0, 100)
  .value(50)
  .text('drag')
  .mode('drag')

ui.addSlider(0, 0.5, 0.49, 0.24)
  .range(0, 100)
  .text('direct')
  .mode('direct')

ui.addSlider(0.5, 0.5, 0.49, 0.24)
  .range(0, 100)
  .text('drag')
  .mode('drag')

ui.addSlider(0, 0.75, 0.24, 0.24)
  .range(0, 100)
  .value(50)
  .mode('direct')
  .verticalMode(true)

ui.addSlider(0.25, 0.75, 0.24, 0.24)
  .range(0, 100)
  .value(50)
  .mode('drag')
  .verticalMode(true)

ui.addSlider(0.5, 0.75, 0.24, 0.24)
  .range(0, 100)
  .mode('direct')
  .verticalMode(false)

ui.addSlider(0.75, 0.75, 0.24, 0.24)
  .range(0, 100)
  .mode('drag')
  .verticalMode(false)
