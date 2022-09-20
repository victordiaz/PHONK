<template>
  <li
    v-bind:class="{ selected: actionOnProject === project }"
    class="project_item"
  >

    <router-link class = "project_item" @click.native="$parent.load_project(f)" :to="{name: 'editor.load', params: {type: uri.type, folder: uri.folder, project: project.name} }">
      <span class="icon">{{ project.name.substr(0, 2) }}</span>
      <span v-if="showingAction !== 'rename'">{{ project.name }}</span>
      <input v-else v-model = "newName" placeholder="New name..." class = "project-input"/>
    </router-link>

    <div class="actionable" :class="{confirmation: showingAction === 'delete'}">
      <transition name="scaleanim" mode="out-in">
        <!-- general -->
        <div v-if="showingAction === 'general'" key="general" class = "">
          <button
            v-on:click="setAction('clone')"
            class="material-icons"
            title="Clone">
            content_copy
          </button>          
          <button
            v-on:click="setAction('rename')"
            class="material-icons"
            title="Rename">
            drive_file_rename_outline
          </button>
          <button
            v-on:click="setAction('delete')"
            class="material-icons"
            title="Delete">
            delete
          </button>
        </div>

        <!-- delete confirmation -->
        <div v-else-if = "showingAction === 'delete'" key="delete">
          <p>Delete?</p>
          <button class="clean" v-on:click="deleteActionSubmit"><i class="material-icons">check</i></button>
          <button class="clean" v-on:click="backActions"><i class="material-icons">close</i></button>  
        </div>

        <!-- rename -->
        <div v-else-if = "showingAction === 'rename'" class = "" key = "">
          <button class="clean" v-on:click="renameActionSubmit"><i class="material-icons">check</i></button>
          <button class="clean" v-on:click="backActions"><i class="material-icons">close</i></button>
        </div>
        
        <!-- clone -->
        <div v-else-if = "showingAction === 'clone'" class = "" key = "clone">
          <input v-model = "newName" placeholder="Clone name..." class = "project-input"/>
          <button class="clean" v-on:click="cloneActionSubmit"><i class="material-icons">check</i></button>
          <button class="clean" v-on:click="backActions"><i class="material-icons">close</i></button>
        </div>
      </transition>
    </div>
  </li>
</template>

<script>
import Store from '../../Store'

export default {
  name: 'ProjectItem',
  props: {
    project: Object,
    uri: Object,
    isEditing: Boolean
  },
  watch: {
    isEditing(newVal, oldVal) {
      console.log('qq ----->', this.project.name, newVal, oldVal)
      if (oldVal) this.backActions()
    }
  },
  data () {
    return {
      showingAction: 'general',
      actionOnProject: null,
      newName: ''
    }
  },
  methods: {
    openActions: function (f) {
      // console.log('openActions', f)
      this.showingAction = 'general'
      this.actionOnProject = f
    },
    cloneAction: function () {
    },
    setAction: function (action) {
      this.showingAction = action
      this.$parent.setEditingProject(this.project)
    },
    deleteActionSubmit: function () {
      console.log('deleteSubmit')
      Store.project_delete(this.actionOnProject.path)
    },
    renameActionSubmit: function () {
      console.log('renameSubmit')

      if (this.newName !== '') {
        Store.project_rename(this.actionOnProject.path, this.newName)
        this.backActions()
      }
    },
    cloneActionSubmit: function () {
      console.log('cloneSubmit')

      if (this.newName !== '') {
        Store.project_clone(this.actionOnProject.path, this.newName)
        this.backActions()
      }
    },
    backActions: function () {
      this.showingAction = 'general'
      this.newName = ''
    },
    cancelActions: function () {
      this.showingAction = null
      this.actionOnProject = null
      this.newName = ''
    }
  }
}
</script>

<style lang = "less">
@import (reference) "../../assets/css/variables.less";

.project_item {
  justify-content: space-between;

  a {
    cursor: pointer;
  }

  input {
    background: var(--color-transparent);
    color: var(--color-text-light);
    max-width: 20ch;
    border-bottom: 1px solid var(--color-text-light) !important;
  }

  &:hover .actionable {
    display: flex;
  }

}

.project_item .actionable {
  right: 10px;
  display: none;
  background: var(--color-main-lighter);
  align-items: center;
  justify-content: flex-end;
  border-radius: 3px;
  padding: 3px 5px;

  > div {
    display: flex;
    align-items: center;
  }

  button {
    background: var(--color-transparent);
    padding: 3px 6px !important;
    color: var(--color-icon);
    display: flex;

    &:hover {
      color: var(--color-accent);
    }
  }

  &.confirmation {
    background-color: var(--color-error);
    align-items: center;
    display: flex;
    background: #a43f3b;

    p {
      color: #eacdcd;
      margin-right: 12px;
    }

    button {
      color: var(--color-text-light);
    }
  }
}
</style>
