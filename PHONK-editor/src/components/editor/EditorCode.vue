<template>
  <div id="editor"></div>
</template>

<script>
import * as ace from 'ace-builds/src-noconflict/ace'
import 'ace-builds/src-noconflict/theme-monokai'
import 'ace-builds/src-noconflict/mode-javascript'
import 'ace-builds/src-noconflict/ext-language_tools'
// import 'ace-builds/src-noconflict/mode-javascript'//Language mode set by default

import store from '../../Store'

export default {
  name: 'EditorCode',
  components: {
  },
  props: ['tabs', 'currentTab'],
  watch: {
    tabs: function (newTabs) {
      let tabsArray = newTabs.map(tab => tab.path)
      let sessionsArray = this.sessions.map(session => session.path)

      // check if tab was closed
      if (tabsArray.length < sessionsArray.length) {
        let difference = tabsArray
                 .filter(x => !sessionsArray.includes(x))
                 .concat(sessionsArray.filter(x => !tabsArray.includes(x)))[0];
                 
        // remove session
        let indexSession = this.sessions.findIndex(session => session.path === difference)
        this.sessions.splice(indexSession, -1)
      }
    },
    currentTab: function (val) {
      // console.log('(EditorCode) currentTab', val)
      this.loadCode(val)
    }
  },
  data () {
    return {
      sharedState: store.state,
      files: null,
      sessions: [],
      project: '',
      d: null,
      editor: '',
    }
  },
  mounted () {
    this.$nextTick(() => {
      this.init_editor()
    })
  },
  created () {
    store.on('font_changed', this.changeFontSize)
    store.on('live_execute', this.live_execute)
    store.on('project_loaded', this.project_loaded)
  },
  destroyed () {
    store.removeListener('font_changed', this.changeFontSize)
    store.removeListener('live_execute', this.live_execute)
    store.removeListener('project_loaded', this.project_loaded)

    this.editor.remove()
  },
  methods: {
    changeFontSize: function () {
      console.log('changing')
      this.editor.setFontSize(
        this.sharedState.preferences['editor']['text size']
      )
    },
    init_editor: function () {
      var ace_ = ace
      this.editor = ace_.edit('editor')
      this.Range = ace_.require('ace/range').Range
      ace_.require('ace/lib/event')
      ace_.require('ace/config')
      ace_.require('ace/edit_session')
      ace_.require('ace/undomanager')
      ace_.require('ace/marker')
      ace_.require('ace/range')
      ace_.require('ace/ext/language_tools')
      ace_.require('ace/theme/monokai')

      var renderer = this.editor.renderer

      this.editor.setTheme('ace/theme/monokai')
      this.editor.setOptions({
        fontSize: this.sharedState.preferences['editor']['text size'],
        enableBasicAutocompletion: true,
        enableLiveAutocompletion: false
      })
      // this.editor.setPrintMarginColumn(true)
      this.editor.setShowPrintMargin(false)
      renderer.setPadding(8)
      // this.editor.session.setUseWorker(false)
      // this.editor.getSession().setUseSoftTabs(false)

      if (this.editor.session.$worker) {
        this.editor.session.$worker.send('changeOptions', [{ asi: true }])
      }
    },
    live_execute: function () {
      var range = this.editor.getSelection().getRange()
      var selectedText = this.sessions[this.currentTab].getTextRange(range)

      var liveExec = {}

      // get the code selected or the whole row
      if (selectedText.length > 0) {
        liveExec.text = selectedText
        liveExec.range = range
      } else {
        var cursorPosition = this.editor.getCursorPosition()
        var numLine = cursorPosition['row']
        liveExec.numLine = numLine
        liveExec.text = this.sessions[this.currentTab].getDocument().$lines[liveExec.numLine]
        liveExec.range = new this.Range(
          liveExec.numLine,
          0,
          liveExec.numLine,
          liveExec.text.length
        )
      }

      // highlight text
      var marker = this.sessions[this.currentTab].addMarker(
        liveExec.range,
        'execute_code_highlight',
        'line',
        true
      )
      setTimeout(() => {
        console.log(marker)
        this.sessions[this.currentTab].removeMarker(marker)
      }, 500)

      // console.log(liveExec)

      store.execute_code(liveExec.text)
    },
    createSession: function (f) {
      var session = ace.createEditSession(f.code, 'ace/mode/javascript')

      session.setMode('ace/mode/javascript')
      session.setUseSoftTabs(true)
      session.setTabSize(2)

      return session
    },
    project_loaded: function (loaded) {
      if (loaded) {
        store.clearArray(this.sessions)
        this.files = store.state.current_project.files
      }
    },
    loadCode: function (tabPos) {
      // get session if exist in currentTab (tabs[tabPos])
      let currentPath = this.tabs[tabPos].path
      
      let session = this.sessions.filter(session => session.path === currentPath)[0]
     
      // Create a session if file is not yet opened
      if (!session) {
        let f = this.files.filter(f => f.path == currentPath)[0]
        // console.log('(EditorCode) loadCode', currentPath, f.path)

        session = this.createSession(f)
        session.setValue(f.code)
        session.on('change', () => {
          // update code
          this.$parent.updateCode(session.getValue())
        })
        this.sessions.push({ 'session': session, 'path': f.path })
      } else {
        session = session.session
      }
       
      // set current session
      this.editor.setSession(session)
      this.editor.focus()
    }
  }
}
</script>

<style lang="less">
@import (reference) '../../assets/css/variables.less';
@import (reference) '../../assets/css/hacks.less';

/*
* TODO move it to a theme
*/
.ace-monokai {
  // .font-mono-400;
  background-color: transparent !important;
}

// KEEP this color
.ace-monokai .ace_string {
  color: #fde079;
}

.ace_content {
  background: var(--color-main);
}

.ace_dark.ace_editor.ace_autocomplete .ace_marker-layer .ace_active-line {
  background: var(--color-accent)_1;
}

// KEEP this color
.ace_gutter {
  background: transparent !important;
  color: rgba(255, 255, 255, 0.2) !important;
}

.ace_editor {
  // .font-mono-400;
  // font-size: 1.1rem !important;
  line-height: 1.3em !important;
}

.ace_dark .ace_gutter-cell {

  &.ace_info {
    background-image: none !important;
  }

  &.ace_error {
    // display: none;
  }
}

// KEEP this color
.ace-tm .ace_marker-layer .run_code {
  background-color: rgba(0, 255, 0, 0.38);
  position: absolute;
  width: 100% !important;
  left: 0 !important;
  z-index: 25 !important;
}

.ace_editor.ace_autocomplete {
 // border: 0px !important;
 // border-radius: 2px 0px 0px 2px;
}

.ace_editor.ace_autocomplete .ace_marker-layer .ace_active-line {
  background-color: var(--color-accent);
}

.ace_scrollbar {
  .scrollbar;
}

.execute_code_highlight {
  background: yellow;
  position: absolute;
  width: calc(~'100% - 5px') !important;
  border: 1px solid var(--color-accent);
  left: 0 !important;
}

/* adjust to different sizes */
@media screen and (max-width: 600px) {
  #myeditor {
    padding: 0px;
  }
}
</style>
