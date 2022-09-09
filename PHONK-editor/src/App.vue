<template>
  <div id="app">
    <div id="top_container" class="container">
      <main-toolbar></main-toolbar>
    </div>

    <div id="main_container">
      <div v-show="backdrop" id="backdrop" v-on:click="toggle_top_container"></div>

      <div id="central_container" class="container">
        <router-view
          class
          @route-data-loaded="changeTitle"
          keep-alive
          transition="banner-anim"
          transition-mode="out-in"
        ></router-view>
      </div>

      <panel-handle orientation="vertical" container="right_container"></panel-handle>
      
      <div
        id="right_container"
        class="container"
        v-show="sharedState.preferences['editor']['sidebar']"
      >
        <div id="panels">
          <ul v-if = "showPanelSelector" id = "panels-menu" class = "panels-menu" v-show = "false">
            <li v-on:click = "changePanel('general')" :class = "{'selected': panel === 'general' }">General</li>
            <li v-on:click = "changePanel('views')"  :class = "{'selected': panel === 'views' }">Views</li>
            <li v-on:click = "changePanel('properties')" :class = "{'selected': panel === 'properties' }">Properties</li>
          </ul>
          <div v-if = "panel === 'general'" id = "editor_panels" class = "panels-container">
            <documentation-panel></documentation-panel>
            <panel-handle orientation="horizontal" container="documentation"></panel-handle>
            <file-manager-panel></file-manager-panel>
            <panel-handle orientation="horizontal" color="dark" container="file_manager"></panel-handle>
            <console-panel></console-panel>
          </div>
          <div v-else id = "uieditor_panels" class = "panels-container"></div>
        </div>
      </div>

      <debug-panel v-if="false"></debug-panel>

      <transition-group name="banneranim">
        <project-load v-if="sharedState.show_load_project" key="l_project"></project-load>
        <!-- <dashboard v-show="sharedState.show_dashboard" key="dashboard"></dashboard> -->
      </transition-group>
    </div>

  </div>
</template>

<script>
import Store from './Store'
import MainToolbar from './components/panels/MainToolbar.vue'
import PanelHandle from './components/panels/PanelHandle'
import DebugPanel from './components/panels/DebugPanel'
import FileManagerPanel from './components/panels/FileManagerPanel'
import ConsolePanel from './components/panels/ConsolePanel'
import DocumentationPanel from './components/panels/documentation/DocumentationPanel'
import ProjectLoad from './components/project/ProjectLoad'

