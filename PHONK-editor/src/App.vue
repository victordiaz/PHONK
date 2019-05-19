 <template>
  <div id = "app">
  <div id = "top_container" class = "container">
    <toolbar></toolbar>
  </div>

  <div id="main_container">
    <div v-show = "backdrop" id = "backdrop" v-on:click = "toggle_top_container"></div>

    <div id = "central_container" class = "container">
      <project-checker v-show = "false"></project-checker>

      <transition name = "banneranim">
      <tutorial-loader v-show = "false" transition="banner-anim"></tutorial-loader>
      </transition>

      <!--
      <interface-editor v-show = "false"></interface-editor>
      -->

    <!--  <calender></calender> -->

  <!--

      <webview url = "http://127.0.0.1" :showcontrols = "true"></webview>
      <webview url = "http://www.slashdot.com" :showcontrols = "false"></webview>
      -->

      <!-- <p5></p5> -->

      <!--
      <gcanvas></gcanvas>
      -->

      <router-view
        class=""
        @route-data-loaded = "changeTitle"
        keep-alive
        transition="banner-anim"
        transition-mode="out-in">
      </router-view>

      <!-- <editor></editor> -->

      <!--
      <banner v-if="$route.name === 'editor' " transition="banner-anim">qq<a href="https://www.google.es"> google.es </a></banner>
      -->
    </div>
    <handle orientation = "vertical" container = "right_container"></handle>

    <div id = "right_container" class = "container"  v-show = "sharedState.preferences['other']['show sidebar']">

      <div id = "panels">
        <div id ="editor_panels">
          <documentation></documentation>
          <handle orientation = "horizontal" container = "documentation"></handle>

          <file-manager></file-manager>
          <handle orientation = "horizontal" color = "dark" container = "file_manager"></handle>

          <console></console>

        </div>
      </div>
    </div>

    <debug-panel v-show = "false"></debug-panel>

    <transition-group name = "banneranim">
      <project-load v-if = "sharedState.show_load_project" key = "l_project"></project-load>
      <dashboard v-show = "sharedState.show_dashboard" key = "dashboard"></dashboard>
    </transition-group>

  </div>

  <!--
  <canvas id="myCanvas" width = "800px" height = "800px"></canvas>
  -->
  </div>
</template>

<script>
import Store from './Store'

import DebugPanel from './components/DebugPanel'

import Toolbar from './components/Toolbar'
import Handle from './components/Handle'

import Editor from './components/Editor'
import Banner from './components/views/Banner'

import FileManager from './components/FileManager'
import Console from './components/Console'
import Dashboard from './components/Dashboard'
import Documentation from './components/Documentation'

import ProjectLoad from './components/ProjectLoad'
import Preferences from './components/Preferences'
import About from './components/About'
import TutorialLoader from './components/TutorialLoader'

import Gcanvas from './components/visual/Gcanvas'
import Webview from './components/views/Webview'
import P5 from './components/views/P5js'

import Calender from './components/views/Calender'
import InterfaceEditor from './components/InterfaceEditor'

import ProjectChecker from './components/ProjectChecker'

// import ResizeHandle from 'resize-handle'

