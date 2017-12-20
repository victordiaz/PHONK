<template>
  <div id = "console" class = "proto_panel">
    <div class = "wrapper">
      <div class = "actionbar">
        <h1 v-on:dblclick = "hideshow">console</h1>
        <ul>
          <li title = "Show time" class = "fa fa-clock-o" v-on:click="showTime()" v-bind:class="{'enabled':showingTime}"></li>
          <li title = "Lock scrolling" class = "fa fa-lock" v-on:click="toggleLock()" v-bind:class="{'enabled':lock}"></li>
          <li title = "Clear console" class = "fa fa-trash" v-on:click="clear()"></li>
        </ul>
      </div>
      <div class = "content" v-show = "showing">
        <ul ref = "log">
          <li v-for="log in slicedLogs" v-bind:class="log.action"><span class = "time" v-bind:class = "{'off': !showingTime}">{{log.time}}</span><span v-html = "log.text"></span></li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
import Store from '../Store'

export default {
  name: 'Console',
  data () {
    return {
      msg: 'Hello World!',
      logs: [
      /* {action: 'error', text: 'potato'}
      */],
      count: 0,
      lock: false,
      limitNum: 100,
      limitOffset: 0,
      showingTime: false,
      showing: true
    }
  },
  computed: {
    slicedLogs: function () {
      return this.logs.slice(this.limitOffset, this.limitOffset + this.limitNum)
    }
  },
  methods: {
    console: function (data) {
      // console.log(data)
      this.logs.push({action: data.action, time: data.time, text: data.data})

      if (this.logs.length > this.limitNum) {
        this.limitOffset = this.logs.length - this.limitNum
      }

      if (this.lock) return

      var ul = this.$refs.log
      // wait until vue rerenders
      this.$nextTick(function () {
        ul.scrollTop = ul.scrollHeight
      })
    },
    clear: function () {
      // console.log('clear')
      this.logs = []
    },
    toggleLock () {
      this.lock = !this.lock
    },
    showTime () {
      this.showingTime = !this.showingTime
    },
    project_action (action) {
      if (action === '/run') this.clear()
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
    Store.on('console', this.console)
    Store.on('project_action', this.project_action)

    /*
    var that = this
    setInterval(function () {
      that.console({action: 'msg', data: 'data ' + ++that.count})
    }, 200)
    */
  },
  destroyed () {
    Store.remove_listener('console', this.console)
  }
}
</script>

<style lang='less'>
@import "../assets/css/variables.less";

#console {
  background: rgba(0, 0, 0, 0.9) !important;
  height: 100% !important;
  flex: 2;

  .actionbar {
    ul li:hover, ul li.enabled {
      color: white !important;
    }
  }

  .content {
    color: white !important;
  }

  .content ul {
    height: calc(~"100%");
    width: 100%;
    overflow-y: auto;
    box-sizing: border-box;

    li {
      padding: 2px 0px;
      border-bottom: 1px dashed #333;
      font-family: Source Code Pro;
      line-height: 1.8em;
      word-break: break-all;
      // .anim-fast;

      img {
        width: 100%;
        max-width: 320px;
      }

      &.log_error {
        border-left: 2px solid @error;
        padding-left: 3px;
      }

      &.log_error::before {
        content: "error";
        background: @error;
        padding: 1px 2px;
      }

      &.log_error:hover {
        background: lighten(@error, 10%)
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