export default {
  components: {
    PanelHandle,
    MainToolbar,
    DebugPanel,
    ProjectLoad,
    FileManagerPanel,
    ConsolePanel,
    DocumentationPanel
  },
  data () {
    return {
      backdrop: false,
      isMobile: false,
      dndState: '',
      myVar: 'hello',
      sharedState: Store.state,
      panel: 'general',
      showPanelSelector: true
    }
  },
  methods: {
    changeTitle (vm) {
      document.title = 'PHONK // ' + vm.title
    },
    handleDragEnter: function () {
      this.dndState = 'enter'
    },
    handleDragLeave: function () {
      this.dndState = 'leave'
    },
    toggle_top_container: function () {
      this.backdrop = !this.backdrop
      this.top_container = !this.top_container
    },
    resize: function () {
      this.isMobile =
        window
          .getComputedStyle(document.querySelector('body'), ':before')
          .getPropertyValue('content')
          .replace(/"/g, '') === 'mobile'
      this.top_container = !this.isMobile
    },
    shortcutsHandler: function () {

    },
    toggle_sidepanel: function () {
      this.sharedState.preferences['editor']['sidebar'] = !this.sharedState.preferences['editor']['sidebar']
    },
    changePanel: function (panel) {
      this.panel = panel
    }
  },
  created () {
    var that = this
    Store.on('toggle_top_container', this.toggle_top_container)
    this.resize()
    window.addEventListener('resize', function () {
      that.resize()
    })

    window.addEventListener('keydown', this.shortcutsHandler)

    // show popup when trying to exit app
    window.onbeforeunload = function (e) {
      e = e || window.event
      var msg =
        'Are you sure you want to exit? Remember to save your project before :)'
      if (e) e.returnValue = msg // For IE and Firefox prior to version 4
      return msg // For Safari
    }

    Store.state.browser = {
      editor_width: '300px',
      files_height: '200px',
      console_height: '100px',
      bool: true
    }
    Store.save_browser_config()
    Store.state.browser = {}
    Store.load_browser_config()
    Store.save_browser_config()

    var keyShortcuts = [
      {
        ctrl: true,
        shift: false,
        alt: false,
        meta: false,
        key: 'KeyL',
        execute: ['toggle_load_project', '']
      },
      {
        ctrl: true,
        shift: false,
        alt: false,
        meta: false,
        key: 'KeyR',
        execute: ['project_action', '/stop_or_run']
      },
      {
        ctrl: true,
        shift: false,
        alt: false,
        meta: false,
        key: 'KeyS',
        execute: ['project_editor_save', '']
      },
      {
        ctrl: true,
        shift: false,
        alt: false,
        meta: false,
        key: 'KeyD',
        execute: ['toggle', 'load_documentation']
      },
      {
        ctrl: true,
        shift: false,
        alt: false,
        meta: false,
        key: 'KeyO',
        execute: ['toggle_sidepanel', '']
      },
      {
        ctrl: true,
        shift: false,
        alt: false,
        meta: false,
        key: 'Enter',
        execute: ['live_execute', '']
      },
      {
        ctrl: false,
        shift: false,
        alt: false,
        meta: true,
        key: 'Enter',
        execute: ['live_execute', '']
      },
      {
        ctrl: true,
        shift: false,
        alt: false,
        meta: false,
        key: 'KeyI',
        execute: ['toggle_device_info', '']
      }
    ]
    // load            ctrl + shift + l
    // OK run / stop      ctrl + r // cmd + r
    // OK save            ctrl + s // cmd + s
    // save as
    // execute code    ctrl + shift + x // cmd + shift + x
    // toggle_device_info
    // live execution linux + windows
    // live execution mac
    // live execution generic
    // documentation   ctrl + d // cmd + d
    // dashboard       ctrl + d // cmd + d
    // fullscreen editor ctrl + f // cmd + f
    window.addEventListener('keydown', function (e) {
      // console.log('key pressed', e)

      for (let i in keyShortcuts) {
        if (
          keyShortcuts[i].ctrl === e.ctrlKey &&
          keyShortcuts[i].shift === e.shiftKey &&
          keyShortcuts[i].alt === e.altKey &&
          keyShortcuts[i].meta === e.metaKey &&
          keyShortcuts[i].key === e.code
        ) {
          Store.emit(keyShortcuts[i].execute[0], keyShortcuts[i].execute[1])
          console.log('keyshortcut is pressed ' + keyShortcuts[i].execute)
          e.preventDefault()
          e.stopPropagation()
          window.event.cancelBubble = true
        }
      }
    })

    Store.loadSettings()
  },
  mounted () {
    Store.on('toggle_sidepanel', this.toggle_sidepanel)
  },
  destroyed () {
    Store.removeListener('toggle', this.toggle_section)
    Store.removeListener('toggle_sidepanel', this.toggle_sidepanel)
    window.removeEventListener('keydown', this.shortcutsHandler)
  }
}
</script>

<style src="./assets/css/reset.css"></style>

<style lang="less">
@import 'assets/css/variables.less';
@import 'assets/css/hacks.less';

::-webkit-scrollbar {
  width: 2px; /* remove scrollbar space */
  height: 2px;
  background: transparent; /* optional: just make scrollbar invisible */
}
/* optional: show position indicator in red */
::-webkit-scrollbar-thumb {
  background: #555;
  border: 0;
}

#myCanvas {
  position: absolute;
  top: 0;
  z-index: -1;
}

body {
  background: var(--color-main);
  .font-main-400;
  font-size: @font-size-default;
  color: var(--color-text-light);
  overflow: hidden;
  height: 100vh;
}

.loading {
  display: none;
}

/* hack to export the media queries to javascript
https://www.lullabot.com/articles/importing-css-breakpoints-into-javascript
*/
body:before {
  content: 'desktop';
  display: none;
}

@keyframes initAnim {
  0% {
    transform: translateY(-40px);
    opacity: 0;
  }
  100% {
    transform: translateY(0);
    opacity: 1;
  }
}

@keyframes initAnimSide {
  0% {
    transform: translateX(+40px);
    opacity: 0;
  }
  100% {
    transform: translateX(0);
    opacity: 1;
  }
}

#app {
  height: 100%;
  display: flex;
  flex-flow: column;
}

