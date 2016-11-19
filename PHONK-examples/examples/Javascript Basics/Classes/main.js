/*
 * \\\ Example: Classes
 *
 * Javascript doesnt have classes per se,
 * but we can get similar results using the prototype
 *
 */

ui.addTitle(app.name)

var txt = ui.addText('Check the source code to see how classes work', 0.1, 0.15, 0.8, -1)
txt.props.textSize = 30

var Robot = function (name) {
  this.name = name;
};

Robot.prototype.say = function(text) {
  console.log(text + ' My name is ' + this.name + '.')
};

var robot1 = new Robot("WKM")
var robot2 = new Robot("1010110")

robot1.say('hello,')
robot2.say('how are you?')
