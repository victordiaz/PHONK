<template>
  <div class = "msg_error">
    <p>
      <slot>{{text}}</slot>
      <button v-if = "action" id = "reload" v-on:click = "actionfn">{{action}}</button>
    </p>
  </div>
</template>

<script>
export default {
  name: 'MessageError',
  props: {
    text: String,
    action: String,
    actionfn: Function
  },
  data () {
    return {
    }
  },
  computed: {
    fn: function () {
    }
  },
  methods: {
  }
}
</script>

<style lang = "less">
@import (reference) "../../assets/css/variables.less";

.msg_error {
  font-size: 1rem;
  z-index: 5;
  text-align: center;
  // width: 402px;
  // height: 100%;
  vertical-align: middle;
  display: flex;
  justify-content: center;
  align-items: center;
  position: absolute;
  background: @mainColor;
  width: 100%;
  height: 100%;

  p {
    padding: 8px 18px;
    margin: 20px;
    color: @primaryTextColor;
    line-height: 1.5em;;
    // border-radius: 58px;
    border: 1px solid @secondaryColor;
  }

  button {
    // text-decoration: underline;
    padding-left: 12px;
    margin-left: 12px;
    color: white;
    font-weight: 700;
    text-transform: uppercase;
    background: none;
    color: @accentColor;

    &:hover {
      color: lighten(@accentColor, 30%);
    }

    &:active {
      color: darken(@accentColor, 30%);
    }
  }
}


</style>
