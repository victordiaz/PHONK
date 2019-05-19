/*
 * Phonk Example: Callbacks
 *
 * Callbacks are basically functions that we
 * can pass to a function as an argument
 *
 */

ui.addTitle(app.name)
ui.addSubtitle('Check the source code to see how callbacks work')

// define a function that accepts a callback (function) as a parameter
function myfunction(callback) {
  callback(2) // call the function
}

// call the function and pass a function inside as a parameter
myfunction(function (value) {
  console.log(value)
})