export default {
  components: {
    DebugPanel,
    Toolbar,
    Handle,
    Editor,
    ProjectLoad,
    TutorialLoader,
    Preferences,
    About,
    Banner,

    FileManager,
    Console,
    Dashboard,
    Documentation,

    Gcanvas,
    Webview,
    P5,
    Calender,
    ProjectChecker,
    InterfaceEditor

  },
  data () {
    return {
      backdrop: false,
      isMobile: false,
      dndState: '',
      myVar: 'hello',
      sharedState: Store.state
    }
  },
  methods: {
    changeTitle (vm) {
      // console.log(vm)
      document.title = 'Protocoder // ' + vm.title
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
    project_created: function (created) {
      // if (created) this.panel_visibility.new_project = false
    },
    resize: function () {
      this.isMobile = window.getComputedStyle(document.querySelector('body'), ':before').getPropertyValue('content').replace(/"/g, '') === 'mobile'
      // console.log(this.isMobile)
      this.top_container = !this.isMobile
    }
  },
  created () {
    var that = this
    Store.on('toggle_top_container', this.toggle_top_container)
    // Store.emit('project_list_all')
    Store.on('project_created', this.project_created)
    // Store.on('toggle_dashboard', this.toggle_dashboard)

    this.resize()
    window.addEventListener('resize', function () {
      that.resize()
    })

    // show popup when trying to exit app
    window.onbeforeunload = function (e) {
      e = e || window.event
      var msg = 'Are you sure you want to exit? Remember to save your project before :)'
      if (e) e.returnValue = msg // For IE and Firefox prior to version 4
      return msg // For Safari
    }

    Store.state.browser = {
      'editor_width': '300px',
      'files_height': '200px',
      'console_height': '100px'
    }
    Store.save_browser_config()
    Store.state.browser = {}
    Store.load_browser_config()
    // console.log(Store.state.browser.editor_width)

    var keyShortcuts = [
      { ctrl: true, shift: false, alt: false, meta: false, key: 'KeyL', execute: ['toggle', 'load_project'] },
      { ctrl: true, shift: false, alt: false, meta: false, key: 'KeyR', execute: ['project_run', ''] },
      { ctrl: true, shift: false, alt: false, meta: false, key: 'KeyS', execute: ['project_editor_save', ''] },
      //
      //
      { ctrl: true, shift: false, alt: false, meta: false, key: 'KeyD', execute: ['toggle', 'load_documentation'] },
      { ctrl: true, shift: false, alt: false, meta: false, key: 'KeyH', execute: 'qq21' },
      { ctrl: true, shift: false, alt: false, meta: false, key: 'KeyF', execute: 'qq21' },
      { ctrl: true, shift: false, alt: false, meta: false, key: 'KeyI', execute: ['toggle_device_info', ''] }

    ]
    // load            ctrl + shift + l
    // run / stop      ctrl + r // cmd + r
    // save            ctrl + s // cmd + s
    // save as
    // execute code    ctrl + shift + x // cmd + shift + x
    // documentation   ctrl + d // cmd + d
    // dashboard       ctrl + d // cmd + d
    // fullscreen editor ctrl + f // cmd + f
    window.addEventListener('keydown', function (e) {
      // console.log('key pressed', e)

      for (var i in keyShortcuts) {
        if (keyShortcuts[i].ctrl === e.ctrlKey &&
            keyShortcuts[i].shift === e.shiftKey &&
            keyShortcuts[i].alt === e.altKey &&
            keyShortcuts[i].meta === e.metaKey &&
            keyShortcuts[i].key === e.code) {
          Store.emit(keyShortcuts[i].execute[0], keyShortcuts[i].execute[1])
          // console.log('keyshortcut is pressed ' + keyShortcuts[i].execute)
          e.preventDefault()
          e.stopPropagation()
          window.event.cancelBubble = true
        }
      }
    })

    Store.loadSettings()
  },
  mounted () {
    /*
    var canvas = document.getElementById('myCanvas')
    canvas.width = window.innerWidth
    canvas.height = window.innerHeight

    var c = canvas.getContext('2d')
    c.scale(1, 1)

    Math.easeInOutQuad = function (t, b, c, d) {
      t /= d / 2
      if (t < 1) return c / 2 * t * t + b
      t--

      return -c / 2 * (t * (t - 2) - 1) + b
    }

    var w = canvas.width
    var h = canvas.height
    var dots = []
    var NUM_DOTS = 45

    // update
    for (var i = 0; i < NUM_DOTS; i++) {
      dots.push({ 'cx': 0, 'x': w * Math.random(), 'y': h * Math.random(), 'r': 80 * Math.random(), 'o': 0 })
    }

    function draw (t) {
      c.clearRect(0, 0, w, h)

      c.lineWidth = 1 // 2 * Math.random()
      c.fillStyle = 'rgba(255, 87, 34, 0)' // + Math.random() + ')'
      c.strokeStyle = 'rgba(255, 255, 255, 0.1)'

      // draw
      for (var i = 0; i < NUM_DOTS; i++) {
        var d = dots[i]
        c.beginPath()
        // var mx = Math.easeInOutQuad(t, d.cx, d.cx - d.x, 2000)
        // if (i === 0) console.log(d.cx, d.x, mx)
        // console.log(d.cx)
        c.arc(d.x, d.y, d.r, 0, 2 * Math.PI)
        c.stroke()
        c.fill()
      }
      window.requestAnimationFrame(draw)
    }

    console.log(draw)
    window.requestAnimationFrame(draw)
    */
  },
  destroyed () {
    Store.remove_listener('toggle', this.toggle_section)
    Store.remove_listener('project_created', this.project_created)
  },
  events: {
    'run': function (msg) {
      // console.log('event parent run ' + msg)
      return true
    }
  }
}
</script>

<style src="./assets/css/reset.css"></style>

<style lang="less">

@import "assets/css/variables.less";
@import "assets/css/hacks.less";

::-webkit-scrollbar {
    width:2px;  /* remove scrollbar space */
    height: 2px;
    background: transparent;  /* optional: just make scrollbar invisible */
}
/* optional: show position indicator in red */
::-webkit-scrollbar-thumb {
    background: #555;
    border: 0;
}

#myCanvas {
  position: absolute;
  top: 0;
  z-index: -1
}

body {
  /* background: linear-gradient(180deg, @backgroundColor, @backgroundColor_second); */
  background: @mainColor;
  font-family: Roboto, Helvetica Neue, Helvetica, Arial, sans-serif;
  color: @primaryTextColor;
  font-size: @defaultFontSize;
  overflow: hidden;
  height: 100vh;
  font-size: 18px;
}

.loading {
  display: none;
}

/* hack to export the media queries to javascript
https://www.lullabot.com/articles/importing-css-breakpoints-into-javascript
*/
body:before {
  content: "desktop";
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
  color: black;
  font-size: 1em;
  width: 100%;
  background: rgb(245, 211, 40);
  box-shadow: 0px 0px 3px 2px rgba(0, 0, 0, 0.29);
  display: none;

  & > * {
    padding: 10px;
  }

  h1 {
    font-size: 1.5em;
  }

  p {
    background: rgba(0, 0, 0, 0.2);
    color: white;
    padding: 5px 20px;
    font-size: 1em;
    border-radius: 125px;
    margin: 10px;
    font-weight: 600;
    border: 1px solid rgba(0, 0, 0, 0.2);
  }
}

#backdrop {
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  position: absolute;
  z-index: 3;
}

