<template>
  <div id = "project-load-container" class = "editor_panel panel_above">
    <!--
    <div class = "btn-sidebar btn-close" v-on:click = "close">
      <i class = "fa fa-close"></i>
    </div>
    -->

    <h3>Create a project</h3>
    <project-new></project-new>

    <h3>Load a project</h3>
    <div id = "project-load">
      <div class="left">
        <div class = "project_list" v-for="(p, pindex) in sharedState.projects">
          <h1> {{pindex}} </h1>

          <ul>
            <li v-for = "(f, index) in p" v-bind:class="{'selected':selected == index && pselected == pindex}" v-on:click = "choose_folder(pindex, index, $event)" v-bind:id = "f.name"> {{f.name}} </li>
          </ul>
        </div>
      </div>
      <div class="right">
        <div class = "project_info">
          <p>Double click to open</p>
          <div class="img-cover"></div>
          <div class = "actions">
            <div class="action-element"></div>
            <div class="action-element"></div>
            <div class="action-element"></div>
          </div>
        </div>
        <ul>
          <li v-for = "f in folder_chosen" v-on:click = "load_project(f)"> {{f.name}} </li>
        </ul>
      </div>
     </div>
   </div>
</template>

<script>
import store from '../Store'
import _ from 'lodash'
import ProjectNew from './ProjectNew'

export default {
  components: {
    ProjectNew
  },
  name: 'ProjectLoad',
  data () {
    return {
      sharedState: store.state,
      id: store.state.id,
      pselected: -1,
      selected: -1,
      folder_chosen: [],
      uri: {
        type: '',
        folder: '',
        fullpath: ''
      }
    }
  },
  methods: {
    new_project: function () {
      return true
    },
    choose_folder: function (pindex, index, event) {
      // console.log(pindex, index, this.projects[pindex].files[index].name)
      this.pselected = pindex
      this.selected = index

      this.uri.type = pindex
      this.uri.folder = this.sharedState.projects[pindex][index].name

      // console.log(this.uri.type, this.uri.folder)
      // console.log(this.state.projects[pindex][index].files)

      this.folder_chosen = _.orderBy(this.sharedState.projects[pindex][index].files, 'name')
    },
    load_project: function (project) {
      // this.uri.fullpath = this.uri.folder + '/' + folder.name
      // store.emit('project_action', '/run', this.uri.fullpath)
      // store.emit('project_load', this.uri.fullpath)
      // console.log(this.uri.fullpath, this.uri.type, this.uri.folder, project.name)

      this.close()
      var to = {name: 'editor.load', params: { type: this.uri.type, folder: this.uri.folder, project: project.name }}
      this.$router.push(to)
    },
    // load from app
    load_project_from_app: function (data) {
      console.log('loading... ' + data.type)
      var to = {name: 'editor.load', params: { type: data.type, folder: data.folder, project: data.name }}
      this.$router.push(to)
    },
    close: function () {
      this.sharedState.show_load_project = false
      store.emit('toggle', 'load_project')
    }
  },
  created () {
    store.on('new_project', this.new_project)
    store.on('load_project_from_app', this.load_project_from_app)
  },
  destroyed () {
    store.remove_listener('new_project', this.new_project)
    store.remove_listener('load_project_from_app', this.load_project_from_app)
  }
}
</script>

<style lang='less'>
@import "../assets/css/variables.less";

#project-load-container {
  display: flex;
  flex-direction: column;
  background: @backgroundColorTerciary;
  margin-bottom: 12px;
  padding: 0px;
  font-size: 1em;
  color: #222;
  overflow: auto;

  h3, #editor_panel_new, #project-load {
    margin: 8px 12%;
  }

  h3 {
    // text-transform: uppercase;
    font-weight: 700;
    font-size: 0.8em;
    margin-top: 25px;
  }
}

#project-load {
  display: flex;
  background: @backgroundColorSecondary;
  flex-direction: row;
  font-size: 1em;
  font-weight: 100;

  ul {
    text-align: left;
    list-style: none;
    margin: 0;
    padding: 0;
    cursor: pointer;

    li {
      padding: 5px 10px;
      font-weight: 300;

      &:hover, &.selected {
        background-color: @accentColor;
        color: @primaryTextColor;
        border-radius: 1px;
      }

      &:active {
        background: darken(@accentColor, 10%);
      }
    }
  }

  .left {
    h1 {
      color: @accentColor;
      font-weight: 600;
      text-transform: uppercase;
      font-size: 0.7em;
      padding: 5px 10px;
      margin-bottom: 0px;
      // border-bottom: 0px solid @accentColor;
    }

    .project_list {
      margin-bottom: 22px;
    }
  }
  .right {
    color: black;
  	text-align: center;
    border-left: 3px solid white;

    .project_info {
      display: none;
    }

    .img-cover {
      width: 100px;
      height: 100px;
      background-color: blue;
    }

    .actions {
    	margin-top: 10px;

    	.action-element {
    		width: 20px;
  		  height: 20px;
  		  background-color: green;
  		  display: inline-block;
    	}
    }

    ul {
      padding: 0.5em;
      font-weight: 500;
    }
  }
}

</style>
