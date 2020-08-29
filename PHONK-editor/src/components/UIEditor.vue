<template>
  <div id = "uieditor">
    <div id = "view-area">
      <!-- View list -->
      <div id = "view-list" class = "block">
        <button v-for = "v in viewTypes" @click = "addView(v)">{{ v }}</button>
        <button @click = "clearViews()">x</button>
      </div>

      <!-- properties -->
      <div v-if = "viewSelected !== null" id = "properties" class = "block">
        <div class = "view-property" v-for = "(p, k) in viewSelected.props">
          <label>{{k}}</label>
          <input
            :value = "viewSelected.props[k]"
            v-on:keydown.enter = "editView($event.target.value, k)"
            v-on:keydown.up = "increase(viewSelected, k, 0.1)"
            v-on:keydown.down = "increase(viewSelected, k, -0.1)"
            :placeholder="k" />
        </div>
      </div>

      <!-- treeView -->
        <div id = "tree-view" class = "block">
          <div v-for = "v in treeView">
            <button class = "level-1">{{v.name}}</button>
            <div v-for = "c in v.children">
              <button class = "level-2" :class = "{ 'selected': c.props.id === viewSelected.props.id }" @click="viewSelected = c">{{ c.name }}</button>
            </div>
          </div>
        </div>

    </div>
    <div id = "canvas">
      <div id = "device" :style="deviceStyle" >
        <div class = "remote-views">
          <!-- {{ remoteViews.updateNum }} -->
          <img id = "even" :class = "{ 'selected': remoteViews.updateNum % 2 }" v-if = "remoteViews.imageBytesEven" :src="'data:image/jpeg;base64,' + remoteViews.imageBytesEven" :key = updateEven />
          <img id = "odd" :class = "{ 'selected': !(remoteViews.updateNum % 2) }" v-if = "remoteViews.imageBytesOdd" :src="'data:image/jpeg;base64,' + remoteViews.imageBytesOdd" :key = updateOdd />
        </div>

        <div class = "placed-views">
          <div class = "view"
            v-for = "v in treeViewFlatten"
            :style = "viewStyle(v)"
            :class = "{ 'selected': v.props.id === viewSelected.props.id }"
            v-on:mousedown="selectView(v)"
            v-on:mouseup="q2()"
            v-on:mouseleave="q3()"
            v-on:mousemove="move()">
            <span>{{v.name}} <!-- {{viewStyle(v)}}--> </span>
          </div>
        </div>
      </div>
      <div id = "cursor">{{ cursor.normalized.x.toFixed(3) }} {{ cursor.normalized.y.toFixed(3) }} {{ initialPos }}</div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'UIEditor',
  data () {
    return {
      viewTypes: ['linearLayout', 'list', 'map', 'canvas', 'touchPad', 'plot', 'webView', 'image', 'radioButtonGroup', 'loader', 'matrix', 'knob', 'slider', 'pager', 'toggle', 'input', 'textArea', 'text', 'textList', 'button', 'imageButton'],
      device: { width: '300', height: '500' },
      button: { x: 0, y: 0 },
      cursor: {
        raw: { x: 0, y: 0 },
        normalized: { x: 0, y: 0 }
      },
      views: [],
      treeView: [],
      viewSelected: {
        props: { id: '' }
      },
      initialPos: { x: 0, y: 0 },
      isMoving: true,
      ws: null,
      wsIsConnected: false,
      reconnectionInterval: null,
      remoteViews: {
        imageBytesEven: null,
        imageBytesOdd: null,
        updateNum: 0
      }
    }
  },
  computed: {
    deviceStyle () {
      return {
        width: this.device.width + 'px',
        height: this.device.height + 'px'
      }
    },
    updateEven () {
      return this.remoteViews.updateNum + 'e'
    },
    updateOdd () {
      return this.remoteViews.updateNum + 'o'
    },
    treeViewFlatten () {
      // console.log('qq1', this.treeView.length)
      if (this.treeView.length === 0) return []

      // console.log('qq2')

      let views = []
      views.push(this.treeView[0])

      this.treeView[0].children.forEach(c => {
        views.push(c)
      })
      return views
    }
  },
  methods: {
    getRandomString () {
      let r = Math.random().toString(36).substring(7)
      return r
    },
    websockets_init () {
      var url = 'ws://' + window.location.hostname + ':2525'
      console.log('trying to connect to ' + url)
      this.ws = new WebSocket(url)

      this.ws.onerror = (e) => {
        console.log('ws error', e)
      }

      this.ws.onopen = () => {
        console.log('ws connected')
        this.wsIsConnected = true
        clearInterval(this.reconnectionInterval)
        /*
        setInterval(() => {
          this.send_ws_data({
            cmd: 'getScreen'
          })

          this.send_ws_data({
            cmd: 'getTree'
          })
        }, 2000)
        */
      }

      this.ws.onmessage = e => {
        // console.log('qq')
        // console.log('ws message', e.data)
        var msg = JSON.parse(e.data)
        // console.log(msg)

        if (msg.cmd === 'getPing') {
        } else if (msg.cmd === 'getScreenResult') {
          if (this.remoteViews.updateNum % 2) {
            this.remoteViews.imageBytesEven = msg.data
          } else {
            this.remoteViews.imageBytesOdd = msg.data
          }
          this.remoteViews.updateNum = this.remoteViews.updateNum + 1
        } else if (msg.cmd === 'getTreeResult') {
          msg.data[0].props = {
            id: 'qq'
          }
          this.treeView = msg.data
          // console.log('tree', this.treeView)
        }
      }

      this.ws.onclose = () => {
        console.log('ws disconnected')
        this.wsIsConnected = false

        // try to reconnect
        this.reconnectionInterval = setTimeout(() => {
          console.log('trying to reconnect')
          this.websockets_init()
        }, 200)
      }
    },
    send_ws_data (data) {
      if (this.wsIsConnected) this.ws.send(JSON.stringify(data))
    },
    addView (type) {
      console.log('adding ' + type)

      this.send_ws_data({
        cmd: 'createView',
        type: type
      })
      // this.views.push({ type: type, props: { id: '', x: 0, y: 0.2, width: 0.5, height: 0.1 } })
    },
    editView (val, i) {
      console.log('editView ', val, i) // JSON.stringify(this.viewSelected.props))

      let castedVal
      switch (typeof this.viewSelected.props[i]) {
        case 'string':
          castedVal = val
          break
        case 'number':
          castedVal = parseFloat(val)
          break
      }
      console.log(val, castedVal)
      this.viewSelected.props[i] = castedVal

      this.send_ws_data({
        cmd: 'editView',
        props: this.viewSelected.props
      })
      // this.views.push({ type: type, props: { id: '', x: 0, y: 0.2, width: 0.5, height: 0.1 } })
    },
    clearViews () {
      this.views = []
    },
    selectView (v) {
      console.log('selectView')
      this.viewSelected = v
      this.viewSelected.style.borderColor = '#ff0000'
      this.initialPos = this.cursor
    },
    viewStyle (v) {
      // console.log('qq', v)

      if (!v.hasOwnProperty('props')) return {}

      return {
        top: v.props.y * this.device.height + 'px',
        left: v.props.x * this.device.width + 'px',
        width: v.props.w * this.device.width + 'px',
        height: v.props.h * this.device.height + 'px'
      }
    },
    increase (v, prop, amt) {
      console.log('increase')
      v.props[prop] += amt
    },
    q1 () {
      console.log('q1')
    },
    q2 () {
      console.log('q2')
    },
    q3 () {
      console.log('q3')
    },
    move (e) {
      console.log('moving')
      this.isMoving = true

      if (this.viewSelected !== null && this.isMoving) {
        // this.viewSelected.style.left = this.cursor.raw.x + 'px'
      }
    },
    stopDrag (e) {
      console.log('drag stop', e)
      this.isMoving = false
    }
  },
  route: {
    data () {

    }
  },
  created () {
  },
  mounted () {
    var canvas = document.getElementById('canvas')
    var device = document.getElementById('device')

    canvas.onmousemove = e => {
      this.cursor.raw = {
        x: e.pageX - e.currentTarget.offsetLeft - device.offsetLeft,
        y: e.pageY - e.currentTarget.offsetTop - device.offsetTop
      }

      this.cursor.normalized = {
        x: this.cursor.raw.x / this.device.width,
        y: this.cursor.raw.y / this.device.height
      }
      // console.log(this.position.x, this.position.y)
      // console.log(e.pageY, e.currentTarget.offsetTop, device.offsetTop)
    }
    window.addEventListener('mouseup', this.stopDrag)

    this.websockets_init()
  },
  destroyed () {
  }
}
</script>
<style lang = "less">
@import (reference) '../assets/css/variables.less';
@import (reference) '../assets/css/hacks.less';

