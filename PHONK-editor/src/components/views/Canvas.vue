<template>
  <div class = "canvas">
    <slot>
    No content to display
    </slot>
  </div>
</template>

<script>
export default {
  name: 'Canvas',
  props: {
    code: String
  },
  data () {
    return {
    }
  },
  computed: {
    cmp: function () {

    }
  },
  methods: {
  }
}
</script>

<style lang = "less">
@import (reference) "../assets/css/variables.less";

.canvas {

}

</style>
