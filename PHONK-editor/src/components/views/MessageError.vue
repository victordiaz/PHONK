<template>
  <div class = "msg_error">
    <p>
      <slot>
      No content to display
      </slot>
    </p>
  </div>
</template>

<script>
export default {
  name: 'MessageError',
  props: {
    prop1: String,
    prop2: Number
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
@import "../../assets/css/variables.less";

.msg_error {
  font-size: 1.1em;
  z-index: 5;
  text-align: center;
  // width: 402px;
  // height: 100%;
  vertical-align: middle;
  display: flex;
  justify-content: center;
  align-items: center;

  position: absolute;
  top: 10px;
  right: 10px;

  p {
    padding: 8px 18px;
    background: rgba(0, 0, 0, 0.8);
    border-radius: 58px;
    color: @accentColor;
  }

  #reload {
    border-left: 1px solid rgba(255, 255, 255, 0.5);
    padding-left: 12px;
    margin-left: 12px;
    color: white;
    font-weight: 600;
    text-transform: uppercase;
    font-size: 0.9em;
    background: none;

    &:hover {
      text-decoration: underline;
    }
  }
}


</style>
