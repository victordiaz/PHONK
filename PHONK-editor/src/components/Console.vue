<template>
  <div id = "console" class = "proto_panel">
    <div class = "wrapper">
      <div class = "actionbar">
        <h1 v-on:dblclick = "hideshow">console</h1>
        <ul>
          <li title = "Show time" class = "material-icons" v-on:click="showTime()" v-bind:class="{'enabled':showingTime}">access_time</li>
          <li title = "Lock scrolling" class = "material-icons" v-on:click="toggleLock()" v-bind:class="{'enabled':lock}">lock</li>
          <li title = "Clear console" class = "material-icons" v-on:click="clear()">delete</li>
        </ul>
      </div>
      <div class = "content" v-show = "showing">
        <ul ref = "log">
          <li v-for="(log, i) in slicedLogs" v-bind:class="log.action">
            <span class = "time" v-bind:class = "{'off': !showingTime}">{{log.time}}</span>
            <span v-html = "log.text"></span>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
import Store from '../Store'

export default {
  logsq: [],
  name: 'Console',
  data () {
    return {
      msg: 'Hello World!',
      logs: [
      /* {action: 'error', text: 'potato'}
      */],
      count: 0,
      consoleCanStart: true,
      consoleIsStarted: false,
      lock: false,
      limitNum: 200,
      limitOffset: 0,
      showingTime: false,
      showing: true,
      slicedLogs: [],
      i: 0
    }
  },
  computed: {
    // slicedLogs: function () {
    //   return this.logs.slice(this.limitOffset, this.limitOffset + this.limitNum)
    // }
  },
  methods: {
    console_log: function (data) {
      // console.log('log', data, this.consoleIsStarted, this.consoleCanStart)
      if (!this.consoleIsStarted && this.consoleCanStart) {
        // console.log('qq1')
        this.consoleIsStarted = true
        this.updateInterval = setInterval(() => {
          this.slicedLogs = this.$options.logsq.slice(this.limitOffset, this.limitOffset + this.limitNum)
          // console.log('qq2')
        }, 100)
      }

      // console.log(data)
      this.$options.logsq.push({action: data.action, time: data.time, text: data.data})

      if (this.$options.logsq.length > this.limitNum) {
        this.limitOffset = this.$options.logsq.length - this.limitNum
      }

      if (this.lock) return

      // wait until vue rerenders and scroll down console log
      this.$nextTick(function () {
        setTimeout(() => {
          var ul = this.$refs.log
          // console.log('ul', ul, this.i++)
          ul.scrollTop = ul.scrollHeight
        }, 300)
      })
    },
    clear: function () {
      this.$options.logsq = []
      this.slicedLogs = []
      this.limitOffset = 0
    },
    toggleLock () {
      this.lock = !this.lock
    },
    showTime () {
      this.showingTime = !this.showingTime
    },
    project_action (action) {
      if (action === '/stop_all_and_run') {
        this.consoleCanStart = true
        this.clear()
      } else if (action === '/stop') {
        this.consoleCanStart = false
        clearInterval(this.updateInterval)
        this.consoleIsStarted = false
      }
    },
    hideshow: function () {
      // this.showing = !this.showing
    }
  },
  route: {
    data () {
    }
  },
  created () {
    Store.on('console', this.console_log)
    Store.on('project_action', this.project_action)

    /*
    var that = this
    setInterval(function () {
      that.console({action: 'msg', data: 'data ' + ++that.count})
    }, 200)
    */
  },
  destroyed () {
    Store.removeListener('console', this.console_log)
    Store.removeListener('project_action', this.project_action)
  }
}
</script>

<style lang='less'>
@import (reference) '../assets/css/variables.less';
@import (reference) '../assets/css/hacks.less';

#console {
  height: 100% !important;
  flex: 2;

  .actionbar {
  }

  .content {
    color: white !important;
  }

  .content ul {
    height: calc(~'100%');
    width: 100%;
    overflow-y: auto;
    .scrollbar;
    box-sizing: border-box;

    li {
      padding: 10px;
      border-bottom: 1px dashed #333;
      font-family: @editorFont;
      line-height: 1.8em;
      word-break: break-all;
      font-size: 1rem;
      // .anim-fast;

      img {
        width: 100%;
        max-width: 320px;
      }

      &.log_error {
        // border-left: 3px solid @accentColor;
        // padding-left: 3px;
      }

      &.log_error::before {
        content: '😭 problem!';
        font-size: 0.8em;
        background: @error;
        padding: 3px 5px;
        border-radius: 2px;
      }

      &.log_error:hover {
        background: lighten(@error, 10%);
      }

      &:hover {
        background-color: rgba(255, 255, 255, 0.05);
      }

      .time {
        &.off {
          display: none;
        }

        color: rgba(255, 255, 255, 0.5);
        font-size: 0.8em;
      }
    }
  }
}
</style>
