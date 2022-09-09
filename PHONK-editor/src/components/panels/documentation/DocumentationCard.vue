<template>
  <div class="info">
    <button class="clean btn-close" v-on:click="$emit('onCloseCard')">
      <i class="material-icons">close</i>
    </button>

    <div class="usage">
      <ul>
        <li v-for="(signature, index) in data.method.signatures" :key="index">
          <h2>
            <span class="object">{{ data.object.name }}.</span>
            <span class="method">{{ data.method.name }}</span> (
            <span v-for="(f, i) in signature.fields" :key="i">
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
      </div>
    </div>

    <div class="params">
      <h3>params</h3>
      <table>
        <tr v-for="p in data.method.params" :key="p.name">
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
        <li v-for="p in data.method.see" :key="p">{{ p }}</li>
      </ul>
    </div>

    <div class="stub">
      <a href="http://" target="_blank">
        <span>Help improve this doc</span>
        <i class="material-icons">launch</i>
      </a>
      <a v-bind:href="getGithubUrl(data.method)" target="_blank">
        <span>Show in sourcecode</span>
        <i class="material-icons">launch</i>
      </a>
    </div>
  </div>
</template>

<script>
// import Marked from 'marked'
// import Highlight from 'highlight.js'
// import 'highlight.js/styles/monokai.css'
/*
Marked.setOptions({
  highlight: function (code, lang) {
    return Highlight.highlightAuto(code, [lang]).value
  }
})
*/

export default {
  name: 'DocumentationCard',
  filters: {
 //   marked: Marked
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
      // Highlight.highlightBlock(this.$el.querySelector('.code'))
    })
  }
}
</script>

<style lang="less" scoped>
@import (reference) '../../../assets/css/variables.less';

.info {
  font-size: 0.8em;
  position: relative;
  padding: 10px;

  & > * {
    padding: 10px 0px;
    color: var(--color-text-light);
  }

  h2 {
    .font-mono-400;
    font-size: 1em;
    display: inline-flex;

    * {
      display: inline-flex;
    }

    .method {
      border-bottom: 2px solid var(--color-accent);
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
    font-size: 0.9em;
    text-transform: uppercase;
    color: var(--color-text-light-faded);
  }

  table {
    width: 100%;
  }

  .returns {
    .type:hover {
      color: var(--color-accent);
      cursor: pointer;
    }
  }

  .params {
    td {
      padding: 3px 0;
    }

    .type {
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
    .font-mono-400;
    overflow: initial;
  }

  li {
    padding: 3px 0;
  }

  .description {
    line-height: 1.3em;
  }

  .usage {
    li {
      padding: 2px;
    }
  }

  .stub {
    display: inline-flex;
    border-top: 1px solid var(--color-lines);
    margin-top: 18px;
    padding-top: 18px;
    gap: 12px;

    a {
      color: var(--color-accent);
      display: flex;
      align-items: center;
      text-decoration: none;

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
      background: var(--color-accent);
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
