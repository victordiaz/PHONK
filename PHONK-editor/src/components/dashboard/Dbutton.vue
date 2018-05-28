<template>
  <button class = "dbutton" v-on:click = "click">{{info.name}}</button>
</template>

<script>
import Store from '../../Store'

export default {
  name: 'DButton',
  props: ['info'],
  data () {
    return {
      pos: null
    }
  },
  computed: {
  },
  methods: {
    click: function () {
      console.log('click')
      console.log(this.info)
      Store.send_ws_data({'module': 'dashboard', 'type': 'button', 'id': this.info.id, 'action': 'pressed'})
    }
  },
  mounted: function () {
    console.log('added dbutton')
  },
  created: function () {
    console.log('created module dbutton')
  }
}
</script>

<style lang = "less">
@import (reference) "../../assets/css/variables.less";

.dbutton {
  position: relative;
  width: 100%;
  height: 100%;
  background-color: @accentColor;
  outline: none;
  border: none;
  color: black;

  &:hover {
    background: lighten(@accentColor, 20%);
  }

  &:active {
    background: darken(@accentColor, 10%);
  }
}
</style>
