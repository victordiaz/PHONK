<template>
  <div id = "console" class = "proto_panel">
    <div class = "wrapper">
      <div class = "actionbar">
        <h1>console</h1>
        <ul>
          <li title = "Show time" class = "material-icons" v-on:click="showTime()" v-bind:class="{'enabled':showingTime}">access_time</li>
          <li title = "Lock scrolling" class = "material-icons" v-on:click="toggleLock()" v-bind:class="{'enabled':lock}">lock</li>
          <li title = "Clear console" class = "material-icons" v-on:click="clear()">delete</li>
        </ul>
      </div>
      <div class = "content" v-show = "showing">
        <ul ref = "log">
          <li
            v-for="(log, i)
            in slicedLogs"
            v-bind:class="log.action"
            :key="i">
            <span class = "time" v-bind:class = "{'off': !showingTime}">{{log.time}}</span>
            <span v-html = "log.text"></span>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
import Store from '../../Store'

export default {
  logsq: [],
  name: 'ConsolePanel',
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
  },
  methods: {
    console_log: function (data) {
      if (!this.consoleIsStarted && this.consoleCanStart) {
        this.consoleIsStarted = true
        this.updateInterval = setInterval(() => {
          this.slicedLogs = this.$options.logsq.slice(this.limitOffset, this.limitOffset + this.limitNum)
        }, 100)
      }

      this.$options.logsq.push({action: data.action, time: data.time, text: data.data})

      if (this.$options.logsq.length > this.limitNum) {
        this.limitOffset = this.$options.logsq.length - this.limitNum
      }

      if (this.lock) return

      // wait until vue rerenders and scroll down console log
      this.$nextTick(function () {
        setTimeout(() => {
          var ul = this.$refs.log
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
    }
  },
  route: {
    data () {
    }
  },
  created () {
    Store.on('console', this.console_log)
    Store.on('project_action', this.project_action)
  },
  destroyed () {
    Store.removeListener('console', this.console_log)
    Store.removeListener('project_action', this.project_action)
  }
}
</script>

<style lang='less'>
@import (reference) '../../assets/css/variables.less';
@import (reference) '../../assets/css/hacks.less';

#console {
  height: 100% !important;
  flex: 2;

  .actionbar {
  }

  .content {
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
     .font-mono-400;
      line-height: 1.8em;
      word-break: break-all;
      font-size: 1rem;
      // .all-transitions;

      img {
        width: 100%;
        max-width: 320px;
      }

      &.log_error::before {
        content: '😭 problem!';
        font-size: 0.8em;
        background: var(--color-error);
        padding: 3px 5px;
        border-radius: 2px;
      }

      &.log_error:hover {
        background: var(--color-error);
      }

      .time {
        &.off {
          display: none;
        }

        font-size: 0.8em;
      }
    }
  }
}
</style>
