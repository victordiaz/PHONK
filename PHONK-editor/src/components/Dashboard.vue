<template>
  <div id="dashboard" class="proto_panel panel_above">
    <div class="actionbar">
      <h1>Dashboard</h1>
      <div class="adding_buttons">
        <button v-on:click="addModule">Add</button>
        <button v-on:click="removeModule">Del</button>
        <button v-on:click="removeAllModules">Clear</button>
      </div>
    </div>
    <div class="content">
      <div id="dashboard_empty">
        <h1>No widgets</h1>
        <h2>
          Apps can be controlled remotely using the Dashboard. Explore the
          Examples!
        </h2>
      </div>

      <module v-for="m in addedModules" :info="m" key="m.id"></module>

      <!--
      <div class = "card">
        <img class = "widget" src = "" />
        <button class = "widget">button</button>
      </div>

      -->
    </div>
  </div>
</template>

<script>
import Store from '../Store'
import Module from './dashboard/Module'

export default {
  name: 'Dashboard',
  components: {
    Module
  },
  data () {
    return {
      modules: [
        { type: 'button' },
        { type: 'text' },
        { type: 'input' },
        { type: 'slider' },
        { type: 'webview' }
      ],
      addedModules: []
    }
  },
  methods: {
    addModule: function () {
      var rnd = Math.floor(this.modules.length * Math.random())
      var type = this.modules[rnd].type
      var mId = '' + Math.random()
      console.log('adding module ---> ' + type)

      // var m = new Module()
      this.addedModules.push({ name: mId, id: mId, type: type })
    },
    removeModule: function () {
      console.log('remove module')
      this.addedModules.pop()
    },
    removeAllModules: function () {
      console.log('remove all widgets')
      this.addedModules = []
    },
    handleEvent: function (e) {
      console.log('handling dashboard event')
      console.log(e)

      switch (e.action) {
        case 'add':
          this.addedModules[e.id] = e
          break

        case 'clear':
          break

        case 'show':
          break

        default:
      }
    }
  },
  route: {
    data () {
      this.title = 'Dashboard'
    }
  },
  created () {
    Store.on('dashboard', this.handleEvent)
  },
  mounted () {},
  destroyed () {
    Store.remove_listener('dashboard', this.handleEvent)
  }
}
</script>
<style lang="less">
@import (reference) '../assets/css/variables.less';

#dashboard .actionbar {
  background: rgba(0, 0, 0, 0.28);
  color: rgba(255, 255, 255, 0.34);
  display: flex;
  flex: 1;

  h1 {
    padding: 10px;
    font-size: 1.2em;
    font-weight: 600;
    text-transform: uppercase;
    flex: 1;
  }

  .adding_buttons {
    padding: 5px;
  }
}

#dashboard .content {
  position: absolute;
  left: 50%;
  top: 50%;
  -webkit-transform: translate(-50%, -50%);
  transform: translate(-50%, -50%);

  .card {
    background: white;
    padding: 5px;
    border-radius: 2px;
    display: flex;
    flex-direction: column;

    * {
      flex: 1;
    }
  }

  #dashboard_empty {
    max-width: 600px;
    font-size: 1em;
    line-height: 1.3em;
    text-align: center;
    color: rgba(255, 255, 255, 1);
    margin: auto auto;

    * {
      padding: 20px;
    }

    h1 {
      font-weight: 700;
    }
  }

  img {
  }
}
</style>
