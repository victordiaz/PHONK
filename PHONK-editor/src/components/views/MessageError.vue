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
  }
}
</script>

<style lang = "less">
@import (reference) "../../assets/css/variables.less";

.msg_error {
  font-size: 1rem;
  z-index: 5;
  text-align: center;
  vertical-align: middle;
  display: flex;
  justify-content: center;
  align-items: center;
  position: absolute;
  background: var(--color-main);
  width: 100%;
  height: 100%;

  p {
    padding: 8px 18px;
    margin: 20px;
    color: var(--color-text-light);
    line-height: 1.5em;;
    border: 1px solid var(--color-lines);
    background: var(--color-main-lighter);
  }

  button {
    padding-left: 12px;
    margin-left: 12px;
    text-transform: uppercase;
    background: none;
    color: var(--color-accent);
    font-size: 0.9rem;
  }
}


</style>
