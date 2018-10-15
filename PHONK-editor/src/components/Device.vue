<template>
  <popup arrow = "top" :posx = "posx" :posy = "posy">
    <div class = "popup_content">
      <ul v-if = "sharedState.device_properties.connected" >
        <li v-for = "(prop, key1) in sharedState.device_properties.info">
          <div class = "title">{{key1}}</div>
          <ul>
            <li v-for = "(value, key2) in prop"><span class = "key">{{ key2 }} :</span> <span class = "value">{{ value }}</span></li>
          </ul>
        </li>
      </ul>
      <div v-else class = "disconnected">
        <div class = "icon fa fa-stack">
          <i class = "wifi fa fa-wifi fa-stack-2x"></i>
          <i class = "ban fa fa-times fa-stack-2x"></i>
        </div>
        <p>Your device is disconnected, please double check the connection among both devices.</p>
      </div>
    </div>
  </popup>
</template>

<script>
import Store from '../Store'
// import _ from 'lodash'

import Popup from './views/Popup'

export default {
  name: 'Device',
  components: {
    Popup
  },
  data () {
    return {
      // device_properties: store.state.device_properties,
      ready: false,
      posx: '35px',
      posy: '48px',
      device_properties: {},
      sharedState: Store.state
    }
  },
  methods: {
    togglepopup: function () {
      var rect = this.$el.getBoundingClientRect()
      // console.log('qq', rect.top, rect.left, rect.height)
      this.posx = rect.left + 'px'
      this.posy = rect.top + rect.height + 5 + 'px'
    },
    device_update: function (data) {
      // console.log('device_update', data)
      if (typeof data.info !== 'undefined') {
        // this.device_properties = _.orderBy(data, 'name')
        this.device_properties = data
        this.ready = true
      } else {
        this.device_properties.connected = false
      }
    }
  },
  created () {
    Store.on('device', this.device_update)
  },
  destroyed () {
    Store.remove_listener('device', this.device_update)
  }
}
</script>

<style lang = "less" scoped>
@import (reference) "../assets/css/variables.less";

#device_frame {
  .all-transitions;
  background: white;
  border: 0;
  padding: 2px 1px 3px 1px;
  margin: 0px;
  border-radius: 1px;
  display: inline-block;

  #device_screen {
    .all-transitions;
    width: 12px;
    height: 20px;
    background: darken(@accentColor, 10%);
    border-radius: 0px;
  }

}

.disconnected {
  display: flex;
  flex-direction: column;
  align-content: center;
  align-items: center;
  margin: 12px;

  .icon { font-size: 1em; text-align: center; }
  .wifi { opacity: 0.3; }
  .ban { color: rgba(255, 0, 0, 0.8); font-size: 1.5em; padding: 10px 0px 0 10px}
  p { margin-top: 22px; font-size: 1.2em; color: lighten(@mainColor, 30%); font-weight: 600; }
}

</style>
