<template>
  <div id = "file_manager" class = "proto_panel" v-bind:class = "{'expanded': expanded, 'collapsed': !expanded}">
    <div class = "wrapper">
      <div class = "actionbar" :class="{ 'showIcons': isEditingFiles }">
        <h1 v-on:click = "expanded = !expanded">Files</h1>
        <input v-show = "false" class = "path" v-model = "current_folder" readonly />
        <ul>
          <li title = "Create a file or folder" class = "material-icons" v-on:click = "toggle_create_file_dialog" v-bind:class="{'toggled':showCreateDialog}">add</li>
          <li title = "Upload a file" class = "material-icons" for = "get_file" v-on:click = "show_upload_dialog">cloud_upload</li>
          <li title = "Edit files" class = "material-icons" v-on:click = "edit_files" v-bind:class="{'toggled':isEditingFiles}">edit</li>
          <li title = "Refresh files" class = "material-icons" v-on:click = "refresh_files">refresh</li>
        </ul>
        <transition name = "scaleanim">
          <div id = "upload_container" v-if = "dnd.showMode === 'drophere' || dnd.showMode === 'dropready'" v-bind:class = "{ 'todrop': dnd.showMode === 'dropready' }">
            <p>Drop the files here <!-- {{ dnd }} {{ uploadingFiles }} --></p>
          </div>
        </transition>
      </div>
      <input id = "upload" type = "file" @change = "onFileChange" multiple = "">

      <transition name = "banneranim">
        <div v-if = "isEditingFiles" class = "file_editing actionable">
          <div v-if = "actionable != 'paste'">
            <div v-if = "!isActionInProcess" class="file_editing_action">
              <span>Select a file</span>

              <button
                v-on:click = 'rename_file'
                class="clean"
                :class = "{ disabled: num_files_selected > 1 || num_files_selected === 0 }">
                  rename
                </button>
              <button
                v-on:click = 'cut_files'
                class="clean"
                :class = "{ disabled: num_files_selected === 0 || isRenaming }">
                  cut
                </button>
              <button
                v-on:click = 'delete_files'
                class="clean"
                :class = "{ disabled: num_files_selected === 0 || isRenaming }">
                  delete
                </button>
            </div>

            <div v-else>
              <button
                v-on:click = 'rename_file_submit'
                v-if = "isRenaming"
                class="clean">
                  <i class = "material-icons">check</i>
              </button>
              <button
                v-on:click = 'cancel_file_operation'
                v-if = "isRenaming"
                class="clean">
                  <i class = "material-icons">close</i>
                </button>
              <button
                v-on:click = 'paste_files'
                v-if = "isCutting"
                class="clean">
                  Paste {{filesToPaste.length}} file(s)
              </button>
            </div>
          </div>
          <div v-else>
            <button v-on:click = 'paste_files'>paste</button>
            <button v-on:click = 'paste_files_cancel'>cancel</button>
          </div>
          <span v-if = "false">Select the files</span>
        </div>
      </transition>

      <transition name = "banneranim">
        <div v-show = "showCreateDialog" class = "new_file actionable">
          <div>
            <select v-model = "new_file.type">
              <option value = "file">New file</option>
              <option value = "folder">New folder</option>
            </select>
            <input id = "new_file" ref = "newfile" type = "text" placeholder="filename.js" v-model = "new_file.name" @keyup.enter = "create_file"/>
            <div class = "button_group">
              <button class = "clean" v-on:click = "create_file"><i class = "material-icons">check</i></button>
              <button class = "clean" v-on:click = "toggle_create_file_dialog"><i class = "material-icons">close</i></button>
            </div>
          </div>
        </div>
      </transition>

      <div class = "content">
        <div class = "num_files_selected" v-if = "isEditingFiles">Selected {{num_files_selected}} file(s)</div>

        <div class = "file" v-if="(current_folder.trim() != '/') && !isEditingFiles" id = "back" v-on:click = "change_dir('..')">
          <div class="file-details">
            <i class = "material-icons">folder</i>
            <span class="file_name"> .. </span>
          </div>
        </div>

        <div
          class = "file"
          v-bind:class="{ 'selected': selected == index }"
          v-for = "(file, index) in files"
          :key="index">

          <input
            v-if = "isEditingFiles && !isCutting"
            type="checkbox"
            v-model="file.selected"
            :class = "{ disabled: isRenaming}"
          />

          <div class="file-details" v-on:click = "showContent(index, $event)">
            <i class = "material-icons">{{get_icon(file)}}</i>
            <span v-if = "!checkRenaming(isRenaming, file.name)" class="file_name">{{file.name}}</span>
            <input v-else-if = "checkRenaming(isRenaming, file.name)" v-model = "file.name" v-on:keyup.enter = "rename_file_submit" />    
            <span v-if="file.type==='file'" class = "file_size"> {{file.size / 1000}} kb</span>
          </div>
        
        </div>
      </div>

      <div id = "uploading" v-show = "dnd.showMode === 'uploading'">
        <h1> uploading... </h1>
        <ul>
          <li v-for = "u in uploadingFiles" v-show = "u.uploading" :key="u.data.name">
            {{u.data.name}}
            <div class="progress_container">
              <div class="progress"></div>
            </div>
          </li>
        </ul>
      </div>
    </div>

    <pop-over arrow = "right" :posx = "posx" :posy = "posy" v-if = "showpopover">
      <i class="material-icons close" v-on:click = "close">close</i>
      <p v-html = "popup_content"></p>
      <audio-player v-show = "showAudioPlayer" v-bind:src = "url"></audio-player>
      <video-player v-show = "showVideoPlayer" src = "/static/cityfireflies.m4v"></video-player>

      <div class = "file_info">
        <input type = "text" v-model = "url" />
        <a v-bind:href = "url" download><i class = "material-icons">cloud_download</i></a>
      </div>
    </pop-over>
  </div>
