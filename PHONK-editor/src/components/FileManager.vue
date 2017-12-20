<template>
  <div id = "file_manager" class = "proto_panel" v-bind:class = "{'expanded': expanded, 'collapsed': !expanded}">
      <div class = "wrapper">
      <div class = "actionbar">
        <h1 v-on:click = "expanded = !expanded">Files</h1>
        <input v-show = "false" class = "path" v-model = "current_folder" readonly />
        <ul>
          <!-- <li class="fa fa-folder" for = "get_file"></li> -->
          <li title = "Create a file or folder" class = "fa fa-plus" v-on:click = "show_create_file_dialog"  v-bind:class="{'enabled':showCreateDialog}"></li>
          <li title = "Upload a file" class = "fa fa-upload" for = "get_file" v-on:click = "show_upload_dialog"></li>
          <li title = "Refresh files" class = "fa fa-refresh" v-on:click = "refresh_files"></li>
        </ul>
        <div id = "upload_container" v-bind:class = "{'show' : isDnd, 'todrop': isReadyToDrop }">
          <p>Drop the files here</p>
          <input id = "upload" type = "file" @change = "onFileChange" multiple = "">
        </div>
      </div>

      <transition name = "banneranim">
        <div v-show = "showCreateDialog" class = "new_file">
          <select v-model = "new_file.type">
            <option value = "file">New file</option>
            <option value = "folder">New folder</option>
          </select>
          <input id = "new_file" type = "text" placeholder="filename.js" v-model = "new_file.name"/>
          <div class = "button_group">
            <button class = "left" v-on:click = "create_file"><i class = "fa fa-check"></i></button>
            <button class = "right" v-on:click = "show_create_file_dialog"><i class = "fa fa-times"></i></button>
          </div>
        </div>
      </transition>

      <div class = "content">
        <table>
          <thead>
            <tr>
              <th> type </type>
              <th> name </th>
              <th> size </th>
              <th class = "action"></th>
            </tr>
          </thead>
          <tbody>
            <tr id = "back" v-on:click = "change_dir('..')">
              <td><i class = "fa fa-folder-o"></i></td>
              <td> .. </td>
              <td> </td>
              <td> </td>
            </tr>
            <tr id = "files" v-bind:class="{ 'selected': selected == index }" v-for = "(file, index) in files" v-on:click = "showcontent(index, $event)">
              <td><i class = "fa" v-bind:class = "get_icon(file)"></i></td>
              <td> {{file.name}} </td>
              <td> {{file.size}} </td>
              <!-- <td class = "action"><i class = "fa fa-ellipsis-v"></i></td> -->
            </tr>
          </tbody>
        </table>

      </div>
      <div id = "uploading" v-show = "showUploadingFiles">
        <h1> uploading... </h1>
        <ul>
          <li v-for = "u in uploadingFiles" v-show = "u.uploading">
            {{u.data.name}} {{u.uploading}}
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
        <a v-bind:href = "url" download><i class = "fa fa-download"></i></a>
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

  },
  methods: {
    init_filemanager: function () {
      // console.log('ready')
      store.on('project_files', this.list_files)
      store.on('file_uploaded', this.file_uploaded)

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
      console.log('showing content ', e)
      var selectedFile = this.files[i]
      this.url = store.get_url_for_current_project() + 'files/view/' + selectedFile.name
      this.posx = 300 + 'px'
      console.log('target', e.target.offsetLeft)
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
    list_files: function () {
      // update object
      this.current_folder = store.state.current_project.current_folder
      var files = store.state.current_project.files
      this.project_name = store.state.current_project.project.name
      store.clearArray(this.files)

      files.sort(function (a, b) {
        return (b.isDir - a.isDir) || (a.name.toString().localeCompare(b.name))
      })

      for (var i in files) {
        this.files.push(files[i])
      }
    },
    change_dir: function (path) {
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
    /* file upload */
    onFileChange: function (e) {
      console.log('onFileChange')

      this.showUploadingFiles = true
      var files = e.target.files || e.dataTransfer.files

      // clean uploading files
      // store.clearArray(this.uploadingFiles)

      // send
      for (var i = 0; i < files.length; i++) {
        // console.log(files[i].name + ' ' + files[i].size + ' ' + files[i].type)
        this.uploadingFiles.push({data: files[i], uploading: true})
        store.upload_file(this.uploadingFiles[i])
      }
      // if (!files.length) return
      // this.createImage(files[0])
    },
    file_uploaded: function (name) {
      var uploading = false

      for (var i = 0; i < this.uploadingFiles.length; i++) {
        if (this.uploadingFiles[i].data.name === name) {
          // console.log(name + ' is uploaded')
          // console.log(this.uploadingFiles)
          // console.log(typeof (this.uploadingFiles))
          this.uploadingFiles[i].uploading = false
        }
        uploading = uploading || this.uploadingFiles[i].uploading
        // console.log(this.uploadingFiles[i].uploading)
        this.uploadingFiles.splice(i, 1) // remove item
      }

      // console.log(this.uploadingFiles)
      if (!uploading) {
        this.showUploadingFiles = false
        store.list_files_in_path('')
      }
    },
    show_create_file_dialog: function () {
      this.showCreateDialog = !this.showCreateDialog
    },
    create_file: function () {
      if (this.new_file.type !== null && this.new_file.name !== null) {
        console.log(this.new_file.type, this.new_file.name)
        store.create_file(this.new_file.type, this.new_file.name)
        this.show_create_file_dialog()
      }
    },
    get_icon: function (o) {
      return 'fa-' + o.type + '-o'
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
  }
}
</script>

<style lang='less'>
@import "../assets/css/variables.less";


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

  table {
    width: 100%;
    text-align: left;
    cursor: pointer;
    color: #444;

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
        padding:0.5em;
      }

      tr {
        &:hover {
          background: @accentColor;
          color: white;
        }
      }

      tr:nth-child(odd) td {
      }
    }

    .action {
      opacity: 0.2;

      &:hover {
        opacity: 1;
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

  .new_file {
    display: flex;
    align-items: center;
    background: white;
    color : @accentColor;
    padding: 0px;
    height: 92px;

    & > * {
      font-family: 'Open Sans Condensed';
      font-weight: 100;
      font-size: 0.8em;
      height: 100%;
      box-sizing: border-box;
    }

    input {
      padding: 10px;
      flex: 1;
      border: 0px solid transparent;
      border-bottom: 1px solid @accentColor;
      min-width: 20px;
    }

    select {
      background: transparent;
      border: 0px;
      padding: 10px;
      min-width: 20px;
    }

    .button_group {
      padding: 0px;
      margin: 0px;
      display: flex;

      button {
        min-width: 28px;
        margin: 0px;
        padding: 5px;
        border-radius: 0px;
        height: 100%;
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
      font-family: 'Source Code Pro';
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
