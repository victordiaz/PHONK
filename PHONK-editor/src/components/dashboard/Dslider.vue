<template>
  <div class = "dslider">
    <span>{{info.name}}</span>
    <input type="range"/>
    <span>2</span>
  </div>

</template>

<script>
export default {
  name: 'DSlider',
  props: ['info'],
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
    console.log('added dbutton')
  },
  created: function () {
    console.log('created module dbutton')
  }
}
</script>

<style lang = "less">
@import (reference) "../../assets/css/variables.less";

.dslider {
  height: 100%;
  background: white;
  display: flex;
  align-items: center;
  justify-content: center;

  input[type=range] {
    -webkit-appearance: none;
    flex: 2;
    height: 100%;
    background: transparent;
  }

  input[type="range"]::-moz-range-thumb {
    background-color: #ffbb33;
    width: 10px;
    height: 30px;
  }

  span {
    padding: 10px;
  }

  .value {
    position: absolute;
    right: 10px;
  }

}

</style>
