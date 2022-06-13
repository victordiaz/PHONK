<template>
  <div class = "">
    <slot>
    No content to display
    </slot>
  </div>
</template>

<script>
export default {
  name: 'Generic',
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
@import (reference) "../assets/css/variables.less";

}


</style>
