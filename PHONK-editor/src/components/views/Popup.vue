<template>
  <div class = "popover" v-bind:class = "arrow" v-bind:style = "arrowposition">
    <button id = "close" v-on:click = "hide_popover">x</button>
    <slot>
    No content to display
    </slot>
  </div>
</template>

<script>
import store from '../../Store'

export default {
  name: 'Popup',
  props: {
    arrow: String,
    posx: String,
    posy: String
  },
  data () {
    return {
    }
  },
  computed: {
    arrowposition: function () {
      // 95 y 308
      // console.log('popup: ' + this.posx + ' ' + this.posy)
      // console.log('popup2: ' + 'top: ' + this.posy + '; left: ' + this.posx + ';')

      if (this.arrow === 'left') return 'bottom: 20px; left: ' + this.posx + ';'
      else return 'top: ' + this.posy + '; right: ' + this.posx + ';'
    }
  },
  methods: {
    hide_popover: function () {
      store.emit('close_popup')
    }
  }
}
</script>

<style lang = "less">
@import (reference) "../../assets/css/variables.less";

.popover {
	background-color: rgba(255, 255, 255, 1);
	color: rgb(87, 87, 87);
	font-family: 'Open Sans';
	font-size: 1em;
  position: absolute;
  width: 300px;
  min-height: 100px;
  border-radius: 3px;
  z-index: 11;
  border: 0px solid black;
  padding: 5px;
  filter: drop-shadow(0 0 1px rgba(0,0,0,0.4)) drop-shadow(0 3px 4px rgba(0,0,0,0.4));
  -webkit-filter: drop-shadow(0 0 1px rgba(0,0,0,0.4)) drop-shadow(0 3px 4px rgba(0,0,0,0.4));
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
      font-weight: 800;
    }

    .value {
      font-weight: 500;
    }
  }

  /* arrow */
  &:before {
    content: ' ';
    width: 15px;
    height: 15px;
    right: 17px;
    top: -5px;
    position: absolute;
    bottom: auto;
    border-right-color: #000;
    background-color: white;
    transform: rotate(45deg);
    -webkit-transform: rotate(45deg);
    border-left: 0px solid black;
    border-bottom: 0px solid black;
    border-radius: 2px;
    z-index: -1;
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
      right: 10px;
      top: 15px;
      /*
      box-shadow: 5px -3px 5px 0px rgba(0, 0, 0, 0.22);
      */
    }
  }

  h3 {
    padding-bottom: 0.4em;
  }

  .block {
    padding: 0.4em 0;
  }
}

</style>
