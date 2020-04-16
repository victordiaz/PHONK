<template>
  <div
    id="documentation"
    class="proto_panel"
    v-bind:class="{ expanded: expanded, collapsed: !expanded }"
  >
    <!--   -->
    <div class="wrapper">
      <div class="actionbar">
        <h1 class="title" v-on:click="expanded = !expanded">Docs</h1>
        <input type="text" v-model="search" placeholder="type to filter..." />
        <ul>
          <li title="Switch view" class="material-icons" v-on:click="switch_view">reorder</li>
        </ul>
      </div>

      <div class="content" v-bind:class="[view_type]">
        <div
          id="browser"
          class="scrollbar"
          v-bind:class="{
            hide_debug: !sharedState.preferences['other'][
              'show TODO in documentation'
            ],
          }"
        >
          <!-- TODO orderBy -->
          <div v-for="object in queriedDocumentation" class="object">
            <h3>{{ object.name }}</h3>
            <ul>
              <!-- TODO filter and orderBy name-->
              <li v-for="f in object.fields">
                <p>{{ f.name }}</p>
              </li>
            </ul>
            <ul>
              <!-- TODO filter and orderBy name -->
              <li
                v-if="show_method(m)"
                v-for="m in object.methods"
                v-bind:class="{
                  todo: m.status === 'TODO',
                  todo_example: m.status === 'TODO_EXAMPLE',
                  toreview: m.status === 'TOREVIEW',
                  advanced: m.advanced === true,
                  missing: m.status === 'missing',
                }"
              >
                <p v-on:click="select_method(object, m)">{{ m.name }}()</p>
              </li>
            </ul>
          </div>
        </div>
        <transition name="upanim2" mode="out-in">
          <div id="card" :class="{ inset: view_type === 'vertical' }" v-if="show_card">
            <documentation-card :data="selected"></documentation-card>
          </div>
        </transition>
      </div>
    </div>
  </div>
</template>

<script>
import Store from '../Store'
import _ from 'lodash'
import DocumentationCard from './DocumentationCard'

export default {
  name: 'Documentation',
  components: {
    DocumentationCard
  },
  data () {
    return {
      sharedState: Store.state,
      msg: 'Hello World!',
      documentation: Store.state.documentation,
      showing: true,
      selected: {},
      search: '',
      show_card: false,
      view_type: 'over',
      expanded: true
    }
  },
  computed: {
    arrowposition: function () {},
    queriedDocumentation: function () {
      var that = this
      var doc = _.cloneDeep(this.documentation)

      if (!doc) return
      var k = doc.length
      while (k--) {
        doc[k].methods = doc[k].methods.filter(function (o) {
          if (o.name.toLowerCase().indexOf(that.search.toLowerCase()) !== -1) {
            return o
          }
        })

        doc[k].fields = doc[k].fields.filter(function (o) {
          if (o.name.toLowerCase().indexOf(that.search.toLowerCase()) !== -1) {
            return o
          }
        })

        if (doc[k].methods.length === 0) {
          doc.splice(k, 1)
          // console.log('remove ' + k)
        }
      }
      return doc
    }
  },
  methods: {
    show_method: function (m) {
      if (m.advanced) {
        return this.sharedState.preferences['other']['show advanced api']
      } else {
        return true
      }
    },
    load_documentation: function (doc) {
      // this.documentation = store.state.documentation
      this.documentation = _.sortBy(Store.state.documentation, 'name')
      for (var k in this.documentation) {
        this.documentation[k].methods = _.sortBy(
          this.documentation[k].methods,
          'name'
        )
        this.documentation[k].fields = _.sortBy(
          this.documentation[k].fields,
          'name'
        )
      }
      // console.log('loaded ', this.documentation)
    },
    select_method: function (object, method) {
      var that = this
      this.show_card = false
      setTimeout(function () {
        console.log(object.name, method.name)
        that.show_card = true
        that.selected = { object: object, method: method }
      }, 20)
    },
    close: function () {
      Store.emit('toggle', 'load_documentation')
    },
    performSearch: function (objs) {
      var that = this
      console.log('qq', objs)
      return objs.filter(function (o) {
        return o.name.indexOf(that.search) !== -1
      })
    },
    switch_view: function () {
      console.log('switching view')
      switch (this.view_type) {
        case 'horizontal':
          this.view_type = 'over'
          break
        // case 'vertical':
        //  this.view_type = 'over'
        //  break
        case 'over':
          this.view_type = 'horizontal'
          break
      }
    },
    close_card: function () {
      this.show_card = false
    }
  },
  created () {
    Store.loadDocumentation()
    Store.on('documentation_loaded', this.load_documentation)
    Store.on('close_card', this.close_card)
  }
}
</script>

