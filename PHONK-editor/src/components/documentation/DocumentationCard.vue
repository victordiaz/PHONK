<template>
  <div class="info">
    <div v-if class="btn-sidebar btn-close" v-on:click="$emit('onCloseCard')">
      <i class="material-icons">close</i>
    </div>

    <div class="usage">
      <ul>
        <li v-for="(signature, index) in data.method.signatures">
          <h2>
            <span class="object">{{ data.object.name }}.</span>
            <span class="method">{{ data.method.name }}</span> (
            <span v-for="(f, i) in signature.fields">
              <span class="field">{{ f.name }}</span>
              <span v-if="i < signature.fields.length - 1" class="comma">, </span>
            </span>)
          </h2>
        </li>
      </ul>
    </div>

    <p class="description">{{ data.method.description }}</p>

    <div class="example" v-if = "false">
      <h3>example</h3>
      <div class="code_wrapper">
        <code class="code">
          <pre class="javascript">
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

    <div class="params">
      <h3>params</h3>
      <table>
        <tr v-for="p in data.method.params">
          <td class="param">{{ p.name }}</td>
          <td class="type">String</td>
          <td class="description">{{ p.description }}</td>
        </tr>
      </table>
    </div>

    <div class="returns">
      <h3>returns</h3>
      <table>
        <tr>
          <td class="type" @click = "goToClass(data)">{{ data.method.returning.type }}</td>
          <td class="description">{{ data.method.returning.name }}</td>
        </tr>
      </table>
    </div>

    <div class="related" v-if = "false">
      <h3>Related</h3>
      <ul>
        <li v-for="p in data.method.see">{{ p }}</li>
      </ul>
    </div>

    <div class="stub">
      <a href="http://" target="_blank">
        <span>Help improve this doc</span>
      </a>
      <a v-bind:href="getGithubUrl(data.method)" target="_blank">
        <i class="material-icons">link</i>
        <span>Show in sourcecode</span>
      </a>
    </div>
  </div>
</template>

<script>
import Marked from 'marked'
import Highlight from 'highlight.js'
import 'highlight.js/styles/monokai.css'

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
    }
  },
  computed: {
  },
  methods: {
    getGithubUrl: function (method) {
      var urlGithub =
        'https://github.com/victordiaz/phonk/blob/master/phonk'
      return (
        urlGithub +
        method.locationFile.substring(14) +
        '#L' +
        method.locationInfile
      )
    },
    goToClass (cls) {
      console.log('goToClass ' + cls)
      this.$emit('onGoToClass', cls)
    }
  },
  mounted () {
    this.$nextTick(function () {
      // $('pre code').each(function(i, block) {
      // hljs.highlightBlock(block)
      Highlight.highlightBlock(this.$el.querySelector('.code'))
    })
  }
}
</script>

<style lang="less" scoped>
@import (reference) '../../assets/css/variables.less';

.vertical #card {
  border-top: solid 0px #54524f;
}

.horizontal #card {
  border-left: solid 0px #54524f;
}

.info {
  font-size: 0.8em;
  position: relative;
  padding: 10px;

  & > * {
    padding: 10px 0px;
    color: @primaryTextColor;
  }

  h2 {
    font-family: @editorFont;
    font-size: 1em;
    font-weight: 600;
    display: inline-flex;

    * {
      display: inline-flex;
    }

    .method {
      border-bottom: 2px solid @accentColor;
    }

    .field {
      border-bottom: 1px dotted white;
    }

    .comma {
      margin-right: 5px;
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

  .returns {
    .type:hover {
      color: @accentColor;
      cursor: pointer;
    }
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
    overflow: hidden;
    margin: 0.8em 0;
  }

  code {
    padding: 8px 8px;
    box-sizing: border-box;
    width: 100%;
    background: transparent;
    border: 1px dashed #4c4f4b;
    border-radius: 2px;
    line-height: 1.3em;
  }

  code,
  .usage > ul,
  .param,
  .returns .type,
  .related {
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
    display: inline-flex;
    /* animation: fadeIn 1s infinite alternate; */

    a {
      padding: 3px 5px;
      border-radius: 2px;
      color: @accentColor;
      font-weight: 600;
      text-decoration: none;
      display: inline-block;
      margin-right: 15px;
      margin-bottom: 0px;
      border: 1px solid @accentColor;
      display: inline-flex;
      line-height: 1.2rem;
      font-size: 0.8rem;
      display: flex;
      align-items: center;

      span {
        padding: 5px;
      }

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
    from {
      opacity: 0.8;
    }
  }
}
</style>