</template>

<script>
import store from '../../Store'
import helpers from '../../Helpers'
import PopOver from '../views/PopOver'
import VideoPlayer from '../views/VideoPlayer'
import AudioPlayer from '../views/AudioPlayer'
import Vue from 'vue'

export default {
  name: 'FileManagerPanel',
  components: {
    PopOver,
    VideoPlayer,
    AudioPlayer
  },
  data () {
    return {
      showpopover: false,
      showAudioPlayer: false,
      showVideoPlayer: false,
      selected: -1,
      popup_content: '',
      current_folder: '',
      project_name: '',
      actionable: 'single',
      isEditingFiles: false,
      isRenaming: false,
      fileToRename: '',
      isCutting: false,
      filesToPaste: [],
      isActionInProcess: false,
      is_moving_files: false,
      files: [
        /*
        { type: 'folder-o', name: 'qq1.png', size: '25kb' }
        */
      ],
      uploadingFiles: [],
      posx: 0,
      posy: 0,
      input: null,
      showCreateDialog: false,
      new_file: {
        type: 'file',
        name: ''
      },
      expanded: true,
      dnd: {
        showMode: 'none',
        counter: 0
      }
    }
  },
  computed: {
    num_files_selected: function () {
      let count = 0
      for (let i in this.files) {
        if (this.files[i].selected) count++
      }
      return count
    }
  },
  methods: {
    elDrop: function (e) {
      e.preventDefault()

      console.log('drop', e.dataTransfer.files[0])
      e.dataTransfer.dropEffect = 'copy'
      this.onFileChange(e)
    },
    elDragOver: function (e) {
      console.log('dragover el', e)

      if (e.target.id === 'upload') {
        e.effectAllowed = true
      }
    },
    docDragEnter: function (e) {
      console.log('dragenter')
      if (this.dnd.showMode === 'dropready') this.dnd.showMode = 'drophere'

      this.dnd.counter++
      this.dnd.showMode = 'drophere'

      e.stopPropagation()
      e.preventDefault()
    },
    docDragOver: function (e) {
      console.log('dragover', e.target.id)

      if (e.target.id === 'upload') {
        e.effectAllowed = true
        this.dnd.showMode = 'dropready'
      }
      e.stopPropagation()
      e.preventDefault()
    },
    docDragLeave: function (e) {
      this.dnd.counter--
      if (this.dnd.counter === 0) {
        this.dnd.showMode = 'none'
      }
      console.log('dragleave')
      e.preventDefault()
    },
    docDrop: function (e) {
      console.log('document drop', e, e.target.id === 'upload')

      if (!(e.target.id === 'upload')) {
        this.dnd.counter = 0
        this.dnd.showMode = 'none'
      }
      e.preventDefault()
    },
    init_filemanager: function () {
      store.on('project_files_list', this.project_files_list)
      store.on('project_file_uploaded', this.project_file_uploaded)
      store.on('project_files_action_completed', this.project_files_action_completed)
      store.on('close_popup', this.close_popup)

      this.$el.addEventListener('drop', this.elDrop, false)
      this.$el.addEventListener('dragover', this.elDragOver, false)

      document.addEventListener('dragenter', this.docDragEnter)
      document.addEventListener('dragover', this.docDragOver, false)
      document.addEventListener('dragleave', this.docDragLeave)
      document.addEventListener('drop', this.docDrop, false)

      this.input = this.$el.querySelector('#upload')
    },
    playersOff: function () {
      this.showAudioPlayer = false
      this.showVideoPlayer = false
      this.popup_content = ''
    },
    showContent: function (i) {
      if (this.isEditingFiles && !this.isCutting) return

      var selectedFile = this.files[i]
      this.url = store.getUrlForCurrentProject() + 'files/view/' + escape(selectedFile.name)
      this.posx = document.getElementById('file_manager').clientWidth + 30 + 'px'
      this.posy = 0 + 'px' // target.offsetTop + 22 + 'px'

      this.playersOff()

      // change directories
      if (selectedFile.isDir) {
        this.change_dir(selectedFile.path)
      // preview file
      } else if (selectedFile.name.toLowerCase().endsWith('.png') ||
            selectedFile.name.toLowerCase().endsWith('.jpg') ||
            selectedFile.name.toLowerCase().endsWith('.jpeg')) {
        this.selected = i
        this.popup_content = '<img src=' + this.url + '?' + helpers.generateUUID() + '/>'
        this.showpopover = !this.showpopover
      } else if (selectedFile.name.toLowerCase().endsWith('.mp3') ||
            selectedFile.name.toLowerCase().endsWith('.ogg') ||
            selectedFile.name.toLowerCase().endsWith('.wav')) {
        this.selected = i
        this.showAudioPlayer = true
        this.showpopover = !this.showpopover
      // try to open file
      } else if (selectedFile.name.toLowerCase().endsWith('.mp4') ||
          selectedFile.name.toLowerCase().endsWith('.m4v') ||
          selectedFile.name.toLowerCase().endsWith('.mpeg') ||
          selectedFile.name.toLowerCase().endsWith('.mpg') ||
          selectedFile.name.toLowerCase().endsWith('.avi') ||
          selectedFile.name.toLowerCase().endsWith('.ogv')) {
        this.selected = i
        this.showpopover = !this.showpopover
        this.showVideoPlayer = true
      } else { // if (selectedFile.name.endsWith('.js')) {
        store.load_file(selectedFile)
      }
    },
    close: function () {
      this.showpopover = false
    },
    clickedOutside: function () {
      console.log('click')
      this.showpopover = false
      this.showAudioPlayer = false
      this.showVideoPlayer = false
      this.popup_content = ''
    },
    project_files_list: function () {
      // update object
      this.current_folder = store.state.current_project.current_folder
      var files = store.state.current_project.files
      this.project_name = store.state.current_project.project.name
      store.clearArray(this.files)

      files.sort(function (a, b) {
        return (b.isDir - a.isDir) || (a.name.toString().localeCompare(b.name))
      })

      for (var i in files) {
        Vue.set(files[i], 'selected', false)
        Vue.set(files[i], 'formerName', files[i].name)
        Vue.set(files[i], 'formerPath', files[i].path)
        this.files.push(files[i])
      }
    },
    change_dir: function (path) {
      if (path === '..') {
        let splittedPath = this.current_folder.split('/')
        path = splittedPath.slice(0, splittedPath.length - 1).join('/')
      }

      console.log('changing dir ' + path)
      store.list_files_in_path(path)
    },
    show_upload_dialog: function () {
      this.input.click()
    },
    refresh_files: function () {
      store.list_files_in_path(store.state.current_project.current_folder)
    },
    edit_files: function () {
      this.showCreateDialog = false
      this.isEditingFiles = !this.isEditingFiles
    },
    /* file upload */
    onFileChange: function (e) {
      let files = e.target.files.length > 0 ? e.target.files : e.dataTransfer.files
      this.dnd.showMode = 'uploading'

      // send
      for (var i = 0; i < files.length; i++) {
        this.uploadingFiles.push({ data: files[i], uploading: true, name: files[i].name })
        store.uploadFile(this.uploadingFiles[i], this.current_folder)
      }
    },
    project_file_uploaded: function (name) {
      var uploading = false

      for (var i = 0; i < this.uploadingFiles.length; i++) {
        if (this.uploadingFiles[i].name === name) {
          console.log(name + ' is uploaded')
          this.uploadingFiles[i].uploading = false
        }
        uploading = uploading || this.uploadingFiles[i].uploading
        this.uploadingFiles.splice(i, 1) // remove item
      }

      if (!uploading) {
        this.dnd.showMode = 'none'
        store.list_files_in_path(this.current_folder)
      }
    },
    toggle_create_file_dialog: function () {
      console.log('----->')
      this.$nextTick(() => this.$refs.newfile.focus())
      this.isEditingFiles = false
      this.showCreateDialog = !this.showCreateDialog
      this.new_file.name = ''
    },
    create_file: function () {
      if (this.new_file.type !== null && this.new_file.name !== null) {
        store.create_file(this.new_file.type, this.new_file.name)
        this.toggle_create_file_dialog()
      }
    },
    rename_file: function () {
      this.isRenaming = true
      this.isActionInProcess = true
    },
    checkRenaming: function (isRenaming, fileName) {
      if (!isRenaming) return false

      // mark which file is selected to renaming
      let fileToRename = this.files.filter(function (el) {
        if (el.selected) return el
      })

      // return true to mark the checkbox
      if (fileName === fileToRename[0].name) return true
      else return false
    },
    rename_file_submit: function () {
      let fileToRename = this.files.filter(function (el) {
        if (el.selected) return el
      })

      let fileToRenameSplitted = fileToRename[0].formerPath.split('/')
      fileToRename[0].path = fileToRenameSplitted.slice(0, fileToRenameSplitted.length - 1).join('/') + '/' + fileToRename[0].name

      store.project_files_move(fileToRename)
      this.isRenaming = false
      this.isActionInProcess = false
    },
    cancel_file_operation: function () {
      for (let i in this.files) {
        this.files[i].selected = false
      }

      // this.isEditingFiles = false
      this.isRenaming = false
      this.isActionInProcess = false
    },
    delete_files: function () {
      let filesToDelete = this.files.filter(function (el) {
        if (el.selected) return el
      })
      // console.log(this.files, filesToDelete)

      store.project_files_delete(filesToDelete)
    },
    cut_files: function () {
      this.isCutting = true
      this.isActionInProcess = true

      for (let i in this.files) {
        if (this.files[i].selected) {
          this.filesToPaste.push(this.files[i])
        }
      }
    },
    paste_files: function () {
      console.log('paste_files ', this.filesToPaste, ' where ' + this.current_folder)

      for (let i in this.filesToPaste) {
        this.filesToPaste[i].path = this.current_folder + '/' + this.filesToPaste[i].name
        console.log('--> paste from', this.filesToPaste[i].formerPath + ' to ' + this.filesToPaste[i].path)
      }

      store.project_files_move(this.filesToPaste)

      this.cancel_file_operation()
    },
    project_files_action_completed: function () {
      this.isRenaming = false
      this.filesToPaste = []
      this.isCutting = false
      this.isActionInProcess = false
    },
    close_popup: function () {
      this.showpopover = false
    },
    get_icon: function (o) {
      if (o.type === 'file') {
        if (o.name.endsWith('.jpg') ||
            o.name.endsWith('.jpeg') ||
            o.name.endsWith('.png')) {
          return 'image'
        } else if (o.name.endsWith('.js')) {
          return 'code'
        }

        return 'insert_drive_file'
      } else if (o.type === 'folder') {
        return 'folder'
      }
    }
  },
  mounted () {
    this.$nextTick(function () {
      this.init_filemanager()
    })
  },
  created () {

  },
  destroyed () {
    this.$el.removeEventListener('dragover', this.elDragOver)
    this.$el.removeEventListener('dragover', this.elDrop)

    /*
    document.removeEventListener('dragenter', docDragEnter)
    document.removeEventListener('dragover', docDragOver)
    document.removeEventListener('dragleave', docDragLeave)
    document.removeEventListener('drop', docDrop)
    */

    store.removeListener('project_files_list', this.project_files_list)
    store.removeListener('project_file_uploaded', this.project_file_uploaded)
    store.removeListener('project_files_action_completed', this.project_files_action_completed)
    store.removeListener('close_popup', this.close_popup)
  }
}
</script>

