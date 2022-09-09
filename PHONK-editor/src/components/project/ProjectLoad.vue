<template>
  <div id="project-load-container" class="editor_panel panel_above">
    <div class="container">
      <span class="debug" v-if="false">
        {{
        store.state.projects["playground"]
        }}
      </span>

      <div>
        <h3>Create a project</h3>
        <project-new></project-new>
      </div>

      <div class="section">
        <h3>Load a project</h3>

        <div v-if="store.state.projects" id="project-load">
          <div class="left">
            <div class="project_list" v-for="(p, pindex) in projectsOrdered" :key="pindex">
              <h1>{{ pindex }}</h1>
              <ul>
                <li
                  v-for="(f, index) in p"
                  v-bind:class="{
                    selected: selected == index && pselected == pindex,
                  }"
                  v-on:click="choose_folder(pindex, index, $event)"
                  v-bind:id="f.name"
                  :key="index"
                >
                  <span class="icon">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="white" width="18px" height="18px"><path d="M0 0h24v24H0V0z" fill="none"/><path d="M9.17 6l2 2H20v10H4V6h5.17M10 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V8c0-1.1-.9-2-2-2h-8l-2-2z"/></svg>
                  </span>
                  {{ f.name }}
                </li>
              </ul>
            </div>
          </div>
          <div class="right">
            <ul v-if="pselected !== -1">
              <ProjectItem
                v-for="project in folder_chosen"
                :project="project"
                :uri="uri"
                :key="project.name"
                :isEditing="editingProject.name === project.name"
              />
            </ul>
          </div>
        </div>
        <div v-else class="no-projects">
          <p>There is a problem loading your projects</p>
        </div>
      </div>
    </div>
    <!-- container -->
  </div>
</template>

<script>
import Store from '../../Store'
import _ from 'lodash'
import ProjectNew from './ProjectNew'
import ProjectItem from './ProjectItem'

export default {
  components: {
    ProjectNew,
    ProjectItem
  },
  name: 'ProjectLoad',
  data () {
    return {
      store: Store,
      id: Store.state.id,
      pselected: -1,
      selected: -1,
      editingProject: { name: '' },
      uri: {
        type: '',
        folder: '',
        fullpath: ''
      }
    }
  },
  computed: {
    folder_chosen: function () {
      let files = this.store.state.projects[this.pselected][this.selected]
        .files
      return _.orderBy(files, [(files) => files.name.toLowerCase()], ['asc'])
    },
    projectsOrdered: function () {
      // return _.sortKeysBy(this.store.state.projects)
      // const ordered = {}
      /*
      Object.keys(this.store.state.projects).sort().forEach((key) => {
        ordered[key] = this.store.state.projects[key]
      });
      */
      const ordered = {
        playground: this.store.state.projects['playground'],
        examples: this.store.state.projects['examples']
      }
      return ordered
    }
  },
  methods: {
    new_project: function () {
      return true
    },
    choose_folder: function (pindex, index) {
      this.pselected = pindex
      this.selected = index

      this.uri.type = pindex
      this.uri.folder = this.store.state.projects[pindex][index].name

      // console.log(this.uri.type, this.uri.folder)
      // console.log(this.state.projects[pindex][index].files)
    },
    load_project: function (project) {
      // this.uri.fullpath = this.uri.folder + '/' + folder.name
      // Store.emit('project_action', '/run', this.uri.fullpath)
      // Store.emit('project_load', this.uri.fullpath)
      // console.log(this.uri.fullpath, this.uri.type, this.uri.folder, project.name)

      this.close()
      var to = {
        name: 'editor.load',
        params: {
          type: this.uri.type,
          folder: this.uri.folder,
          project: project.name
        }
      }
      this.$router.push(to)
    },
    // load from app
    load_project_from_app: function (data) {
      console.log('loading... ' + data.type)
      var to = {
        name: 'editor.load',
        params: { type: data.type, folder: data.folder, project: data.name }
      }
      this.$router.push(to)
    },
    close: function () {
      this.store.state.show_load_project = false
      Store.emit('toggle', 'load_project')
    },
    setEditingProject: function (project) {
      this.editingProject = project      
    }
  },
  mounted () {
    // this.cancelActions()
  },
  created () {
    Store.on('new_project', this.new_project)
    Store.on('load_project_from_app', this.load_project_from_app)
  },
  destroyed () {
    Store.removeListener('new_project', this.new_project)
    Store.removeListener('load_project_from_app', this.load_project_from_app)
  }
}
</script>

