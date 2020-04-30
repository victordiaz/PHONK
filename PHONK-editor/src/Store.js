import Vue from 'vue'
import { EventEmitter } from 'events'
const store = new EventEmitter()
export default store

// var TAG = 'store'
var DEBUG = false

// constants
var WEBAPP_PORT = 8585
var WS_PORT = 8587

/*
 * Global state variables
 */
var state = {
  DEBUG,
  projects: {},
  examples: [],
  current_project: {
    project: {
      folder: '',
      name: ''
    }
  },
  device_properties: {
    connected: false,
    info: {
      network: { 'ip': 'none' },
      device: { 'model name': 'none' },
      script: { 'running script': 'none' },
      other: { 'debugging': true },
      screen: { orientation: 'portrait' }
    }
  },
  show_load_project: false,
  show_dashboard: false,
  show_device_info: false,
  show_preferences: false,
  preferences: {
    'editor': {
      'text size': 20,
      /*
      'light theme': true,
      'autosave': true,
      */
      'show tab bar': true
    },
    'other': {
      'show sidebar': true,
      'show advanced api': true,
      'show documentation status': true,
      'show TODO in documentation': false
    }
  },
  webide: {
    'editor_width': '300px',
    'docs_height': '200px',
    'files_height': '100px'
  }
}

var vm = new Vue({
  data: {
    state: state
  }
})

store.state = state
store.vm = vm

/*
 * List all projects
 */
store.project_list_all = function () {
  // console.log('project_list_all(query)')
  var query = {}

  Vue.axios.get(getUrlWebapp('/api/project/list/'), query).then(function (response) {
    // console.log('project_list_got', response.data)
    // store.state.projects = response.data
    Vue.set(store.state, 'projects', response.data)
    // store.clearArray(store.state.projects)
    // store.state.projects.push(...response.data)

    // store.state.projects = Object.assign({}, response.data)
    store.emit('project_listed_all')
  }, function (response) {
    // console.log(TAG + ': project_list_all(status) > ' + response.status)
  })
}

/*
 * Load a project
 */
store.project_load = function (uri) {
  var query = {}

  Vue.axios.get(getUrlWebapp('/api/project' + uri + '/load'), query).then(function (response) {
    // console.log(TAG + ': project_load(status) > ' + response.status)
    store.state.current_project = response.data
    store.emit('project_loaded', true)
    store.emit('project_files_list')
    store.load_project_preferences()
    // store.list_files_in_path('')
  }, function (response) {
    store.emit('project_loaded', false)
    // console.log(TAG + ': project_load(status) > ' + response.status)
  })
}

store.load_project_preferences = function () {
  var query = {}
  Vue.axios.get(this.getUrlForCurrentProject() + 'files/load/app.conf', query).then(function (response) {
    if (response.data.files[0].code) {
      console.log(response)
      var conf = JSON.parse(response.data.files[0].code)

      if (conf) store.state.current_project.conf = conf
    }
  }, function (response) {
    // console.log(TAG + ': project_save(status) > ' + response.status)
  })
}

store.list_files_in_path = function (p) {
  var query = {} // path: p}

  // console.log('listing files in path ' + toPath)
  Vue.axios.get(getUrlWebapp('/api/project' + this.get_current_project() + '/files/list/' + p), query).then(function (response) {
    console.log('list_files_in_path(status) > ' + response.status)

    store.state.current_project.current_folder = '/' + p
    store.state.current_project.files = response.data
    console.log(response.data)

    store.emit('project_files_list', true)
  }, function (response) {
    store.emit('project_files_list', false)

    // console.log('list_files_in_path(status) > ' + response.status)
  })
}

store.project_files_move = function (files) {
  console.log('files move')
  var query = {}
  query.files = files
  console.log(query)

  Vue.axios.post(getUrlWebapp('/api/project' + this.get_current_project() + '/files/move/'), query).then(function (response) {
    console.log('rename_files_in_path(status) > ' + response.status)

    // maybe here refresh again the files
    store.list_files_in_path(store.state.current_project.current_folder)
    store.emit('project_files_action_completed', true)
  }, function (response) {
    store.emit('project_files_action_completed', false)
    // console.log('list_files_in_path(status) > ' + response.status)
  })
}

