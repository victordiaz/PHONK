<template>
  <div id = "file_manager" class = "proto_panel" v-bind:class = "{'expanded': expanded, 'collapsed': !expanded}">
      <div class = "wrapper">
      <div class = "actionbar">
        <h1 v-on:click = "expanded = !expanded">Files</h1>
        <input v-show = "false" class = "path" v-model = "current_folder" readonly />
        <ul>
          <li title = "Create a file or folder" class = "material-icons" v-on:click = "toggle_create_file_dialog" v-bind:class="{'enabled':showCreateDialog}">add</li>
          <li title = "Upload a file" class = "material-icons" for = "get_file" v-on:click = "show_upload_dialog">cloud_upload</li>
          <li title = "Edit files" class = "material-icons" v-on:click = "edit_files" v-bind:class="{'enabled':isEditingFiles}">edit</li>
          <li title = "Refresh files" class = "material-icons" v-on:click = "refresh_files">refresh</li>
        </ul>
        <div id = "upload_container" v-bind:class = "{'show' : isDnd, 'todrop': isReadyToDrop }">
          <p>Drop the files here</p>
          <input id = "upload" type = "file" @change = "onFileChange" multiple = "">
        </div>
      </div>

      <transition name = "banneranim">
        <div v-if = "isEditingFiles" class = "file_editing actionable">
          <div v-if = "actionable != 'paste'">
            <div v-if = "!isActionInProcess">
              <button v-on:click = 'rename_file' :class = "{ disabled: num_files_selected > 1 || num_files_selected === 0 }">rename</button>
              <button v-on:click = 'cut_files' :class = "{ disabled: num_files_selected === 0 || isRenaming }">cut</button>
              <button v-on:click = 'delete_files' :class = "{ disabled: num_files_selected === 0 || isRenaming }">delete</button>
            </div>

            <div v-else>
              <button v-on:click = 'rename_file_submit' v-if = "isRenaming"><i class = "material-icons">check</i></button>
              <button v-on:click = 'cancel_file_operation' v-if = "isRenaming"><i class = "material-icons">close</i></button>
              <button v-on:click = 'paste_files' v-if = "isCutting">Paste {{filesToPaste.length}} file(s)</button>
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
        <div v-if = "showCreateDialog" class = "new_file">
          <select v-model = "new_file.type">
            <option value = "file">New file</option>
            <option value = "folder">New folder</option>
          </select>
          <input id = "new_file" ref = "newfile" type = "text" placeholder="filename.js" v-model = "new_file.name" @keyup.enter = "create_file"/>
          <div class = "button_group">
            <button class = "left" v-on:click = "create_file"><i class = "material-icons">check</i></button>
            <button class = "right" v-on:click = "toggle_create_file_dialog"><i class = "material-icons">close</i></button>
          </div>
        </div>
      </transition>

      <div class = "content">
        <div class = "num_files_selected" v-if = "isEditingFiles && num_files_selected > 0">{{num_files_selected}}</div>
        <table>
          <thead>
            <tr>
              <th> type </type>
              <th> name </th>
              <th> size </th>
              <th class = "action"></th>
            </tr>
          </thead>
          <tbody>
            <tr id = "back" v-on:click = "change_dir('..')">
              <td><i class = "material-icons">folder</i></td>
              <td> .. </td>
              <td> </td>
            </tr>
            <tr id = "files" v-bind:class="{ 'selected': selected == index }" v-for = "(file, index) in files" v-on:click = "showcontent(index, $event)">
              <td v-if = "isEditingFiles && !isCutting"><input type="checkbox" v-model="file.selected" :class = "{ disabled: isRenaming}"></td>
              <td><i class = "material-icons">{{get_icon(file)}}</i></td>
              <td><span v-if = "!checkRenaming(isRenaming, file.name)">{{file.name}}</span> <input v-else = "checkRenaming(isRenaming, file.name)" v-model = "file.name" v-on:keyup.enter = "rename_file_submit"></input> </td>
              <td class = "file_size"> {{file.size}} </td>
            </tr>
          </tbody>
        </table>

      </div>
      <div id = "uploading" v-show = "showUploadingFiles">
        <h1> uploading... </h1>
        <ul>
          <li v-for = "u in uploadingFiles" v-show = "u.uploading">
            {{u.data.name}} <!-- {{u.uploading}} -->
            <div class = "progress pre"></div>
            <div class = "progress"></div>
          </li>
        </ul>
      </div>
    </div>
    <popup arrow = "right" :posx = "posx" :posy = "posy" v-if = "showpopover">
      <p v-html = "popup_content"></p>
      <audio-player v-show = "showAudioPlayer" v-bind:src = "url"></audio-player>
      <video-player v-show = "showVideoPlayer" src = "/static/cityfireflies.m4v"></video-player>

      <div class = "file_info">
        <input type = "text" v-model = "url" />
        <a v-bind:href = "url" download><i class = "material-icons">cloud_download</i></a>
      </div>
      <!--
      <video-player src = "https://www.youtube.com/embed/BC2dRkm8ATU"></video-player>
      <video-player src = "http://player.vimeo.com/video/25071870"></video-player>
    -->
    </popup>
  </div>