#top_bar {
  font-size: 1em;
  width: 100%;
  box-shadow: 0px 0px 3px 2px rgba(0, 0, 0, 0.29);
  display: none;

  & > * {
    padding: 10px;
  }

  h1 {
    font-size: 1.5em;
  }

  p {
    padding: 5px 20px;
    font-size: 1em;
    border-radius: 125px;
    margin: 10px;
  }
}

#backdrop {
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  position: absolute;
  z-index: 3;
}

.btn-open {
  margin-left: 5px;
  margin-right: 12px;
}

#main_container {
  display: flex;
  order: 2;
  position: relative;
  height: 100%;
  display: flex;
  flex-flow: row;
  flex-flow: row nowrap;
  overflow: hidden;
}

#top_container {
  order: 1;
  flex-flow: column;
  animation: 0.3s ease-out 0s 1 normal initAnim;
  animation-fill-mode: backwards;
  min-width: 105px;
}

#central_container {
  position: relative;
  flex: 2;
  animation: 0.3s ease-out 0.2s 1 normal initAnim;
  animation-fill-mode: backwards;
  min-width: 100px;
}

#right_container {
  position: relative;
  width: 300px;
  min-width: 100px;
  // overflow-y: auto;
  animation: 0.3s ease-out 0.4s 1 normal initAnimSide;
  animation-fill-mode: backwards;
  min-width: 135px;

  /*
  flex: 1;
  min-width: 250px;
  max-width: 550px;
  */
}

/********* global thingies **/
button {
  position: relative;
  border-radius: 0px;
  cursor: pointer;
  padding: 12px 18px;
  margin: 0;
  border-radius: 1px;
  border: 0;
  color: var(--color-text-light);
  text-transform: uppercase;

  &.icon {
    color: var(--color-icon);
    background: transparent;

    &:hover {
      background: transparent;
    }
  }

  &.boxed {
    border: 1px solid var(--color-lines);
    color: var(--color-accent);
    background-color: var(--color-transparent);

    &:hover {
      background: var(--color-accent);
      color: var(--color-main);
    }
  }

  &.clean {
    padding: 0;
    color: var(--color-text-light);
    background: var(--color-transparent);

    &:hover {
      background: transparent;
      color: var(--color-accent);
    }
  }

  &:hover {
    background-color: var(--color-accent);
    color: var(--color-accent);
  }

  &:active {
    background-color: var(--color-accent);
    color: var(--color-accent);
  }
}

.editor_panel {
  position: relative;
  display: flex;
  flex-flow: row;
  color: var(--color-main);
  padding: 5px 5px;
  border-radius: 1px;
  overflow: hidden;

  .left,
  .right {
    overflow-y: auto;
    padding: 5px;
    box-sizing: border-box;
  }

  .left {
    flex: 0.4;
  }

  .right {
    flex: 0.6;
  }
}

#panels {
  width: 100%;
  height: 100%;
  box-sizing: border-box;
  padding: 0px;
  border-left: 1px solid var(--color-lines);

  #panels-menu {
    display: flex;

    li {
      padding: 17px;
      font-size: 0.8em;
      cursor: pointer;

      &:hover, &.selected {
      }
    }
  }

  .panels-container {
    position: relative;
    display: flex;
    flex-direction: column;
    height: 100%;

    .proto_panel {
      box-sizing: border-box;
      position: relative;
      width: 100%;
      border-radius: 0px;
      margin-bottom: 0px;
      border-bottom: 1px solid var(--color-lines);
      min-height: 45px;

      &.expanded {
        height: 200px;
      }

      &.collapsed {
        height: 48px !important;
      }

      .wrapper {
        overflow: hidden;
        height: 100%;
        display: flex;
        flex-direction: column;
      }

      &:hover {
        .actionbar ul {
          opacity: 1;
          box-sizing: border-box;
        }
      }

      &:hover > ul {
      }

      .actionbar {
        display: flex;
        text-transform: uppercase;
        width: 100%;
        min-height: 45px;
        text-transform: uppercase;
        font-size: 0.9em;
        box-sizing: border-box;
        user-select: none;

        & > * {
          padding: 12px 12px;
          box-sizing: border-box;
        }

        h1 {
          text-align: left;
          flex: 1;
          cursor: pointer;
          font-size: 0.8em;
          color: var(--color-accent);

          &:hover {
            color: darken(@color-accent, 10%);
          }

          .filename {
            text-decoration: underline;
          }
        }

        input {
          min-width: 25px;
          color: var(--color-text-light);
          font-size: 0.8rem;
        }

        &.showIcons {
          ul {
            opacity: 1;
          }
        }

        ul {
          display: block;
          opacity: 0;

          li {
            display: inline-block;
            padding: 5px;
            cursor: pointer;
            font-size: 1em;

            &:hover {
              color: var(--color-accent);
            }

            &.toggled {
              background: var(--color-main-lighter);
              color: var(--color-accent);
              border-radius: 3px 3px 0 0;
            }

            &.enabled {
              color: var(--color-accent);
            }
          }
        }
      }

      .content {
        overflow: auto;
        height: 100%;
      }
    }
  }
}

