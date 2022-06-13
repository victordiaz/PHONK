<template>
  <div class = "p5">
    <div id = "p5container"></div>
  </div>
</template>

<script>
export default {
  name: 'P5js',
  props: {
    sketch: String
  },
  data () {
    return {
    }
  },
  computed: {
  },
  methods: {
  },
  created () {

  },
  destroyed () {

  }
}
</script>

<style lang = "less">

.p5 {
  #p5container {
    width: 350px;
    height: 200px;
  }

}


</style>
