<template>
  <popup arrow = "top" :posx = "posx" :posy = "posy">
    <div class = "popup_content">
      <ul>
        <li v-for = "(prop, key1) in device_properties.info">
          <div class = "title">{{key1}}</div>
          <ul>
            <li v-for = "(value, key2) in prop"><span class = "key">{{ key2 }} :</span> <span class = "value">{{ value }}</span></li>
          </ul>
        </li>
      </ul>
    </div>
  </popup>
</template>

<script>
import store from '../Store'
// import _ from 'lodash'

import Popup from './views/Popup'

export default {
  name: 'Device',
  components: {
    Popup
  },
  data () {
    return {
      device_properties: store.state.device_properties,
      ready: false,
      posx: '35px',
      posy: '48px'
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
    store.on('device', this.device_update)
  },
  destroyed () {
    store.remove_listener('device', this.device_update)
  }
}
</script>

<style lang = "less" scoped>
@import "../assets/css/variables.less";

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

</style>
