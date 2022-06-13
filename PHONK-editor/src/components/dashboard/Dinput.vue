<template>
  <div class = "dinput">
    <span> qq </span>
    <input class = "" />
    <button> Ok </button>
  </div>
</template>

<script>
export default {
  name: 'DInput',
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
    console.log('added dinput')
  },
  created: function () {
    console.log('created module dinput')
  }
}
</script>

<style lang = "less">
@import (reference) "../../assets/css/variables.less";

.dinput {
  width: 100%;
  height: 100%;
  left: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #252525;

  span {

  }

  input {
    border: 0px;
    background: transparent;
    color: white;
    flex: 2;
    min-width: 20px;
    margin: 0px 10px;
    height: 100%;
  }

  button {
    width: 50px;
    height: 100%;
    margin: 0px;
    border-radius: 0;
    color: @secondaryTextColor;
    background: @accentColor;
  }
}

</style>