<style lang="less">
@import (reference) '../../assets/css/variables.less';
@import (reference) '../../assets/css/hacks.less';

#project-load-container {
  display: flex;
  flex-direction: column;
  margin-bottom: 12px;
  padding: 0px;
  font-size: 1rem;
  overflow: auto;
  z-index: 5;

  .container {
    max-width: 1000px;
    margin: 0 auto;
    width: 100%;
  }

  h3,
  #editor_panel_new,
  #project-load {
    margin: 8px 12%;
    color: var(--color-text-light);
  }

  .project-input {
    flex: 2;
    width: 100%;
    min-width: 20%;
    outline: none;
    border: none;
    width: 100%;
    box-sizing: border-box;
    font-size: 1rem;
    padding: 0 12px; 
  }

  h3 {
    font-size: 0.9rem;
    margin-top: 50px;
    margin-bottom: 15px;
    .font-main-400;
  }

  .debug {
    font-size: 0.7em;
  }

  .section {
    position: relative;
  }
}

#project-load {
  display: flex;
  flex-direction: row;
  border: 1px solid var(--color-lines);
  font-size: 1em;
  height: calc(100vh - 288px);

  .no-projects {
    p {
      font-size: 1.5em;
      line-height: 1.3em;
      padding: 2em;
      text-align: center;
    }
  }

  ul {
    text-align: left;
    list-style: none;
    margin: 0;
    padding: 0;
    user-select: none;

    li, .project_item {
      position: relative;
      color: var(--color-text-light);
      padding: 3px 10px;
      display: flex;
      line-height: 1.4em;
      align-items: center;
      text-decoration: none;
      .font-main-400;
      box-sizing: border-box;

      .icon {
        max-width: 30px;
        min-width: 30px;
        height: 30px;
        border: 1px solid var(--color-lines);
        color: var(--color-text-light-faded);
        display: flex;
        justify-content: center;
        align-content: center;
        align-items: center;
        margin-right: 10px;
        padding: 5px;
        border-radius: 5px;

        svg {
          width: 28px;
          height: 23px;
          fill: var(--color-text-light-faded);
        }
      }

      span {
        flex: 2;
      }

      &:hover,
      &.selected {
        color: var(--color-accent);
        border-radius: 2px;
        position: relative;
      }

      &.selected {
        color: var(--color-accent);
      }

      &.selected {
        background: var(--color-main-lighter);

        .icon {

          svg {
            fill: var(--color-accent);
          }
        }
      }
    }
  }

  .left {
    .scrollbar;

    h1 {
      color: var(--color-accent);
      text-transform: uppercase;
      font-size: 0.7em;
      padding: 18px 18px;
      margin-bottom: 0px;
    }

    .project_list {
      margin-bottom: 22px;

      ul li {
        cursor: pointer;
      }
    }
  }
  .right {
    text-align: center;
    border-left: 1px solid var(--color-lines);
    position: relative;
    overflow-y: auto;
    .scrollbar;

    .action {
      opacity: 1;
      position: sticky;
      right: -10px;
      padding: 2px 10px;

      &:hover {
        color: var(--color-accent);
      }
    }

    .img-cover {
      width: 100px;
      height: 100px;
    }

    .actions {
      margin-top: 10px;

      .action-element {
        width: 20px;
        height: 20px;
        display: inline-block;
      }
    }

    ul {
      height: 100%;
    }
  }

  #new_folder {
    font-size: 0.8em;
    display: inline-block;
    border-radius: 2px;
    margin-left: 10px;
    margin-top: 28px;
    padding: 8px;
    text-transform: uppercase;

    &:hover {
    }

    i {
      margin-right: 5px;
    }
  }
}
</style>
