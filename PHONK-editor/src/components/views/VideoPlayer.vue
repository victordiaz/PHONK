<template>
  <div class = "video">
    <video v-if = "vtype === 'served'" :src = "src" controls></video>
    <iframe v-if = "vtype === 'youtube'" :src = "src" frameborder = "0" allowfullscreen></iframe>
    <iframe v-if = "vtype === 'vimeo'" :src = "src" frameborder = "0" allowfullscreen></iframe>
  </div>
</template>

<script>
export default {
  name: 'VideoPlayer',
  props: {
    src: String
  },
  data () {
    return {
      type: ''
    }
  },
  computed: {
    vtype: function () {
      // console.log(this.src)
      if (this.getDomain(this.src).indexOf('youtube') !== -1) this.type = 'youtube'
      else if (this.getDomain(this.src).indexOf('vimeo') !== -1) this.type = 'vimeo'
      else this.type = 'served'
      console.log(this.type)
      return this.type
    }
  },
  methods: {
    // http://stackoverflow.com/questions/8498592/extract-root-domain-name-from-string
    getDomain: function (url) {
      var domain
      // find & remove protocol (http, ftp, etc.) and get domain
      if (url.indexOf('://') > -1) {
        domain = url.split('/')[2]
      } else {
        domain = url.split('/')[0]
      }

      // find & remove port number
      domain = domain.split(':')[0]

      return domain
    }
  }
}
</script>

<style lang = "less">
@import (reference) "../../assets/css/variables.less";

.video {
  iframe, video {
    width: 100%;
    height: 100%;
  }
}


</style>
