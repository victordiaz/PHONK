<template>
  <popup arrow = "top" :posx = "posx" :posy = "posy">
    <div class = "popup_content">
      <div class = "block" v-for = "(v, name) in sharedState.preferences">
        <h3>{{name}}</h3>
        <ul>
          <li v-for = "(value, prop) in v">
            <p>{{prop}}</p>
            <div class = "widget">
              <input v-show = "valueType(value) == 'boolean'" type="checkbox" id="checkbox" v-model="sharedState.preferences[name][prop]">
              <div class = "size" v-show = "valueType(value) == 'number'"><button v-on:click = "decrease_size">-</button><input v-model = "sharedState.preferences[name][prop]" readonly="readonly" /><button v-on:click = "increase_size">+</button></div>
            </div>
          </li>
        </ul>
      </div>
      <ul>
      <li><p>Reset WebEditor</p><button v-on:click = "clearSettings">reset</button></li>
      </ul>
    </div>
  </popup>
</template>

<script>
import store from '../Store'
// import _ from 'lodash'

import Popup from './views/Popup'

export default {
  name: 'Preferences',
  components: {
    Popup
  },
  data () {
    return {
      posx: '4px',
      posy: '48px',
      sharedState: store.state,
      preferences: store.state.preferences
    }
  },
  methods: {
    valueType: function (value) {
      return typeof (value)
    },
    increase_size: function () {
      this.sharedState.preferences['editor']['text size'] += 1
      store.emit('font_changed')
    },
    decrease_size: function () {
      this.sharedState.preferences['editor']['text size'] -= 1
      store.emit('font_changed')
    },
    clearSettings: function () {
      store.clearSettings()
    }
  },
  watch: {
    preferences: {
      handler: function (newVal, oldVal) {
        console.log('watching', oldVal + ' ' + newVal)
        store.saveSettings()
      },
      deep: true
    }
    /*,
    "preferences['other']['WebIDE as default editor']": {
      handler: function (newVal, oldVal) {
        console.log('changed webid ' + newVal)
      }
    }
    */
  },
  created () {
  },
  destroyed () {
  }
}
</script>

<style lang = "less" scoped>
@import (reference) "../assets/css/variables.less";

.popover {
    ul, li {
    width: 100%;
  }
}

ul {
  display: block;
}

li {
  display: inline-flex;
  align-items: center;
  height: 32px;

  p {
    flex: 5;
  }
}

.widget {
  display: inline-flex;

  .size {
    background: #ff356b;

    input {
      color: white;
      background: transparent;
      font-weight: bolder;
      border-left: 1px solid #fff5;
      border-right: 1px solid #fff5;
    }
  }
}

button {
  padding: 8px 12px;
}

input {
  width: 31px;
  margin: 0px 0px;
  padding: 0;
  text-align: center;
  outline: none;
  color: @accentColor;
  border: 0px;
}

</style>
