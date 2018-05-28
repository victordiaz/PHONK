<template>
  <div class = "gconector"  v-bind:width="xdelta+'px'" v-bind:height="ydelta+'px'">

    <svg v-bind:width="xdelta+'px'" v-bind:height="ydelta+'px'">
      <path id="lineAB" :d="p" stroke="gray" stroke-width="3" fill="none" />
    </svg>

    <p> {{path}} </p>

  </div>
</template>

<script>
export default {
  name: 'Gconector',
  props: {
  },
  data () {
    return {
      width: 400,
      height: 400,
      x: 0,
      y: 0,
      xdelta: 300,
      ydelta: 200,
      path: ''
    }
  },
  computed: {
    p: function () {
      // this.path = 'm 100,0 c 200,0 0,200 200,200'
      this.path = 'm ' + this.x + ',' + this.y + ' c ' + this.xdelta + ',0 0,' + this.ydelta + ' ' + this.xdelta + ',' + this.ydelta
      // console.log(path)

      return this.path
    }
  },
  methods: {
  }
}
</script>

<style lang = "less">
@import (reference) "../../assets/css/variables.less";

.gconector {
  color: black;
  border: 1px solid pink;
  position: absolute;
}

.gconector p {
  font-family: monospace;
  font-size: 10px;
  background-color: rgba(0, 0, 0, 0.1);
  padding: 2px;
  border-radius: 2px;
  position: absolute;
  top: 50%;
  left: 25%;
  color: #383838;
  padding: 5px;
}

</style>
