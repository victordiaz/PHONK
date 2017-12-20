<template>

  <div id = "toolbar" class = "main_shadow">
    <div class = "">
      <logo></logo>
      <div class = "project_actions">

        <transition name = "upanim" mode = "out-in">
          <button class = "transparent" key = "show" v-if = "!sharedState.show_load_project" id = "load_project" v-on:click = "toggle_load_project">
            <p v-if = "not_loaded">/{{sharedState.current_project.project.folder}}/<span class = "name">{{sharedState.current_project.project.name}}</span></p>
            <p v-else class = "bold">LOAD PROJECT</p>
            <i class = "fa fa-sort-down"></i>
          </button>

          <button class = "transparent" key = "hide" v-else v-on:click = "toggle_load_project">
            <p class = "bold">back to editor</p>
            <i class = "fa fa-sort-up"></i>
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
      <button class = "fa fa-dashboard transparent" v-show = "false" v-on:click = "toggle_dashboard"></button>
      <button class = "fa fa-tablet transparent" v-bind:class = "{ 'rotate' : is_rotated }"  v-on:click = "sharedState.show_device_info = !sharedState.show_device_info"></button>
      <button class = "fa fa-cog transparent" v-on:click = "sharedState.show_preferences = !sharedState.show_preferences"></button>
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
      sharedState: Store.state
    }
  },
  methods: {
    toggle_load_project: function () {
      console.log('qq')
      this.sharedState.show_load_project = !this.sharedState.show_load_project
    },
    toggle_dashboard: function () {
      // Store.emit('toggle_dashboard')
      this.sharedState.show_dashboard = !this.sharedState.show_dashboard
      console.log('toggling dashboard')
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
  },
  destroyed () {
  }
}
</script>

<style lang='less'>
@import "../assets/css/variables.less";

#toolbar {
  display: flex;
  flex-direction: row;
  flex-wrap: nowrap;
  align-items: center;
  justify-content: space-between;
  color: @primaryTextColor;
  user-select: none;
  background: @backgroundColor;
  z-index: 2;
  height: 52px;
  font-size: 1.2em;

  background: url('/static/phonk_icon_big_no_text.png') no-repeat @backgroundColor;
  background-position-y: 0px;
  background-size: 120px;
  background-position-x: 0px;

  > * {
    flex: 1;
    display: inline-flex;
    align-self: center;
    align-items: center;
    padding: 10px;
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

  }

}

</style>