<style lang="less">
@import (reference) '../assets/css/variables.less';
@import (reference) '../assets/css/hacks.less';

.todo {
  background: cyan;
}

.todo_example {
  background: yellow;
}

.toreview {
  background: purple;
}

.missing {
  background: orange;
}

.advanced::before {
  content: 'A';
  background: magenta;
}

.inset {
  margin: 13px;
  .main_shadow;
  margin-top: -10px;
  z-index: 10;
  background: white;
}

#documentation {
  height: 40%;
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
  padding: 0px;
  z-index: 1;

  .actionbar {
    align-items: center;
    padding: 0;

    .title {
      width: 100%;
    }

    input {
      border: 0px;
      outline: none;
      padding: 8px;
      border-radius: 2px;
      background: transparent;
      width: 100%;
      font-size: 1em;
      box-sizing: border-box;
    }

    .placeHolder {
      color: @primaryTextColor;
      font-family: 'Roboto Mono';
      font-size: 0.8rem;
    }

    *::-webkit-input-placeholder {
      .placeHolder;
    }
    *:-moz-placeholder {
      /* FF 4-18 */
      .placeHolder;
    }
    *::-moz-placeholder {
      /* FF 19+ */
      .placeHolder;
    }
    *:-ms-input-placeholder {
      /* IE 10+ */
      .placeHolder;
    }
  }

  .content {
    display: flex;
    height: 100%;
    min-height: 1px;
    padding: 0px;
    overflow: hidden;
    position: relative;

    .btn-close {
      color: gray;
      right: 5px;
      top: -8px;

      :hover {
        color: black;
      }
    }

    &.horizontal {
      flex-direction: row;

      .btn-close {
        display: none;
      }

      #browser {
        max-width: 212px;
      }
    }

    &.vertical {
      flex-direction: column;
    }

    &.over #card {
      position: absolute;
      width: 100%;
      height: 100%;
      box-sizing: border-box;
    }

    #browser {
      font-family: 'Roboto Mono';
      flex: 1;
      overflow-y: scroll;
      overflow-x: hidden;

      &.hide_debug {
        ul li {
          background: transparent !important;

          &:before {
            content: '';
          }
        }
      }

      h1 {
        font-size: 1.1em;
        text-transform: uppercase;
        font-weight: 700;
      }

      h2 {
        font-size: 1.2em;
        font-weight: 600;
      }

      h3 {
        font-weight: 600;
        font-size: 0.8em;
        display: inherit;
        padding: 2px;
        color: @accentColor;
        margin-bottom: 6px;
        margin-left: 2px;
        border-radius: 0px;
      }

      .object {
        display: inline-block;
        vertical-align: top;
        width: 100%;
        margin-bottom: 10px;
        box-sizing: border-box;

        ul {
          column-count: auto;
          -moz-column-count: auto;
          column-gap: 25px;
          -moz-column-gap: 25px;
          column-width: 150px;
          -moz-column-width: 150px;
          font-size: 0.8em;
        }

        li {
          color: #f7f7f7;
          font-weight: 400;
          letter-spacing: 1px;
          font-size: 0.9em;

          p {
            cursor: pointer;
            padding: 5px 4px;
            display: inline-block;
          }

          p:hover {
            color: @accentColor;
          }
        }
      }
    }
  }

  #card {
    background: #32332d;
    flex: 1.5;
    overflow: hidden;
    overflow-y: auto;
    .scrollbar;
    color: white;
  }
}
</style>