.btn-toolbar {
  display: none;
  font-size: 2em;
  color: @mainColor;
  cursor: pointer;
  z-index: 3;

  &:hover {
    color: darken(@accentColor, 10%);
  }

  &:active {
    color: darken(@accentColor, 30%);
  }
}

.btn-open {
  margin-left: 5px;
  margin-right: 12px;
}

.btn-close {
  cursor: pointer;
  color: @accentColor;
  padding: 10px 15px;
  position: absolute;
  right: 0;

  &:hover {
    color: lighten(@accentColor, 10%);
  }

  &:active {
    color: darken(@accentColor, 10%);
  }
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

  .container {
  }
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
  background: whitesmoke;

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
  border: 0px;
  font-family: 'Open Sans';
  color: @primaryTextColor;
  text-transform: uppercase;
  font-weight: 700;
  font-size: 0.7em;
  background-color: @accentColor;

  &:hover {
    background-color: lighten(@accentColor, 10%);
  }

  &:active {
    background-color: darken(@accentColor, 10%);
  }

}

.btn-primary {
  color: #fff;
  background-color: #474747;
  border: 0px;
}

.editor_panel {
  position: relative;
  display: flex;
  flex-flow: row;
  background: white;
  color: @mainColor;
  padding: 5px 5px;
  border-radius: 1px;
  overflow: hidden;

 	.left, .right {
    overflow-y: auto;
    padding: 5px;
    box-sizing: border-box;
 	}

  .left {
    flex: 0.40;
  }

	.right {
    flex: 0.60;
	}

}

