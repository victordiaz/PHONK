<template>
  <div id = "info">
    <div v-if = "" class = "btn-sidebar btn-close" v-on:click = "close">
      <i class = "fa fa-close"></i>
    </div>

    <h2><span class = "object">{{data.object.name}}.</span><span class = "method">{{data.method.name}}()</span></h2>
    <p class = "description">{{data.method.description}}</p>

    <div class = "example">
      <h3>example</h3>
      <div class = "code_wrapper">
        <code class = "code">
          <pre class = "javascript">
// this is not a real example
function () {
  console.log('hola')
}
          </pre>
        </code>
        <!--
        <a v-bind:href = "store.get_full_url_for_project(data.method.exampleLink)" target = "_blank">Open in Tab</a>
        -->
      </div>
    </div>

    <div class = "usage">
      <h3>Usage</h3>
      <ul>
        <li v-for = "(signature, index) in data.method.signatures">
          {{data.object.name}}.{{data.method.name}}(<span v-for = "f in signature.fields">{{f.name}}<span v-if = "index != signature.fields.length-1">, </span></span>)
        </li>
      </ul>
    </div>

    <div class = "params">
      <h3>params</h3>
      <table>
        <tr v-for = "p in data.method.params">
          <td class = "param">{{p.name}}</td>
          <td class = "type">String</td>
          <td class = "description">{{p.description}}</td>
        </tr>
      </table>
    </div>

    <div class = "returns">
      <h3>returns</h3>
      <table>
        <tr>
          <td class = "type">{{data.method.returning.type}}</td>
          <td class = "description">{{data.method.returning.name}}</td>
        </tr>
      </table>
    </div>

    <div class = "related">
      <h3>Related</h3>
      <ul>
        <li v-for = "p in data.method.see">{{p}}</li>
      </ul>
    </div>

    <div class = "stub">
      <a href = "http://" target = "_blank">Help improve this doc</a>
      <a v-bind:href = "getGithubUrl(data.method)" target = "_blank"><i class = "fa fa-github"></i> Show method sourcecode</a>
    </div>

  </div>
</template>

<script>
import Store from '../Store'
import Marked from 'marked'
import Highlight from 'highlight.js'
import 'highlight.js/styles/github-gist.css'

Marked.setOptions({
  highlight: function (code, lang) {
    return Highlight.highlightAuto(code, [lang]).value
  }
})

export default {
  name: 'DocumentationCard',
  filters: {
    marked: Marked
  },
  props: {
    data: Object,
    posy: Number
  },
  data () {
    return {
      store: Store
    }
  },
  computed: {
    arrowposition: function () {
    }
  },
  methods: {
    getGithubUrl: function (method) {
      var urlGithub = 'https://github.com/Protocoder/Protocoder/blob/app_cleanup/'
      return urlGithub + method.locationFile.substring(14) + '#L' + method.locationInfile
    },
    close: function () {
      Store.emit('close_card')
    }
  },
  mounted () {
    this.$nextTick(function () {
      // $('pre code').each(function(i, block) {
      // hljs.highlightBlock(block)
      console.log('------------------')
      Highlight.highlightBlock(this.$el.querySelector('.code'))
    })
  }
}
</script>

<style lang = "less">
@import (reference) "../assets/css/variables.less";

.vertical #card {
  border-top: solid 0px #54524f;
}

.horizontal #card {
  border-left: solid 0px #54524f;
}

#info {
  font-size: 0.8em;
  position: relative;

  & > * {
    padding: 10px 0px;
    color: black;
  }

  h2 {
    font-family: @editorFont;
    font-size: 1em;
    font-weight: 600;

    .method {
      border-bottom: 2px solid @accentColor;
    }
  }
  h3 {
    padding: 5px 0px;
    color: @accentColor;
    font-weight: 500;
    font-size: 0.9em;
    text-transform: uppercase;
  }

  table {
    width: 100%;
  }

  .params {
    td {
      padding: 3px 0;
    }

    .type {
      color: gray;
    }
  }

  .code_wrapper {
    position: relative;
    box-shadow: 0 0 5px -1px #9f9f9f;
    overflow: hidden;
    margin: 0.8em 0;
  }

  code {
    padding: 8px 8px;
    box-sizing: border-box;
    width: 100%;

    background: white;
    border-radius: 2px;
    line-height: 1.3em;
  }

  code, .usage>ul, .param, .returns .type, .related{
    font-family: @editorFont;
    overflow: initial;
  }

  li {
    padding: 3px 0;
  }

  .description {
    font-weight: 300;
    line-height: 1.3em;
  }

  .usage {
    li {
      padding: 2px;
    }
  }

  .stub {
    margin: 10px 0px;
    display: block;
    color: white;
    /* animation: fadeIn 1s infinite alternate; */

    a {
      padding: 5px 5px;
      background: @accentColor;
      border-radius: 2px;
      color: white;
      font-weight: 600;
      text-decoration: none;
      display: inline-block;
      margin-right: 2px;
      margin-bottom: 2px;
      border: 1px solid rgba(255, 255, 255, 0.11);

      &:hover {
        opacity: 0.8;
      }

      &:active {
        opacity: 0.6;
      }
    }
  }

  .example {
    a {
      background: @accentColor;
      display: block;
      position: absolute;
      bottom: 0;
      right: 0;
      color: white;
      padding: 3px 10px;
      text-decoration: none;
    }
  }

  @keyframes fadeIn {
    from { opacity: 0.8; }
  }
}

</style>
