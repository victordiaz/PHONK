<template>
  <div class = "gcanvas">
    <gpalette :amodules = "addedModules" :modules = "modules"></gpalette>
    <div class = "modules_container">
      <gmodule v-if = "m.display" v-for = "m in addedModules" :module = "m"></gmodule>
      <gconector v-for = "c in addedConnectors"></gconector>
      <button v-on:click = "load">load</button>
    </div>
  </div>
</template>

<script>
import Gpalette from './Gpalette'
import Gconector from './Gconector'

export default {
  components: {
    Gpalette,
    gmodule (resolve) { require(['./Gmodule.vue'], resolve) },
    Gconector
  },
  name: 'Gcanvas',
  props: {
  },
  data () {
    return {
      addedModules: [],
      addedConnectors: [],
      //  blocks[0].signals[0].connector.to.slot
      modules: [
        {
          name: 'qq1',
          x: 200,
          y: 200,
          type: 'normal',
          display: true,
          signals: [
            { name: 'audio', type: 'normal', connector: null },
            { name: 'audio', type: 'normal', connector: null },
            { name: 'audio', type: 'normal', connector: null }
          ],
          slots: [
            { name: 'audio', type: 'normal', connector: null },
            { name: 'audio', type: 'normal', connector: null }
          ]
        },
        { name: 'qq2', x: 200, y: 200 }
      ],
      connectors: [
        { name: '', x1: '', y1: '', x2: '', y2: '', from: '', to: '' }
      ]
    }
  },
  computed: {
  },
  methods: {
    load: function () {
      this.displayModule = true
      console.log('hola')
    }
  },
  created () {
    // this.addedModules.push(this.modules[0])
  }
}
</script>

<style lang = "less">
@import (reference) "../../assets/css/variables.less";

.gcanvas {
  position: relative;
  background: #ffb53b;
  width: 100%;
  height: 100%;
}

</style>
