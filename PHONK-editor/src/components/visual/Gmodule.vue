<template>
  <div class = "gmodule vertical draggable" @mousedown="startDrag" @mouseup="stopDrag" @mousemove="doDrag" @mouseout="stopDrag" :style="dragstyle">
    <h1>{{module.name}}</h1>
    <div class = "connectors">
      <div class = "slots">
        <div id = "1" class = "slot" v-for = "s in module.slots">
          <div class = "circle"> </div>
          <p contenteditable>{{s.name}}</p>
        </div>
      </div>
      <div class = "signals">
        <div class = "signal" v-for = "s in module.signals">
          <p>{{s.name}}</p>
          <div class = "circle"> </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Gmodule',
  props: ['module'],
  data () {
    return {
      isEditable: true,
      t: 'slot 1',
      pos: null,
      dragging: false
    }
  },
  computed: {
    dragstyle: function () {
      console.log('style')

      if (!this.pos) {
        return {}
      }
      return {
        position: 'absolute',
        top: this.pos.top + 'px',
        left: this.pos.left + 'px'
      }
    }
  },
  methods: {
    startDrag: function (event) {
      console.log('startDrag')
      event.stopPropagation()
      this.dragging = true
    },
    stopDrag: function (event) {
      console.log('stopDrag')

      event.stopPropagation()
      this.dragging = false
    },
    doDrag: function (event) {
      console.log('doDrag')

      if (!this.dragging) {
        return
      }
      event.stopPropagation()
      var p = this.$el.parentElement
      var x = this.pos.left + event.movementX
      var y = this.pos.top + event.movementY
      if (x > 0 && x < p.clientWidth && y > 0 && y < p.clientHeight) {
        this.pos.left = x
        this.pos.top = y
      }
    }
  },
  mounted: function () {
    var p = this.$el.parentElement
    if (p.style.position === '') {
      p.style.setProperty('position', 'relative')
    }
    this.pos = {
      left: this.$el.offsetLeft,
      top: this.$el.offsetTop
    }
  },
  created: function () {
    console.log('qq')
  }
}
</script>

<style lang = "less">
@import (reference) "../../assets/css/variables.less";

.gmodule {
  width: 122px;
  height: 120px;
  background: whitesmoke;
  border: 0px solid black;
  border-radius: 2px;
  position: absolute;
  top: 100px;
  left: 52px;
  color: black;
  font-family: monospace;
  font-size: 8px;
  user-select: none;
  box-shadow: 0px 0px 2px 1px rgba(0, 0, 0, 0.33);

  &:hover {
    background: rgb(240, 240, 240);
    cursor: move;
  }

  h1 {
    position: absolute;
    left: 0;
    right: 0;
    height: 100%;
    text-align: center;
    display: flex;
    justify-content: center;
    align-items: center;
  }
}

.connectors > div {
  vertical-align: middle;
  position: absolute;
  display: flex;
  color: rgba(0, 0, 0, 0.52);

  * {
    vertical-align: middle;
  }

  .slot, .signal {
    width: 100%;
    left: 0;

    .circle {
      background: black;

      &:hover {
        background: rgba(255, 0, 255, 0.53);
        border-radius: 0px 2px 2px 0px;
        cursor: pointer;
      }
    }

  }
}

.horizontal {
  * {
    display: inline-block;
  }

  .connectors > div {
    flex-flow: column;
  }

  .signals {
    text-align: right;
    right: 0;
  }

  .circle {
    width: 3px;
    height: 10px;
  }

  .slot, .signal {
    padding: 2px 0px;
  }

  .slot .circle {
    border-radius: 0px 1px 1px 0px;
  }

  .signal .circle {
    border-radius: 1px 0px 0px 1px;
  }
}

.vertical {
  height: 50px;

  .connectors > div {
    flex-flow: row;

    p {
      position: absolute;
    }
  }

  .slot p {
    transform: rotate3d(0, 0, 1, -42deg);
  }

  .signal p {
    transform: rotate3d(0, 0, 1, 42deg);
  }


  .slots {
    p {
      top: -19px;
    }
  }

  .signals {
    bottom: 0;
    p {
      top: 15px;
    }
  }

  .slot, .signal {
    padding: 0px 2px;
  }

  .circle {
    width: 10px;
    height: 3px;
    background: black;
  }

  .slot .circle {
    margin: 0px 0px 0px 0px;
    border-radius: 0px 0px 1px 1px;
  }

  .signal .circle {
    margin: 0px 0px 0px 0px;
    border-radius: 1px 1px 0px 0px;
  }
}

</style>