store.project_files_delete = function (files) {
  console.log('files delete')
  var query = {}
  query.files = files

  Vue.axios.post(getUrlWebapp('/api/project' + this.get_current_project() + '/files/delete/'), query).then(function (response) {
    console.log('delete_files_in_path(status) > ' + response.status)

    // maybe here refresh again the files
    store.list_files_in_path(store.state.current_project.current_folder)
    store.emit('project_files_action_completed', true)
  }, function (response) {
    store.emit('project_files_action_completed', false)
    // console.log('list_files_in_path(status) > ' + response.status)
  })
}

/*
 * Load file
 */
store.load_file = function (file) {
  var query = {}
  // console.log('load_file', file)
  Vue.axios.get(this.getUrlForCurrentProject() + 'files/load/' + file.path, query).then(
    function (response) {
      // console.log(response)
      file.code = response.data.files[0].code

      // console.log('load_file(status) > ' + response.status, file.code)
      store.emit('file_loaded', file)
    }, function (response) {
      // console.log(TAG + ': project_save(status) > ' + response.status)
    })
}

/*
 * Create a file
 */
store.create_file = function (filetype, filename) {
  // console.log('create file ' + filetype + ' ' + filename)
  var query = {}
  query.files = [{ name: filename, path: store.state.current_project.current_folder, type: filetype }]

  Vue.axios.post(getUrlWebapp('/api/project' + this.get_current_project() + '/files/create'), query).then(function (response) {
    // console.log('create_file(status) OK > ' + response.status)
    store.emit('project_files_action_completed', true)
    store.list_files_in_path(store.state.current_project.current_folder)
  }, function (response) {
    // console.log('create_file(status) NOP > ' + response.status)
    store.emit('project_files_action_completed', false)
  })
}

/*
 * Create a project
 */
store.project_create = function (projectName) {
  // console.log('project create')

  var query = {}

  // vm.$log()

  Vue.axios.get(getUrlWebapp('/api/project' + this.get_userproject_url(projectName) + '/create'), query).then(function (response) {
    var data = { 'type': store.userproject.type, 'folder': store.userproject.folder, 'name': projectName }
    store.emit('project_created', true, data)
    store.project_list_all()
  }, function (response) {
    store.emit('project_created', false)
    console.log('project_create(status) > ' + response.status)
  })
}

/*
 * Save a project
 */
store.project_save = function (files) {
  // console.log('project saving')

  var query = {}
  query.project = Object.assign({}, store.state.current_project.project)
  query.project.files = null
  query.files = []
  query.files = Object.assign([], files)

  Vue.axios.post(getUrlWebapp('/api/project' + this.get_current_project() + '/save'), query).then(function (response) {
    // console.log('project_save(status) OK > ' + response.status)
    store.emit('project_saved')
    store.list_files_in_path(store.state.current_project.current_folder)

    if (!store.state.current_project.conf) return

    if (store.state.current_project.conf.execute_on_save) {
      // console.log(store.state.current_project.conf.execute_on_save)
      store.execute_code(store.state.current_project.conf.execute_on_save)
    }
  }, function (response) {
    console.log('project_save(status) NOP > ' + response.status)
    if (!response.status) {
      console.log('qq')
      store.emit('show_info', { icon: 'error', text: 'Cannot save! Check the connection' })
    }
  })
}

/*
 * Run a loaded project
 */
store.project_action = function (action) {
  var query = {}

  Vue.axios.get(getUrlWebapp('/api/project' + this.get_current_project() + action), query).then(function (response) {
    // console.log(response.status)
  }, function (response) {
    // console.log(response.status)
  })
}

/*
 * Run a project
 */
store.project_run = function (project) {
  var query = {}

  Vue.axios.get(getUrlWebapp('/api/project/' + project.gparent + '/' + project.parent + '/' + project.name + '/run'), query).then(function (response) {
    // console.log(response.status)
  }, function (response) {
    // console.log(response.status)
  })
}

/*
 * Project stop all and run
 */
store.project_stop_all_and_run = function (project) {
  var query = {}

  Vue.axios.get(getUrlWebapp('/api/project/' + project.gparent + '/' + project.parent + '/' + project.name + '/stop_all_and_run'), query).then(function (response) {
    // console.log(response.status)
  }, function (response) {
    // console.log(response.status)
  })
}

/*
 * Execute a code line
 */
store.execute_code = function (code) {
  // console.log('execute_code ' + code)
  var query = { code: code }

  Vue.axios.post(getUrlWebapp('/api/project/execute_code'), query).then(function (response) {
    // console.log(response.status)
  }, function (response) {
    // console.log(response.status)
  })
}