<style lang='less'>
@import (reference) '../../assets/css/variables.less';
@import (reference) '../../assets/css/hacks.less';

#file_manager {
  z-index: 1;
  height: 20%;
  position: relative;

  .file {
    display: flex;
    gap: 12px;
    padding: 3px 0;

    .file-details {
      display: flex;
      gap: 12px;
      align-items: center;
      cursor: pointer;

      &:hover {
        color: var(--color-accent);

        .material-icons {
          color: var(--color-accent);
        }

        .file_size {
          opacity: 1;
        }
    }
    }

    .file_name {
      font-size: @font-size-s;
    }

    .file_size {
      .font-mono-400;
      color: var(--color-text-light-faded);
      font-size: @font-size-xs;
      opacity: 0;
    }

    .material-icons {
      color: var(--color-icon);
    }
  }

  .close {
    position: absolute;
    right: 15px;
    top: 15px;
    font-size: 0.9em;
    cursor: pointer;

    &:hover {
    }

    &:active {
    }
  }

  .actionbar {
    input {
      font-size: 1.2em;
      height: 12px;
      margin: 5px;
      border-radius: 15px;
    }
  }
  .content {
    padding: 10px;
    overflow-y: auto;
    overflow: auto;
    .scrollbar;
  }

  .path {
    background: rgba(0, 0, 0, 0);
    outline: none;
    border: 0px;
    border-radius: 2px;
    flex: 8;
    margin: 4px 0px;
    padding: 6px 8px;
    min-width: 20px;
  }

  &.hovered {
    input {
      display: block;
    }
  }

  #upload {
    display: none;
  }

  #upload_container {
    display: flex;
    border: 10px solid var(--color-transparent);
    position: absolute;
    top: 0px;
    right: 0;
    bottom: 0;
    left: 0;
    width: 100%;
    height: 100%;
    align-items: center;
    font-size: 2em;

    p {
      text-shadow: 0 0 2px #0000003d;
      text-transform: capitalize;
      font-size: 1.5rem;
    }

    &.todrop {
      border: 10px solid var(--color-text-light);
    }

    p {
      text-align: center;
      width: 100%;
    }

    input {
      cursor: pointer;
      position: absolute;
      top: 0px;
      right: 0;
      bottom: 0;
      left: 0;
      width: 100%;
      height: 100%;
      opacity: 0;
    }
  }

  .num_files_selected {
    font-size: @font-size-xs;
    color: var(--color-text-light-faded);
    padding: 10px 0 3px 1px;
  }

  #uploading {
    position: absolute;
    width: 100%;
    height: 100%;
    top: 0;
    font-size: 0.8em;
    box-sizing: content-box;
    vertical-align: middle;
    display: flex;
    align-items: center;

    h1 {
      position: absolute;
      top: 0;
      right: 0;
      padding: 8px;
    }

    ul {
      list-style: none;
      width: 100%;
      padding: 8px 22px;
      li {
        position: relative;
        width: 100%;
        padding: 0.7em 0px;
      }
    }

    .pre {
      width: 100%;
    }
  }

  .actionable {
    margin-top: -8px;

    > div {
      background: var(--color-main-lighter);
      width: 100%;
      border-radius: 3px;
    }

    & > * {
      align-items: center;
      align-content: normal;
      display: inherit;
    }

    span {
      color: var(--color-text-light);
      flex: 2;
      display: flex;
      align-items: center;
      padding: 8px;
    }
    
    button {
      padding: 8px 8px;
      margin-left: 3px;
      font-size: 12px;

      &:hover {
      }
    }

  }

  .new_file,
  .file_editing {
    display: flex;
    align-items: center;
    align-content: normal;
    display: inherit;
    align-items: center;
    color: var(--color-text-dark);
    padding: 0 12px;
    min-height: 40px;
    max-height: 40px;

    & > * {
      font-size: 0.8rem;
      height: 100%;
      box-sizing: border-box;
    }

    input {
      background: var(--color-transparent);
      padding: 0px;
      flex: 1;
      outline: none;
      border: 0px solid transparent;
      min-width: 20px;
      color: var(--color-text-light);
      border-bottom: 1px solid var(--color-text-light);

      &::placeholder {
      }
    }

    .file_editing_action {
      display: flex;
      justify-content: space-between;
      width: 100%;
    }

    select {
      // text-transform: uppercase;
      color: var(--color-text-light);
      background: transparent;
      border: 1px solid var(--color-transparent);
      padding: 10px;
      font-size: 0.8rem;
      appearance: none;
      background-image: url('data:image/svg+xml;charset=US-ASCII,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22292.4%22%20height%3D%22292.4%22%3E%3Cpath%20fill%3D%22%23FFFFFF%22%20d%3D%22M287%2069.4a17.6%2017.6%200%200%200-13-5.4H18.4c-5%200-9.3%201.8-12.9%205.4A17.6%2017.6%200%200%200%200%2082.2c0%205%201.8%209.3%205.4%2012.9l128%20127.9c3.6%203.6%207.8%205.4%2012.8%205.4s9.2-1.8%2012.8-5.4L287%2095c3.5-3.5%205.4-7.8%205.4-12.8%200-5-1.9-9.2-5.5-12.8z%22%2F%3E%3C%2Fsvg%3E'),
        linear-gradient(to bottom, transparent 0%, transparent 100%);
      background-repeat: no-repeat, repeat;
      background-position: right 0.7em top 50%, 0 0;
      background-size: 0.65em auto, 100%;

      border-radius: 3px;
      padding: 3px 5px;
      margin: 0px 5px;

      &:hover {
        background-color: var(--color-main);
        border-color: var(--color-lines);
      }
    }

    select option {
      padding: 15px;
    }

    select option:hover,
    select option:active,
    select option:active {
      background: var(--color-accent);
    }

    .button_group {
      padding: 0px;
      margin: 0px;
      display: flex;

      button {
        min-width: 28px;
        font-size: 0.8rem;
        margin: 0px;
        padding: 5px;
        border-radius: 0px;
        height: 100%;

        i {
          padding: 7px 3px;
          font-size: 0.9rem;
        }
      }
    }
  }

  .file_info {
    display: flex;
    flex-direction: row;
    margin-top: 5px;
    border-radius: 2px;
    padding: 1px 2px;
    align-items: center;

    input {
      outline: none;
      border: 0px;
      border-radius: 2px;
      font-size: 0.8rem;
      -webkit-box-flex: 1;
      -ms-flex: 1;
      flex: 1;
      margin-right: 2px 5px;
      padding: 2px;
      .font-mono-400;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      padding: 5px 12px;
    }

    a {
      padding: 5px;
      opacity: 0.7;

      i {
        font-size: 1.1rem;
      }

      &:hover {
        opacity: 1;
      }

      &:active {
        opacity: 0.7;
      }
    }
  }
}

.progress_container {
  width: 250px;
  height: 2px;
  overflow: hidden;
  background-color: var(--color-accent);
  margin-top: 5px;

  .progress {
    margin-top: 0;
    animation-name: loading_animation;
    animation-duration: 1.5s;
    animation-iteration-count: infinite;
    animation-timing-function: linear;
    width: 250px;
    height: 19px;
  }
}

@keyframes loading_animation {
  0% {
    margin-left: -250px;
  }

  100% {
    margin-left: 250px;
  }
}
</style>
