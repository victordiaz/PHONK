<template>
  <p class = "dtext"> {{info.name}} </p>
</template>

<script>
export default {
  name: 'DText',
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

.dtext {
  height: 100%;
  padding: 10px;
  box-sizing: border-box;
  .font-mono-400;
}

</style>