store.uploadFile = function (file, folder) {
  var formData = new FormData()
  // formData.append('_token', this.token) // just the csrf token
  formData.append('name', file.data.name)
  formData.append('type', file.data.type)
  formData.append('file', file.data)
  formData.append('toFolder', folder)

  let config = {
    headers: { 'Content-Type': 'multipart/form-data;charset=UTF-8' }
  }

  console.log('uploadFile', file, formData)
  Vue.axios.post(getUrlWebapp('/api/project' + this.get_current_project() + '/files/upload/'), formData, config).then(function (response) {
    console.log('File upload success', response)
    store.emit('project_file_uploaded', response.data)
  }, function (response) {
    console.log('File upload error...', response)
  })
}

/*
xhr: {
  onprogress: function (e) {
    // console.log(e)
    // console.log('uploading')

    if (e.lengthComputable) {
      // var progress = (e.loaded / e.total) * 100
      // console.log('p1 ' + progress)
    }
  },
  onreadystatechange: function (e) {
    // console.log(e + this.readyState)
    if (this.readyState === 4) {
      // console.log(e)
    }
  }
}
*/

/*
 * List views
 */
store.views_list_types = function (action) {
  var query = {}
  Vue.axios.get(getUrlWebapp('/api/project/views_list_types'), query).then(function (response) {
    // console.log(response.status)
    // console.log(response.data)
    store.emit('views_list_types', response.data)
  }, function (response) {
    // console.log(response.status)
  })
}

/*
 * Add views all
 */
store.views_get_all = function (action) {
  var query = {}
  Vue.axios.get(getUrlWebapp('/api/project/views_get_all'), query).then(function (response) {
    // console.log(response.status)
    // console.log(response.data)
    store.emit('views_get_all', response.data)
  }, function (response) {
    // console.log(response.status)
  })
}

/*
* Event Listeners
*/
store.on('project_list_all', store.project_list_all)
store.on('project_action', store.project_action)
store.on('project_load', store.project_load)
store.on('project_save', store.project_save)

/*
 * Helper functions
 */
store.get_current_project = function () {
  return '/' + store.state.current_project.project.folder + '/' + store.state.current_project.project.name
}

store.userproject = { 'type': 'playground', 'folder': 'User Projects' }
store.get_userproject_url = function (name) {
  return '/' + store.userproject.type + '/' + store.userproject.folder + '/' + name
}

store.clearArray = function (dst) {
  while (dst.length) dst.pop()
}

store.copyArray = function (or, dst) {
  this.clearArray(dst)
  for (var i in or) {
    dst.push(or[i])
  }
}

/*
 * Method that returns the ProtocoderURL Server, useful when debugging
 */
var getUrl = function (route) {
  if (DEBUG) {
    // return '192.168.2.151'
    return '127.0.0.1'
  } else {
    return window.location.hostname
  }
}

store.get_full_url_for_project = function (route) {
  return 'http://' + getUrl() + ':' + WEBAPP_PORT + '#/editor' + route
}

var getUrlWebapp = function (route) {
  return 'http://' + getUrl() + ':' + WEBAPP_PORT + route
}

var getUrlWs = function () {
  return 'ws://' + getUrl() + ':' + WS_PORT
}

store.getUrlForCurrentProject = function () {
  var p = store.state.current_project.project
  return getUrlWebapp('/api/project/' + encodeURIComponent(p.folder + '/' + p.name + '/'))
}

store.loadDocumentation = function () {
  var query = {}

  Vue.axios.get('/static/documentation.json', query).then(function (response) {
    // console.log(TAG + ': project_load(status) > ' + response.status)
    // console.log('loading documentation')
    // console.log(response.data)
    store.state.documentation = response.data
    store.emit('documentation_loaded')
  }, function (response) {
  })
}

store.save_browser_config = function () {
  localStorage.setItem('browser', JSON.stringify(state.preferences))
  // console.log(state.preferences)
}

store.load_browser_config = function () {
  state.preferences = JSON.parse(localStorage.getItem('browser') || '[]')
  // console.log(state.preferences)
}

/*
* Websockets for rapid communication
*/
var ws
var wsIsConnected = false
var reconnectionInterval

