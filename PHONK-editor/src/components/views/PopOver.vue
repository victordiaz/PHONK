<template>
  <div class="popover" v-bind:class="arrow" v-bind:style="arrowposition">
    <button id="close" v-on:click="hide_popover">x</button>
    <slot>No content to display</slot>
  </div>
</template>

<script>
import store from '../../Store'

export default {
  name: 'PopOver',
  props: {
    arrow: String,
    posx: String,
    posy: String
  },
  data () {
    return {}
  },
  computed: {
    arrowposition: function () {
      // this is a bit hardcoded :/
      if (this.arrow === 'left') { return 'bottom: 20px; left: ' + this.posx + ';' } else return 'top: ' + this.posy + '; right: ' + this.posx + ';'
    }
  },
  methods: {
    hide_popover: function () {
      store.emit('close_popup')
    }
  }
}
</script>

<style lang="less">
@import (reference) '../../assets/css/variables.less';
@popOverColor:#2e2f29;

.popover {
  background-color: @popOverColor;
  color: @primaryTextColor;
  font-size: 1em;
  position: absolute;
  width: 300px;
  min-height: 100px;
  border-radius: 3px;
  z-index: 11;
  border: 1px solid @secondaryColor;
  padding: 5px;
  filter: drop-shadow(0 0 1px rgba(0, 0, 0, 0.4))
    drop-shadow(0 3px 4px rgba(0, 0, 0, 0.4));
  -webkit-filter: drop-shadow(0 0 1px rgba(0, 0, 0, 0.4))
    drop-shadow(0 3px 4px rgba(0, 0, 0, 0.4));
  text-align: left;

  #close {
    display: none;
    color: @accentColor;
    background: @backgroundColorSecondary;
    position: absolute;
    top: 10px;
    right: 10px;
    border-radius: 100px;
    width: 30px;
    height: 30px;
    padding: 0;

    &:hover {
      background: @accentColor;
      color: @primaryTextColor;
    }
  }

  img {
    width: 100%;
  }

  ul {
    padding: 5px;

    .key {
      // font-weight: 800;
    }

    .value {
      font-weight: 500;
      color: #a49573;
    }
  }

  /* arrow */
  &:before {
    content: ' ';
    width: 15px;
    height: 15px;
    right: 17px;
    top: -9px;
    position: absolute;
    bottom: auto;
    border-right-color: #000;
    background-color: @popOverColor;
    transform: rotate(45deg);
    -webkit-transform: rotate(45deg);
    border-radius: 2px;
    z-index: -1;
  }

  &.top {
    &:before {
      border-top: 1px solid @secondaryColor;
      border-left: 1px solid @secondaryColor;
    }
  }

  &.left {
    &:before {
      left: -10px;
      bottom: 40px;

      /*
      box-shadow: -5px 3px 5px 0px rgba(0, 0, 0, 0.22);
      */
    }
  }

  &.right {
    min-height: 200px;

    &:before {
      right: -9px;
      top: 15px;
      border-top: 1px solid @secondaryColor;
      border-right: 1px solid @secondaryColor;

      /*
      box-shadow: 5px -3px 5px 0px rgba(0, 0, 0, 0.22);
      */
    }
  }

  h3 {
    margin-bottom: 0.2em;
  }

  .block {
    padding: 0.4em 0;
    margin-bottom: 0.4em;
  }
}
</style>
