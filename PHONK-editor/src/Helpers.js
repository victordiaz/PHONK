const helpers = {}

// http://stackoverflow.com/questions/105034/create-guid-uuid-in-javascript
helpers.generateUUID = function () { // Public Domain/MIT
  var d = new Date().getTime()
  if (typeof performance !== 'undefined' && typeof performance.now === 'function') {
    d += performance.now() // use high-precision timer if available
  }
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
    var r = (d + Math.random() * 16) % 16 | 0
    d = Math.floor(d / 16)
    return (c === 'x' ? r : (r & 0x3 | 0x8)).toString(16)
  })
}


helpers.clearArray = function (dst) {
  while (dst.length) dst.pop()
}

helpers.copyArray = function (or, dst) {
  this.clearArray(dst)
  for (var i in or) {
    dst.push(or[i])
  }
}

helpers.mydragg = function () {
  return {
    move: function (divid, xpos, ypos) {
      divid.style.left = xpos + 'px'
      divid.style.top = ypos + 'px'
    },
    startMoving: function (divid, container, evt) {
      evt = evt || window.event

      var posX = evt.clientX
      var posY = evt.clientY
      var divTop = divid.style.top
      var divLeft = divid.style.left

      var eWi = parseInt(divid.style.width)
      var eHe = parseInt(divid.style.height)
      var cWi = parseInt(document.getElementById(container).style.width)
      var cHe = parseInt(document.getElementById(container).style.height)

      document.getElementById(container).style.cursor = 'move'
      divTop = divTop.replace('px', '')
      divLeft = divLeft.replace('px', '')
      var diffX = posX - divLeft
      var diffY = posY - divTop

      document.onmousemove = function (evt) {
        evt = evt || window.event
        var posX = evt.clientX
        var posY = evt.clientY
        var aX = posX - diffX
        var aY = posY - diffY

        if (aX < 0) aX = 0
        if (aY < 0) aY = 0
        if (aX + eWi > cWi) aX = cWi - eWi
        if (aY + eHe > cHe) aY = cHe - eHe

        helpers.mydragg.move(divid, aX, aY)
      }
    },
    stopMoving: function (container) {
      document.getElementById(container).style.cursor = 'default'
      document.onmousemove = function () { }
    }
  }
}

export default helpers