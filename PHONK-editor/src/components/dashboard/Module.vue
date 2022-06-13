<template>
  <div class = "module main_shadow" :style = "pos">
    <p class = "info" v-show = "true">{{info}}</p>
    <dbutton v-if = "info.type === 'button'" :info = "info"></dbutton>
    <dtext v-if = "info.type === 'text'" :info = "info"></dtext>
    <dinput v-if = "info.type === 'input'" :info = "info"></dinput>
    <dslider v-if = "info.type === 'slider'" :info = "info"></dslider>
    <dwebview v-if = "info.type === 'webview'" :info = "info"></dwebview>
  </div>
</template>

<script>
import Dbutton from './Dbutton'
import Dtext from './Dtext'
import Dinput from './Dinput'
import Dslider from './Dslider'
import Dwebview from './Dwebview'

export default {
  name: 'module',
  props: ['info'],
  components: {
    Dbutton,
    Dtext,
    Dinput,
    Dslider,
    Dwebview
  },
  data () {
    return {
      pos: null
    }
  },
  computed: {
  },
  methods: {
  },
  mounted: function () {
    // var p = this.$el.parentElement
    this.pos = {
      top: 500 * Math.random() + 'px',
      left: 500 * Math.random() + 'px'
    }
    console.log('added')
    console.log(this.pos)
  },
  created: function () {
    console.log('created module')
  }
}
</script>

<style lang = "less">
@import (reference) "../../assets/css/variables.less";

.module {
  position: absolute;
  box-sizing: border-box;
  width: 150px;
  height: 150px;
  border: 0px solid black;
  color: #2E2E2E;
  font-family: Roboto-Condensed-Regular, verdana, sans-serif;
  overflow: hidden;
  margin: 5px 0px;
  border-radius: 2px;
  background: whitesmoke;
  font-family: monospace;
  font-size: 8px;

  .info {
    padding: 5px;
    position: absolute;
    z-index: 10;
    pointer-events: none;
  }

  &:hover {
    background: rgb(240, 240, 240);
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
</style>