#panels {
  width: 100%;
  height: 100%;
  // overflow: hidden;
  box-sizing: border-box;
  padding: 0px;

  #editor_panels {
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
      font-family: 'Open Sans';
      color: white;
      border: 0px solid rgba(0, 0, 0, 0);
      min-height: 45px;
      // overflow: hidden;
      border-bottom: 1px solid rgba(88, 88, 88, 0.28);
      /* transition: height 0.3 ease-in-out; */

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
          box-sizing: border-box;
        }
      }

      &:hover > ul {
      }

    	.actionbar {
        color: #828282;
        display: flex;
        font-weight: 700;
        text-transform: uppercase;
        font-size: 0.8em;
        border-bottom: 0px solid rgba(255, 255, 255, 0.1);
        width: 100%;
        min-height: 45px;
        text-transform: uppercase;;
        font-weight: 700;
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
          font-weight: 700;
          font-size: 0.8em;
          color: @accentColor;

          &:hover {
            color: darken(@accentColor, 10%);
          }

    			.filename {
    				text-decoration: underline;
    			}
    	  }

        input {
          min-width: 25px;
        }

    		ul {
          display: block;;

    			li {
    				display: inline-block;
    				padding: 0px 5px;
    				cursor: pointer;
            font-size: 0.8em;

    				&:hover {
              color: @mainColor;
    				}

            &.enabled {
              color: @accentColor;
            }

    			}

    		}
    	}

      .content {
        color: black;
        // overflow-y: hidden;
        overflow: auto;
        height: 100%;

        & > * {
          padding: 10px;
        }
      }
    }
  }
}

/* always present */
.banneranim-enter, .banneranim-leave-active {
  opacity: 0;
  transform: translate3d(0px, -20px, 0) scale3d(1, 1.1, 1);
}

.banneranim-enter-active, .banneranim-leave-active {
  transition: all 0.3s ease;
}

.upanim-enter, .upanim-leave-active {
  opacity: 0;
  transform: translate3d(0px, 5px, 0);
}

.upanim-enter-active, .upanim-leave-active {
  transition: all 0.2s ease-in-out;
}

.upanim2-enter, .upanim2-leave-active {
  opacity: 0;
  transform: translate3d(0px, 55px, 0);
}

.upanim2-enter-active, .upanim2-leave-active {
  transition: all 0.2s ease-in-out;
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

.banner-anim2-enter, .banner-anim2-leave {
  transform: translate3d(0px, -20px, 0) scale3d(1, 1, 1);
  opacity: 0;
}


.actionable {
  button {
    padding: 5px 8px;
    margin-left: 3px;
    // background: rgba(0, 0, 0, 0.1);
  }
}


/* adjust to different sizes */
@media screen and (max-width: 600px) {

  body:before {
    content: "mobile";
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
      direction: rtl;
    }
  }

}

.panel_above {
  background: @mainColor;
  border: 0px solid @accentColor;
  box-sizing: border-box;
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 3;
}


.popup_content {
  font-size: 0.6em;
  box-sizing: border-box;

  &:hover {
    /* background: rgba(255, 255, 255, 0.1); */
  }

  .title, h3 {
    color: @accentColor;
    text-transform: uppercase;
    font-size: 0.9em;
    font-weight: 700;
    padding: 0px 5px;
  }

}


.bold {
  font-weight: 600 !important;
}

.disabled {
  opacity: 0.5;
  cursor: not-allowed;
  pointer-events: none;
}

</style>
