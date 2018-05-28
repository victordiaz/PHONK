<template>
  <div class = "dwebview">
    <iframe src = "http://www.protocoder.org"></iframe>
  </div>
</template>

<script>
export default {
  name: 'DWebview',
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

.dwebview {
  margin: 0;
  padding: 0px;
  width: 100%;
  height: 100%;

  iframe {
    width: 100%;
    height: 100%;
  }

}

</style>
