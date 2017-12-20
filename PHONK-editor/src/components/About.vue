<template>
  <div id = "about" class = "proto_panel">
    <div class = "actionbar">
      <h1>About</h1>
    </div>
    <div class = "content">
    </div>
  </div>
</template>

<script>
export default {
  name: 'About',
  data () {
    return {

    }
  },
  methods: {

  },
  route: {
    data () {
      this.title = 'About'
    }
  },
  created () {
  },
  mounted () {
  },
  destroyed () {
  }
}
</script>
<style lang = "less">
#panels #about {
  background: white;
  color: black;
  height: 100px;
}
</style>
