<template>
  <div class = "editor_panel check_list main_shadow">
    <div class = "left">
      <div>
        <h1> examples </h1>
        <ul>
          <li
            v-for = "p in projects_to_check"
            v-bind:id = "p.name"
            v-bind:class = "{'ok':p.status === 'ok', 'nop':p.status === 'fail'}"
            :key="p.name">
            <span class = "parent">{{p.parent}}</span><span class = "name">{{p.name}}</span>
          </li>
        </ul>
      </div>

    </div>
    <div class = "right">
      <div class = "grouped_buttons">
        <button v-on:click = "start_checking">run</button>
        <button>stop</button>
      </div>
      <ul class = "log">
        <li>qq1</li>
        <li>qq2</li>
      </ul>
    </div>
   </div>
</template>

<script>
import helpers from '../../Helpers'
import Store from '../../Store'

export default {
  name: 'ProjectChecker',
  data () {
    return {
      projects_to_check: [],
      folder_chosen: [],
      checkInterval: null,
      uri: {
        type: '',
        folder: '',
        fullpath: ''
      }
    }
  },
  methods: {
    load_project: function (project) {
      console.log(this.uri.fullpath, this.uri.type, this.uri.folder, project.name)
      Store.emit('toggle', 'load_project')
      this.$route.router.go({name: 'editor.load', params: { type: this.uri.type, folder: this.uri.folder, project: project.name }})
    },
    project_listed: function () {
      helpers.clearArray(this.projects_to_check)
      for (var i in Store.state.projects) {
        if (Store.state.projects[i].name === 'examples') {
          for (var j in Store.state.projects[i].files) {
            for (var k in Store.state.projects[i].files[j].files) {
              // this.projects_to_check.push(Store.state.projects[i].files[j].files[k])

              // add status param
              this.projects_to_check.push(Object.assign({}, Store.state.projects[i].files[j].files[k], { status: 'not', gparent: Store.state.projects[i].name, parent: Store.state.projects[i].files[j].name }))
              // this.projects_to_check[this.projects_to_check.length - 1].status = 'ok'
            }
          }
        }
      }
      // console.log(this.projects_to_check)

      // this.start_checking()
    },
    start_checking: function () {
      clearInterval(this.checkInterval)
      var that = this
      var i = 0
      that.projects_to_check[0].status = 'ok'
      that.projects_to_check[5].status = 'ok'
      that.projects_to_check[7].status = 'fail'

      this.checkInterval = setInterval(function () {
        // console.log(that.projects_to_check[i])
        if (i >= that.projects_to_check.length) {
          console.log('clear')
          clearInterval(that.checkInterval)
        } else {
          Store.project_stop_all_and_run(that.projects_to_check[i])
          that.projects_to_check[i++].status = 'ok'
          // console.log(that.projects_to_check[i].name)
          // console.log(i + ' ' + that.projects_to_check.length)
        }
      }, 5000)
    }
  },
  created () {
    Store.on('project_listed_all', this.project_listed)
  },
  destroyed () {
    Store.removeListener('project_listed_all', this.project_listed)
  }
}
</script>

<style lang='less'>
@import (reference) '../../assets/css/variables.less';

.check_list {
  margin-bottom: 12px;

  h1 {
    color: var(--color-accent);
    padding: 5px 0px;
    border-bottom: 0px solid var(--color-accent);
    margin-bottom: 5px;
    text-transform: uppercase;
  }

  h2 {
    padding: 3px 2px;
  }

  ul {
    li {
      border-radius: 2px;
      padding: 3px 2px;
      margin: 2px;
      font-size: 1em;

      .name {
        margin-left: 5px;
      }

      &.selected {
        border-radius: 1px;
      }

      &.ok {
      }

      &.nop {
      }
    }
  }

  .right {
    display: flex;
    flex-direction: column;
    .grouped_buttons {
      width: 100%;
      display: flex;

      button {
        width: 100%;
        margin: 0px 5px;
      }
    }
    .log {
      border-radius: 2px;
      margin: 10px 5px 0px 5px;
      font-size: 0.8em;
      .font-mono-400;
      height: 100%;
    }
  }
}

.full {
  position: fixed;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  width: 100%;
  height: 100%;
  z-index: 10;
  padding: 0;
}
</style>
