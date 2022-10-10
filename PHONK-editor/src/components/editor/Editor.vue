<template>
  <div id="myeditor">
    <div id="editor_container" :class="{ project_loaded: isProjectLoaded }">
      <EditorBar :tabs="tabs" :currentTab="currentTab" />

      <div v-if="sharedState.show_ui_editor" id="uieditor-container">
        <UIEditor />
      </div>
      <EditorCode
        v-show="!sharedState.show_ui_editor"
        :tabs="tabs"
        :currentTab="currentTab"
        :project="project"
        :files="files" />
    </div>
    <message-error
      v-if="isError"
      text="There is a problem opening the file 😅"
      action="try again"
      :actionfn="try_again"
    ></message-error>
  </div>
</template>

<script>
import store from '../../Store'
import helpers from '../../Helpers'
import EditorBar from './EditorBar.vue'
import EditorCode from './EditorCode.vue'
import UIEditor from '../uieditor/UIEditor.vue'
import MessageError from '../views/MessageError'

export default {
  name: 'EditorPanel',
  components: {
    EditorBar,
    EditorCode,
    UIEditor,
    MessageError
},
  data () {
    return {
      sharedState: store.state,
      currentTab: -1,
      tabs: [{ name: 'main.js', text: '', modified: '' }],
      isError: false,
      sessions: [],
      project: null,
      files: null,
      d: null,
      editor: '',
      isProjectLoaded: true
    }
  },
  watch: {
    $route: 'fetchData'
  },
  computed: {
    isUIFile() {
      let filename = 'main.ui'
      return filename.endsWith('.ui')
    }
  },
  mounted () {
    this.fetchData()
  },
  created () {
    store.on('project_loaded', this.project_loaded)
    store.on('file_loaded', this.load_file)
    store.emit('project_list_all')
    store.on('project_created', this.project_created)
    store.on('project_editor_save', this.project_save)
    store.on('project_saved', this.project_saved)
  },
  destroyed () {
    store.removeListener('project_loaded', this.project_loaded)
    store.removeListener('file_loaded', this.load_file)
    store.removeListener('project_created', this.project_created)
    store.removeListener('project_editor_save', this.project_save)
    store.removeListener('project_saved', this.project_saved)
  },
  methods: {
    showUIEditor: function (val) {
      console.log(val)
      this.sharedState.show_ui_editor = val
    },
    fetchData: function () {
      var type = this.$route.params.type
      var folder = this.$route.params.folder
      var project = this.$route.params.project
      var url = type + '/' + folder + '/' + project

      if (type) {
        store.project_load('/' + url)
      }

      this.title = url

      if (!type) this.sharedState.show_load_project = true

      this.isProjectLoaded = false
    },
    project_loaded: function (loaded) {
      // clear previous state
      helpers.clearArray(this.tabs)

      if (loaded) {
        console.log('Editor > project_loaded')
        // update object
        this.project = store.state.current_project

        let mainFile = this.project.files.filter(f => f.name === 'main.js')[0]
        console.log(mainFile)

        if (mainFile) {
          this.load_file(mainFile)
        }
        
        this.isError = false
        this.isProjectLoaded = true
      } else {
        // project couldnt be loaded
        this.isError = true
        console.log('error -----> ')
      }
    },
    load_file: function (file) {
      // select index of current file
      this.currentTab = this.tabs.findIndex(tab => tab.name === file.name && tab.path === file.path)

      // if not existing then push
      if (this.currentTab === -1) {
        this.tabs.push(file)
        this.currentTab = this.tabs.length - 1
      }
    },
    updateCode: function (code) {
      let f = this.tabs[this.currentTab]
      f.code = code
      f.modified = true
    },
    add_tab: function () {
      this.tabs.push({ name: 'qq', text: 'lala' })
    },
    select_tab: function (selectedTab) {
      this.currentTab = selectedTab
    },
    close_tab: function (index) {
      // remove tab
      this.tabs.splice(index, 1)
      this.select_tab(index - 1)
    },
    project_created: function (status, data) {
      if (status) {
        this.$router.push({
          name: 'editor.load',
          params: { type: data.type, folder: data.folder, project: data.name }
        })
      }
    },
    project_save: function () {
      store.emit('project_save', this.tabs)
    },
    project_saved: function () {
      for (var k in this.tabs) {
        this.tabs[k].modified = false
      }
    },
    try_again: function () {
      console.log('trying again')

      this.$router.go(this.$router.currentRoute)
    }
  }
}
</script>

<style lang="less">
@import (reference) '../../assets/css/variables.less';
@import (reference) '../../assets/css/hacks.less';

#myeditor {
  display: flex;
  flex-direction: column;
  height: 100%;

  #project-actions {
    display: flex;
  }

  .shortcut {
    // background: var(--color-accent);
  }
}

@keyframes fadein {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

#editor_container {
  position: relative;
  width: 100%;
  height: 100%;
  z-index: 1;
  transition: transform 300ms ease-in-out;
  display: flex;
  flex-direction: column;
  border-bottom: 0px;
  border-radius: 2px 2px 0 0;
  overflow: hidden;
  opacity: 0;

  &.project_loaded {
    animation: fadein 0.3s forwards;
    animation-delay: 0.3s;
  }

  &.slide {
    transform: translateY(420px);
  }

  #editor {
    position: relative;
    height: calc(~'100%');
  }

  #project_name {
    cursor: pointer;
    font-size: 0.7em;
    padding: 0px 10px;
    margin-right: 5px;
    min-width: 50px;
    text-align: left;
    display: flex;
    flex-flow: column;
    justify-content: center;

    .folder {
      padding-bottom: 4px;
    }

    .name::before {
      content: '└';
      padding-right: 2px;
    }

    .name {
      margin-left: 5px;
    }
  }

  .msg_error {
    position: absolute;
    z-index: 5;
  }
}

#uieditor-container {
  width: 100%;
  height: 100%;
}

#project-options {
  user-select: none;
  padding: 18px 0px 18px 0px;
  /* padding: 26px 0px 26px 0px; */
}

/* adjust to different sizes */
@media screen and (max-width: 600px) {
  #myeditor {
    padding: 0px;
  }
}
</style>
