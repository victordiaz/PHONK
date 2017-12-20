<template>
  <div class = "webview">
    <div class = "nav" v-show = "showcontrols">
      <input /> <button></button>
    </div>
    <iframe v-bind:src = "url"></iframe>
  </div>
</template>

<script>
export default {
  name: 'WebView',
  props: {
    url: String,
    showcontrols: Boolean
  },
  data () {
    return {
      shownav: true
    }
  },
  computed: {
    cmd: function () {
    }
  },
  credted () {
    console.log(this.showcontrols)
  },
  methods: {
  }
}
</script>

<style lang = "less">
@import "../../assets/css/variables.less";

.webview {
  border: 1px solid black;

  .nav {
    display: flex;

    input {
      width: 100%;
    }
  }

  iframe {
    width: 100%;
    height: 100%;
  }
}

</style>
