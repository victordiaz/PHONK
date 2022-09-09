import Vue from 'vue'
import store from './Store'
import { domain } from './filters'
import App from './App'
import VueRouter from 'vue-router'

import Editor from './components/editor/Editor.vue'
// import TutorialLoader from './components/TutorialLoader'
// import About from './components/About'
// import Preferences from './components/Preferences'
import UIEditor from './components/uieditor/UIEditor'

import axios from 'axios'
import VueAxios from 'vue-axios'

// register filters globally
Vue.filter('domain', domain)

// install router
Vue.use(VueRouter)
Vue.use(VueAxios, axios)

// routing
var router = new VueRouter({
  routes: [
    {
      path: '/',
      redirect: { name: 'editor' }
    },
    {
      path: '/editor',
      name: 'editor',
      component: Editor,
      title: 'PHONK'
    },
    {
      path: '/editor/:type/:folder/:project',
      name: 'editor.load',
      component: Editor,
      title: 'PHONK'
    },
    {
      path: '/uieditor/',
      name: 'uieditor',
      title: 'UI Editor',
      component: UIEditor
    },
    {
      path: '/tutorial/:id',
      name: 'tutorial',
      title: 'Tutorial',
      // component: TutorialLoader
    },
    {
      path: '/about',
      name: 'about',
      // component: About
    },
    {
      path: '/preferences',
      name: 'preferences',
      // component: Preferences
    }
  ]
})

const app = new Vue({
  router,
  ...App
})

export { app, router, store }

app.$mount('app')