@mainColor: #6176AD;
@mainColor: #1ca0e1;

#uieditor {
  display: flex;
  width: 100%;
  height: 100%;

  button {
    text-transform: initial;

  }
}

#canvas {
  background: @mainColor;
  height: 100%;
  display: flex;
  align-items: center;
  flex: 2;

  .remote-views, .placed-views {
    position: absolute;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
    // background: rgba(255, 255, 0, 0.2)
  }
}

#view-area {
  width: 400px;
  height: 100%;
  background: @mainColor;
  padding: 24px;
  box-shadow: 0 0 2px 2px #00000024;
  display: flex;
  flex-direction: column;
  box-sizing: border-box;

  .block {
     .scrollbar;
    flex: 1;
    margin: 12px 0;
    box-shadow: 2px 2px 5px -3px #00000063;
    border: 1px solid #ffffff29;
    background: #ffffff24;
    border-radius: 5px;
    overflow: hidden;
  }

  #view-list {
    padding: 10px;
    overflow-y: auto;

    button {
      background: transparent;
      margin: 5px;
      border: 1px solid #ffffff30;
      padding: 8px 12px;
    }
  }

  #properties {
    flex: 3;
    border-radius: 2px;
    overflow-y: auto;
    overflow-x: hidden;

    .view-property {
      display: flex;
      align-items: center;
      border-bottom: 1px solid #00000021;

      * {
        padding: 6px;
        font-size: 0.8rem;
        box-sizing: border-box;
      }

      label {
        min-width: 195px;
      }

      input {
        background: #0000000a;
        border: 0;
        padding: 10px;
        width: 100%;
        width: 148px;
        font-weight: 800;
        font-family: "Roboto Mono";
        color: white;

        &:focus {
          background: #484848 !important;
          color: white;
        }
      }
    }
  }

  #tree-view {
    // background: #00000040;
    border-radius: 5px;

    button {
      width: 100%;
      text-align: left;

      &.selected {
        background: yellow;
      }
    }

    .level-2 {
      padding-left: 30px;
    }
  }
}

#device {
  cursor: crosshair;
  width: 300px;
  height: 500px;
  background: black;
  border-radius: 2px;
  margin: 0 auto;
  box-shadow: 0 0 8px 1px #0000006b;
  position: relative;

  img {
    width: 100%;
    height: 100%;
    opacity: 0.5;
    border: 2px solid transparent;
    transition: all 300s ease-in-out;
    transition-delay: 100ms;
    position: absolute;
    top: 0;
    left: 0;
  }

  .selected {
    // border-color: yellow;
    opacity: 1;
  }
}

#cursor {
  position: absolute;
  bottom: 10px;
  right: 300px;
}

.view {
  border: 3px solid transparent;
  position: absolute;
  // display: flex;
  // align-items: center;
  justify-content: center;
  font-size: 0.5em;
  box-sizing: content-box;

  span {
    position: absolute;
    background: greenyellow;
    padding: 5px 2px;
    color: black;
    opacity: 0;
  }

  &:hover {
    border-color: greenyellow;

    span {
      opacity: 1;
    }
  }

  &.selected {
    border-color: magenta;
  }
}
</style>