</template>

<script>
import store from '../Store'
import helpers from '../Helpers'
import Popup from './views/Popup'
import VideoPlayer from './views/VideoPlayer'
import AudioPlayer from './views/AudioPlayer'
import Vue from 'vue'

export default {
  name: 'FileManager',
  components: {
    Popup,
    VideoPlayer,
    AudioPlayer
  },
  data () {
    return {
      showpopover: false,
      showAudioPlayer: false,
      showVideoPlayer: false,
      qq: true,
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
        { type: 'folder-o', name: 'qq1.png', size: '25kb' },
        { type: 'folder-o', name: 'qq2.png', size: '25kb' },
        { type: 'folder-o', name: 'qq3.png', size: '25kb' },
        { type: 'folder-o', name: 'qq4.png', size: '25kb' },
        { type: 'folder-o', name: 'qq5.png', size: '25kb' },
        */
      ],
      uploadingFiles: [
      ],
      showUploadingFiles: false,
      isDnd: false,
      isReadyToDrop: false,
      dndState: 'none',
      posx: 0,
      posy: 0,
      input: null,
      showCreateDialog: false,
      new_file: {
        type: 'file',
        name: ''
      },
      expanded: true
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
    init_filemanager: function () {
      // console.log('ready')
      store.on('project_files_list', this.project_files_list)
      store.on('project_file_uploaded', this.project_file_uploaded)
      store.on('project_files_action_completed', this.project_files_action_completed)
      store.on('close_popup', this.close_popup)

      var that = this
      var firstTarget

      this.$el.ondrop = function (e) {
        // upload
        that.showUploadingFiles = true

        that.onFileChange(e)
        that.isDnd = false
        that.isReadyToDrop = false
        e.preventDefault()
      }

      document.addEventListener('dragenter', function (e) {
        firstTarget = e.target
        // console.log('dragenter ', e)
        that.isDnd = true
        // e.stopPropagation()
        e.preventDefault()
      })

      document.addEventListener('dragover', function (e) {
        e.target.effectAllowed = true
      })

      document.addEventListener('dragover', function (e) {
        // console.log('dragover')
        // console.log('dragover -->' + e.target.id)
        if (e.target.id === 'upload') {
          // console.log('yeah!')
          e.dataTransfer.dropEffect = 'copy'
          that.isReadyToDrop = true
          that.showUploadingFiles = true
        }
        // e.stopPropagation()
        e.preventDefault()
      }, false)

      document.addEventListener('dragleave', function (e) {
        if (firstTarget === e.target) {
          // console.log('dragleave')
          that.isDnd = false
          that.isReadyToDrop = false
          // e.stopPropagation()
          e.preventDefault()
        } else if (e.target.id === 'upload') {
          that.isReadyToDrop = false
        }
      })

      document.addEventListener('dragcancel', function (e) {
        if (firstTarget === e.target) {
          // console.log('dragcancel')
          that.isDnd = false
          that.isReadyToDrop = false
          e.stopPropagation()
          e.preventDefault()
        }
      })

      this.input = this.$el.querySelector('#upload')
    },
    playersOff: function () {
      this.showAudioPlayer = false
      this.showVideoPlayer = false
      this.popup_content = ''
    },
    showcontent: function (i, e) {
      if (this.isEditingFiles && !this.isCutting) return

      var selectedFile = this.files[i]
      this.url = store.getUrlForCurrentProject() + 'files/view/' + escape(selectedFile.name)
      this.posx = 300 + 'px'
      this.posy = 0 + 'px' // target.offsetTop + 22 + 'px'

      this.playersOff()

      // change directories
      if (selectedFile.isDir) {
        this.change_dir(selectedFile.path)
      // preview file
      } else if (selectedFile.name.toLowerCase().endsWith('.png') ||
            selectedFile.name.toLowerCase().endsWith('.jpg') ||
            selectedFile.name.toLowerCase().endsWith('.jpeg')) {
        console.log(e)
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
        console.log('opening file ' + selectedFile.name)
        store.load_file(selectedFile)
      }
    },
    clickedOutside: function () {
      console.log('click')
      this.showpopover = false
      this.showAudioPlayer = false
      this.showVideoPlayer = false
      this.popup_content = ''
    },
    project_files_list: function (filesList, b) {
      // console.log('---> 2')
      // console.log('files' + filesList + ' ' + b)
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
      console.log('-->', this.current_folder + ' ' + path)

      if (path === '..') {
        let splittedPath = this.current_folder.split('/')
        console.log(splittedPath)
        path = splittedPath.slice(0, splittedPath.length - 1).join('/')
        console.log(path)
        console.log('-------------__***SA *SA*SASA ')
      }

      console.log('changing dir ' + path)
      store.list_files_in_path(path)
    },
    show_upload_dialog: function (e) {
      console.log('show')
      this.input.click()
    },
    refresh_files: function (e) {
      store.list_files_in_path(store.state.current_project.current_folder)
    },
    edit_files: function (e) {
      this.showCreateDialog = false
      this.isEditingFiles = !this.isEditingFiles
      console.log('qq ' + this.isEditingFiles)
    },
    /* file upload */
    onFileChange: function (e) {
      console.log('onFileChange')

      this.showUploadingFiles = true
      var files = e.target.files || e.dataTransfer.files

      // clean uploading files
      // store.clearArray(this.uploadingFiles)

      // send
      for (var i = 0; i < files.length; i++) {
        console.log('uploading...', files[i].name + ' ' + files[i].size + ' ' + files[i].type)
        this.uploadingFiles.push({data: files[i], uploading: true})
        store.uploadFile(this.uploadingFiles[i], this.current_folder)
      }
      // if (!files.length) return
      // this.createImage(files[0])
    },
    project_file_uploaded: function (name) {
      console.log('------> uploaded ')

      var uploading = false

      for (var i = 0; i < this.uploadingFiles.length; i++) {
        if (this.uploadingFiles[i].data.name === name) {
          console.log(name + ' is uploaded')
          this.uploadingFiles[i].uploading = false
        }
        uploading = uploading || this.uploadingFiles[i].uploading
        this.uploadingFiles.splice(i, 1) // remove item
      }

      if (!uploading) {
        this.showUploadingFiles = false
        store.list_files_in_path(this.current_folder)
      }
    },
    toggle_create_file_dialog: function () {
      this.isEditingFiles = false
      this.showCreateDialog = !this.showCreateDialog
      this.new_file.name = ''
      this.$nextTick(() => this.$refs.newfile.focus())
    },
    create_file: function () {
      if (this.new_file.type !== null && this.new_file.name !== null) {
        console.log(this.new_file.type, this.new_file.name)
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

      console.log('------->', fileToRename)

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
    document.removeEventListener('dragover')
    document.removeEventListener('dragleave')
    document.removeEventListener('dragcancel')

    store.remove_listener('project_files_list')
    store.remove_listener('project_file_uploaded')
    store.remove_listener('project_files_action_completed')
    store.remove_listener('close_popup')
  }
}
</script>

<style lang='less'>
@import (reference) "../assets/css/variables.less";
@import (reference) "../assets/css/hacks.less";

#file_manager {
  z-index: 1;
  height: 20%;
  position: relative;

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
    border: 2px solid red;
    background: tomato;

    input {
      display: block;
    }
  }

  #upload_container {
    display: none;
    background: fadeout(@accentColor, 20%);
    position: absolute;
    top: 0px;
    right: 0;
    bottom: 0;
    left: 0;
    width: 100%;
    height: 100%;

    &.show {
      display: flex;
      align-items: center;
      font-size: 2em;
    }

    &.todrop {
      background: red;
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
    background: @mainColor;
    border-radius: 25px;
    width: 10px;
    height: 10px;
    font-size: 0.7em;
    position: absolute;
    right: 10px;
    color: @accentColor;
    font-weight: bold;
    text-align: center;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  table {
    table-layout: fixed;
    width: 100%;
    text-align: left;
    cursor: pointer;
    color: @primaryTextColor;

    thead {
      display: none;
      font-weight: 600;
      font-size: 0.8em;

      th {
        padding: 0px 5px 12px 5px;
      }

      th:first-child {
      }

      th:last-child {
      }

    }

    tbody {
      font-size: 0.8em;
      td {
        padding: 0.5em;
        // white-space: nowrap;
        // overflow: hidden;
        // text-overflow: ellipsis;
        // max-width: 15ch;
      }

      checkbox {
        border: 1px solid black;
        margin: 0px;
      }

      tr {
        display: flex;
        align-items: center;
        .material-icons {
          color: #b7b7b7;
        }

        &:hover {
          color: @accentColor;

          .material-icons {
            color: @accentColor;
          }

          .file_size {
            color: #ffffff88;
          }
          .action {
            // display: block;
          }
        }
      }

      tr:nth-child(odd) td {
      }
    }

    .file_size {
      color: #00000088;
    }

    .action {
      display: none;
      opacity: 1;
      position: sticky;
      right: -11px;
      background: @backgroundColor;
      color: white;
      padding-left: 15px;
      padding-right: 15px;

      &:hover {
        background: dark(@backgroundColor, 10%);
        color: #ff356b;
      }
    }
  }

  #uploading {
    position: absolute;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.8);
    top: 0;
    font-size: 0.8em;
    font-weight: 500;
    font-family: 'Open Sans';
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

    .progress {
      position: absolute;
      background: #ffeb00;
      height: 2px;
      width: 100%;
      border-radius: 2px;
      margin-top: 3px;
    }

    .pre {
      background: gray;
      width: 100%;
    }

  }

  .actionable {
    & > * {
      align-items: center;
      align-content: normal;
      display: inherit;
    }

    span {
      flex: 2;
      display: flex;
      align-items: center;
      padding: 8px;
      color: #fffc;
      font-weight: 400;
    }
  }

  .new_file, .file_editing {
    display: flex;
    align-items: center;
    align-content: normal;
    display: inherit;
    align-items: center;
    background: @accentColor_1;
    color : @secondaryTextColor;
    padding: 0px;
    min-height: 40px;
    max-height: 40px;

    & > * {
      font-weight: 600;
      font-size: 0.8rem;
      height: 100%;
      box-sizing: border-box;
      font-weight: 500;
      color: white;
    }

    input {
      background: @accentColor_1;
      padding: 10px;
      flex: 1;
      border: 0px solid transparent;
      min-width: 20px;

      &::placeholder {
        color: rgba(255, 255, 255, 0.4);
      }
    }

    select {
      text-transform: uppercase;
      background: transparent;
      border: 0px;
      padding: 10px;
      min-width: 10px;
      font-size: 0.7rem;
      appearance: none;
    	background-image: url('data:image/svg+xml;charset=US-ASCII,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22292.4%22%20height%3D%22292.4%22%3E%3Cpath%20fill%3D%22%23FFFFFF%22%20d%3D%22M287%2069.4a17.6%2017.6%200%200%200-13-5.4H18.4c-5%200-9.3%201.8-12.9%205.4A17.6%2017.6%200%200%200%200%2082.2c0%205%201.8%209.3%205.4%2012.9l128%20127.9c3.6%203.6%207.8%205.4%2012.8%205.4s9.2-1.8%2012.8-5.4L287%2095c3.5-3.5%205.4-7.8%205.4-12.8%200-5-1.9-9.2-5.5-12.8z%22%2F%3E%3C%2Fsvg%3E'),
	    linear-gradient(to bottom, transparent 0%, transparent 100%);
	    background-repeat: no-repeat, repeat;
	    background-position: right .7em top 50%, 0 0;
	    background-size: .65em auto, 100%;
    }

    select option {
      background: darken(@accentColor, 10%);
      color: white;
      padding: 15px;
    }

    select option:hover, select option:active, select option:active {
      background: yellow;
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
    background: rgba(0, 0, 0, 0.2);
    border-radius: 2px;
    padding: 1px 2px;

    input {
      background: rgba(0, 0, 0, 0);
      outline: none;
      border: 0px;
      border-right: 1px solid rgba(0, 0, 0, 0.1);;
      border-radius: 2px;
      color: black;
      font-size: 0.9em;
      -webkit-box-flex: 1;
      -ms-flex: 1;
      flex: 1;
      margin-right: 2px 5px;
      padding: 2px;
      font-family: @editorFont;
    }

    a {
      color: black;
      padding: 5px;

      &:hover {
        opacity: 0.7;
      }

      &:active {
        opacity: 0.4;
      }
    }
  }
}

</style>
