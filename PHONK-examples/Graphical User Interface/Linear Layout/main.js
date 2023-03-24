function divide(linearLayout, dividerWidth, dividerHeight) {
  // adds gaps between views
  var divider = new android.graphics.drawable.ShapeDrawable()
  divider.setAlpha(0)
  divider.setIntrinsicWidth(dividerWidth)
  divider.setIntrinsicHeight(dividerHeight)
  linearLayout.setDividerDrawable(divider)
  linearLayout.setShowDividers(android.widget.LinearLayout.SHOW_DIVIDER_MIDDLE)
  return linearLayout
}

function info(name, view) {
  // shows a popup with layout information
  var s = ''
  while (true) {
    var n = view.getClass().getSimpleName()
    if (n == 'PButton') s += name
    else if (n == 'PLinearLayout') s += view.props.orientation
    else break

    var p = view.getLayoutParams()
    s += ' || w: ' + view.props.w + ', h: ' + view.props.h + ', weight: ' + p.weight + '\n'
    view = view.getParent()
  }
  ui.popup().description(s).show()
}


// make borders visible
var defaultProps = ui.getProps()
defaultProps.borderWidth = 1
defaultProps.borderColor = '#ffffff'


ui.addTitle(app.name)

constButton = ui.addButton('Constants', 0.75, 0.02)
constButton.props.padding = 10
constButton.onClick(() => ui.popup().description('-1 .. maximum size\n-2 .. minimum size').show())


var column = divide(ui.addLinearLayout(0, 0.08, 1, 0.92), 1, 20)
column.props.padding = 0


var topView = divide(ui.newView('linearLayout', {padding: 20}).orientation('horizontal'), 20, 1)

var topLeft = ui.newView('button', {text: 'left'})
topLeft.onClick(() => info('top left', topLeft))
topView.add(topLeft, 'top left')  // width: -2 (min), height: -2 (min), weight: 0 (dont' resize)

var topFill = ui.newView('button', {text: 'fill'})
topFill.onClick(() => info('top fill', topFill))
topView.add(topFill, 'top fill', 1)  // min width, min height, weight: 1 (resize)

var topRight = ui.newView('button', {text: 'right'})
topRight.onClick(() => info('top right', topRight))
topView.add(topRight, 'top right')

column.add(topView, 'top', -1, -2, 0)  // width: -1 (max), height: -2 (min), weight: 0 (don't resize)


var fillView = ui.newView('linearLayout', {padding: 10}).orientation('horizontal')

var fillLeft = ui.newView('button', {textAlign: 'right', text: 'fill left, weight: 1'})
fillLeft.onClick(() => info('fill left', fillLeft))
fillView.add(fillLeft, 'fill left', -1, -1, 1)

var fillRight = ui.newView('button', {text: 'fill right, weight: 2'})
fillRight.onClick(() => info('fill right', fillRight))
fillView.add(fillRight, 'fill right', -1, -1, 2)

column.add(fillView, 'fill', -1, -2, 1)


var bottomView = ui.newView('button', {textAlign: 'center', text: 'bottom'})
bottomView.onClick(() => info('bottom', bottomView))

column.add(bottomView, 'bottom', -1, -2, 0)
