<template>
  <div class = "audio">
    <audio id = "audioplayer" :src = "src" preload = "auto" controls>
      Please upgrade to a modern browser like Firefox or Chrome
    </audio>
    <!-- <button v-on:click = "play()">Play</button> -->
  </div>
</template>

<script>
export default {
  name: 'AudioPlayer',
  props: {
    src: String
  },
  data () {
    return {
    }
  },
  computed: {
    arrowposition: function () {
    }
  },
  methods: {
    play: function () {
      console.log('play')
      this.audioPlayer.play()
    }
  },
  mounted () {
    this.$nextTick(function () {
      console.log(this.$el)
      this.audioPlayer = this.$el.querySelector('#audioplayer')
      this.play()
    })
  },
  destroyed () {
  }
}
</script>

<style lang = "less">
@import (reference) "../../assets/css/variables.less";


.popover.right {
  min-height: auto;

  .audio {
    margin-top: 35px;
    audio {
      width: 100%;
    }
  }
}

</style>