store.websockets_init = function () {
  var that = this

  // console.log('trying to connect to ' + getUrlWs())
  ws = new WebSocket(getUrlWs())

  ws.onerror = function (e) {
    console.log('ws error', e)
  }

  ws.onopen = function () {
    // console.log('ws connected')
    wsIsConnected = true
    clearInterval(reconnectionInterval) // _s the reconnection
    store.emit('device', { connected: true })

    // restart
    store.emit('project_list_all')
  }

  ws.onmessage = function (e) {
    // console.log('ws message', e)
    var data = JSON.parse(e.data)
    // console.log(e.data)

    // getting console data
    switch (data.module) {
      case 'webeditor':
        if (data.action === 'load_project') {
          store.emit('load_project_from_app', data)
        }
        break

      case 'console':
        // console.log(e)
        store.emit('console', data)
        break
      // getting device data
      case 'device':
        // console.log('qq', store.state.device_properties)
        data.connected = true
        Vue.set(store.state, 'device_properties', data)
        store.emit('device', data)
        // store.device_properties = data

        break
      case 'dashboard':
        console.log('dashboard', data)
        store.emit('dashboard', data)
        break
      default:
    }
  }

  ws.onclose = function () {
    // console.log('ws disconnected')
    // this.protoEvent.send('ui_appConnected', false)
    wsIsConnected = false
    store.emit('device', { connected: false })

    // try to reconnect
    reconnectionInterval = setTimeout(function () {
      console.log('trying to reconnect')
      that.websockets_init()
    }, 20000)
  }
}

store.send_ws_data = function (data) {
  if (wsIsConnected) ws.send(JSON.stringify(data))
}

store.websockets_init()

store.mouse = function () {
  document.onmousemove = function handleMouseMove(event) {
    event = event || window.event
    // console.log(event.button)
    // console.log(event.pageX, event.pageY)
  }
}

// store.mouse()

store.mydragg = function () {
  return {
    move: function (divid, xpos, ypos) {
      divid.style.left = xpos + 'px'
      divid.style.top = ypos + 'px'
    },
    startMoving: function (divid, container, evt) {
      evt = evt || window.event

      var posX = evt.clientX
      var posY = evt.clientY
      var divTop = divid.style.top
      var divLeft = divid.style.left

      var eWi = parseInt(divid.style.width)
      var eHe = parseInt(divid.style.height)
      var cWi = parseInt(document.getElementById(container).style.width)
      var cHe = parseInt(document.getElementById(container).style.height)

      document.getElementById(container).style.cursor = 'move'
      divTop = divTop.replace('px', '')
      divLeft = divLeft.replace('px', '')
      var diffX = posX - divLeft
      var diffY = posY - divTop

      document.onmousemove = function (evt) {
        evt = evt || window.event
        var posX = evt.clientX
        var posY = evt.clientY
        var aX = posX - diffX
        var aY = posY - diffY

        if (aX < 0) aX = 0
        if (aY < 0) aY = 0
        if (aX + eWi > cWi) aX = cWi - eWi
        if (aY + eHe > cHe) aY = cHe - eHe

        store.mydragg.move(divid, aX, aY)
      }
    },
    stopMoving: function (container) {
      document.getElementById(container).style.cursor = 'default'
      document.onmousemove = function () { }
    }
  }
}

store.loadSettings = function () {
  var savedSettings = localStorage.getItem('preferences')
  // console.log('loadSettings', savedSettings)
  if (typeof savedSettings === 'undefined' || !savedSettings || savedSettings === 'null') {
    // console.log('not loading settings')
  } else {
    // console.log('loadingSettings')
    store.state.preferences = JSON.parse(savedSettings)
  }

  // we get the settings injected from the app
  if (typeof settingsFromAndroid !== 'undefined') {
    console.log('WebIDE is loaded within the app', settingsFromAndroid.isTablet())
    Vue.set(store.state.preferences['other'], 'WebIDE as default editor', settingsFromAndroid.getWebIde())
  } else {
    console.log('WebIDE is not loaded within the app')
  }
}

store.saveSettings = function () {
  console.log('saveSettings')
  localStorage.setItem('preferences', JSON.stringify(this.state.preferences))

  // console.log('settingsFromAndroid', settingsFromAndroid)
  if (settingsFromAndroid != null) {
    console.log('qq', store.state.preferences['other']['WebIDE as default editor'])
    settingsFromAndroid.setWebIde(store.state.preferences['other']['WebIDE as default editor'])
  }
}

store.clearSettings = function () {
  console.log('clearSettings')
  localStorage.setItem('preferences', null)
}
