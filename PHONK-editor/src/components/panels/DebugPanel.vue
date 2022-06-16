<template>
  <div class = "debug_panel">
    <h2>Debug Panel</h2>
    <ul>
      <li
        v-for = "(m, index) in debug_msg"
        track-by = "index"
        :key="index">
        {{m}}
      </li>
    </ul>
  </div>
</template>

<script>
import Store from '../../Store'

export default {
  name: 'DebugPanel',
  props: {
    prop1: String,
    prop2: Number
  },
  data () {
    return {
      debug_msg: []
    }
  },
  methods: {
    debug_print: function (data) {
      console.log(data)
      this.debug_msg.push(data)
    }
  },
  created () {
    Store.on('project_created', this.debug_print)
  },
  destroyed () {
    Store.removeListener('project_created', this.debug_print)
  }
}
</script>

<style lang = "less">
@import (reference) '../../assets/css/variables.less';

.debug_panel {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 200px;
  height: 200px;
  background-color: rgba(0, 255, 255, 0.5);
  padding: 5px;
  font-family: @editorFont;
  box-shadow: 0px 0px 2px 2px rgba(0, 0, 0, 0.2);
  z-index: 10;

  h2 {
    font-weight: 500;
    padding: 2px;
  }

  ul {
    padding: 2px;

    li {
      padding: 3px 0px;
      border-bottom: 1px dotted rgba(255, 255, 255, 0.2);
    }
  }
}
</style>