/* always present */
.banneranim-enter,
.banneranim-leave-active {
  opacity: 0;
  transform: translate3d(0px, -20px, 0) scale3d(1, 1.1, 1);
}

.banneranim-enter-active,
.banneranim-leave-active {
  transition: all 0.3s ease;
}

.scaleanim-enter,
.scaleanim-leave-active {
  opacity: 0;
  transform: translate3d(0px, 0px, 0) scale3d(1, 1.2, 1);
}

.scaleanim-enter-active,
.scaleanim-leave-active {
  transition: all 0.2s ease;
}

.upanim-enter,
.upanim-leave-active {
  opacity: 0;
  transform: translate3d(0px, 5px, 0);
}

.upanim-enter-active,
.upanim-leave-active {
  transition: all 0.2s ease-in-out;
}


.downanim-enter,
.downanim-leave-active {
  opacity: 0;
  transform: translate3d(0px, -5px, 0);
}

.downanim-enter-active,
.downanim-leave-active {
  transition: all 0.2s ease-in-out;
}


.upanim2-enter,
.upanim2-leave-active {
  opacity: 0;
  transform: translate3d(0px, 55px, 0);
}

.upanim2-enter-active,
.upanim2-leave-active {
  transition: all 0.2s ease-in-out;
}

.info-anim-item {
  transition: all 0.3s;
  display: inline-block;
  // margin-right: 10px;
}
.info-anim-enter, .info-anim-leave-to
/* .list-complete-leave-active below version 2.1.8 */ {
  opacity: 0;
  transform: translate3d(30px, 0, 0);
}
.info-anim-leave-active {
  transform: translate3d(300px, 0, 0);
  // position: absolute;
}

/*
.banneranim-active, .banneranim-leave-active {
  transition: all 0.3s ease;
  transform: translate3d(0px, 20px, 0) scale3d(1, 1, 1);
  overflow: hidden;
}

.banneranim-enter, .banneranim-leave-active {
  transform: translate3d(0px, -20px, 0) scale3d(1, 1, 1);
  opacity: 0;
  height: 0px;
}
*/

.banner-anim2-transition {
  transition: all 0.3s ease;
  transform: scale3d(1, 1, 1);
  overflow: hidden;
}

.banner-anim2-enter,
.banner-anim2-leave {
  transform: translate3d(0px, -20px, 0) scale3d(1, 1, 1);
  opacity: 0;
}

/* adjust to different sizes */
@media screen and (max-width: 600px) {
  body:before {
    content: 'mobile';
  }

  #right_container {
    display: none;
  }

  #myeditor {
    padding: 0px;
  }

  #project-options {
    padding: 16px 5px 16px 5px !important;

    #project-actions {
    }
  }

  #toolbar {
    button {
      max-width: 200px;
    }

    .folder {
      display: none;
    }
  }
}

@media screen and (max-width: 525px) {
  .left_side { display: none !important; }
   #toolbar {
     .central_side {
       justify-content: left !important;
     }
     .project_actions button {
       min-width: 100px !important;
       padding: 5px 0px;
     }
     .folder {
       display: none;
     }
  }
}

.panel_above {
  background: var(--color-main);
  border: 0px solid @color-accent;
  box-sizing: border-box;
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 10;
}

.popup_content {
  font-size: 0.6em;
  box-sizing: border-box;

  .title,
  h3 {
    color: var(--color-accent);
    text-transform: uppercase;
    font-size: 0.9em;
    padding: 0px 5px;
  }
}

.disabled {
  opacity: 0.3;
  cursor: not-allowed;
  pointer-events: none;
}
</style>
