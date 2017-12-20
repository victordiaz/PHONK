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
@import "../../assets/css/variables.less";

.dtext {
  height: 100%;
  background: #252525;
  padding: 10px;
  box-sizing: border-box;
  color: white;
  font-family: Source Code Pro;
}

</style>
