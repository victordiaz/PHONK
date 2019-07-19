<template>

  <div id = "toolbar" class = "main_shadow">
    <div class = "">
      <logo></logo>
      <div class = "project_actions">

        <transition name = "upanim" mode = "out-in">
          <button class = "transparent" key = "show" v-if = "!sharedState.show_load_project" v-on:click = "toggle_load_project">
            <p v-if = "not_loaded"><span class = "folder">/{{sharedState.current_project.project.folder}}/</span><span class = "name">{{sharedState.current_project.project.name}}</span></p>
            <p v-else class = "bold">load project</p>
            <i class = "material-icons closer">arrow_drop_down</i>
          </button>

          <button class = "transparent" key = "hide" v-else v-on:click = "toggle_load_project">
            <p class = "bold">back to editor</p>
            <i class = "material-icons closer">arrow_drop_up</i>
          </button>
        </transition>

      </div>
    </div>

    <!--
    <div class = "central_side">
      <transition name = "banneranim" mode = "out-in">
        <div class = "dashboard" v-if = "sharedState.show_dashboard" key = "dashboard">
          <h1>Dashboard</h1>
        </div>
      </transition>
    </div>
    -->

    <div class = "right_side">
      <transition name = "upanim" mode = "out-in">
        <div v-if = "isShowingInfo" class = "app_info_msg">
          <span class = "icon_left material-icons">{{infoIcon}}</span>
          <span>{{infoMsg}}</span>
        </div>
      </transition>
      <button class = "material-icons transparent" v-show = "false" v-on:click = "toggle_dashboard">dashboard</button>
      <button class = "material-icons transparent" v-bind:class = "{ 'rotate' : is_rotated, 'enabled': sharedState.show_device_info, 'device_disabled': !sharedState.device_properties.connected}"  v-on:click = "sharedState.show_device_info = !sharedState.show_device_info">phone_android</button>
      <button class = "material-icons transparent" v-on:click = "sharedState.show_preferences = !sharedState.show_preferences" v-bind:class = "{ 'enabled': sharedState.show_preferences }">settings</button>
    </div>

    <transition name = "upanim">
      <device v-show = "sharedState.show_device_info"></device>
    </transition>

    <transition name = "upanim">
      <preferences v-show = "sharedState.show_preferences"></preferences>
    </transition>

  </div>
</template>

<script>
import Store from '../Store'
import Logo from './Logo'
import Device from './Device'
import Preferences from './Preferences'
import { setTimeout } from 'timers'

export default {
  name: 'Toolbar',
  components: {
    Logo,
    Device,
    Preferences
  },
  data () {
    return {
      navitem: '',
      qq: true,
      project: '',
      isConnected: false,
      isError: false,
      runShortcut: false,
      saveShortcut: false,
      saveAsShortcut: false,
      sharedState: Store.state,
      isShowingInfo: false,
      infoMsg: 'message...',
      infoIcon: null,
      isProjectRunning: false
    }
  },
  methods: {
    toggle_load_project: function () {
      // console.log('qq')
      this.sharedState.show_load_project = !this.sharedState.show_load_project
    },
    toggle_dashboard: function () {
      // Store.emit('toggle_dashboard')
      this.sharedState.show_dashboard = !this.sharedState.show_dashboard
      // console.log('toggling dashboard')
    },
    project_saved: function () {
      this.showInfo('save', 'Saved')
    },
    project_action: function (state) {
      if (state === '/run') {
        this.showInfo('play_arrow', 'Running...')
      } else {
        this.showInfo('stop', 'Stopping...')
      }
    },
    showInfo (icon, msg) {
      this.infoMsg = msg
      this.isShowingInfo = true
      this.infoIcon = icon
      
      setTimeout(() => {
        this.isShowingInfo = false
      }, 2000)
    }
  },
  computed: {
    is_rotated: function () {
      return this.sharedState.device_properties.info.screen.orientation === 'landscape'
    },
    not_loaded: function () {
      if (!this.sharedState.current_project.project.folder) return false
      else return true
    }
  },
  created () {
    Store.on('project_saved', this.project_saved)
    Store.on('project_action', this.project_action)
  },
  destroyed () {
    Store.remove_listener('project_saved', this.project_saved)
    Store.remove_listener('project_action', this.project_action)
  }
}
</script>

<style lang='less'>
@import (reference) "../assets/css/variables.less";

#toolbar {
  display: flex;
  flex-direction: row;
  flex-wrap: nowrap;
  align-items: center;
  justify-content: space-between;
  color: @primaryTextColor;
  user-select: non;
  background: @backgroundColor;
  z-index: 2;
  height: 52px;
  font-size: 1.2em;

  /*
  background: url('/static/phonk_icon_big_no_text.png') no-repeat @backgroundColor;
  background-position-y: 0px;
  background-size: 120px;
  background-position-x: 0px;
  */

  > * {
    flex: 1;
    display: inline-flex;
    align-self: center;
    align-items: center;
    padding: 10px;
  }

  .name {
    color: white;
    margin-left: 3px;
    margin-right: 3px;
  }

  // statuses
  // showing query (show waiting icon) => showing result (show tick result or convert to red if fail)
  // save / load / trying to reconnect

  .app_info_msg {
    font-family: 'Roboto Mono';
    font-size: 0.7em;
    font-weight: 400;
    margin-right: 20px;
    color: white;
    padding: 5px 15px;
    background: rgba(0, 0, 0, 0.3);
    border-radius: 20px;
    display: flex;
    align-items: center;

    .icon_left, .icon_right {
      padding-right: 10px;
    }
    .icon_left {
      border-right: 1px solid #fff0;
    }
    .icon_right {
      color: green;
    }

  }

  .transparent {
    background: fade(@backgroundColor, 0%);

    &:hover {
      background: lighten(@backgroundColor, 10%);
    }

    &:active {
      background: darken(@backgroundColor, 0%);
    }
  }

  .left_side {
    flex: 2;
  }

  .central_side {
    flex: 1;
    justify-content: center;
  }

  .right_side {
    justify-content: flex-end;

    button {
      padding: 8px 8px;
      transition: all 0.3s ease-in-out;

      &.rotate {
        transform: rotate3d(0, 0, 1, 90deg);
      }

      &.rotate * {
        transform: translate3d(0px, 3px, 0px);
      }

      &.enabled {
        color: @accentColor;
      }

      &.device_disabled {
        opacity: 0.3;
      }
    }
  }

  .dashboard {
    display: inline-flex;
    margin: 5px;

    h1 {
      text-align: center;
      padding: 6px 15px;
      font-weight: 600;
      width: 100%;
      text-transform: uppercase;
    }
  }

  .project_actions {
    display: inline-flex;
    margin: 5px;

    button {
      display: inline-flex;
      align-items: center;
      font-family: @editorFont;
      color: #ffffffa8;
    }

    p {
      text-transform: none;
      flex: 2;
      padding: 0px 5px;
      font-size: 1em;
      font-weight: 500;
      text-overflow: ellipsis;
      overflow: hidden;
      white-space: nowrap;
    }

    .closer {
      margin-left: -5px;
    }

  }

}

</style>
