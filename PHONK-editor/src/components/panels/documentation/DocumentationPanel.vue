<template>
  <div
    id="documentation"
    class="proto_panel"
    v-bind:class="{ expanded: expanded, collapsed: !expanded }"
  >
    <div class="wrapper">
      <div class="actionbar">
        <h1 class="title" v-on:click="expanded = !expanded">Docs</h1>
        <input type="text" v-model="search" placeholder="type to filter..." />
        <button id = "clearSearch" v-on:click = "clearSearch" v-if = "search !== ''"><i data-v-15cdbf56="" class="material-icons">close</i></button>
        <ul>
          <li title="Switch view" class="material-icons" v-on:click="switch_view">reorder</li>
        </ul>
      </div>

      <div class="content" v-bind:class="[view_type]">
        <div
          id="browser"
          class="scrollbar"
          v-bind:class="{
            hide_debug: true,
          }"
        >
          <div
            v-for = "(object, key) in documentation"
            :key="key">
            <div
              v-for="(methods, name) in object"
              :key="name">
              <documentation-object :object = "methods" v-on:onSelectMethod="select_method" :search = "search"/>
            </div>
          </div>
        </div>
        <transition name="upanim2" mode="out-in">
          <div id="card" :class="{ inset: view_type === 'vertical' }" v-if="show_card">
            <documentation-card :data="selected" v-on:onGoToClass="goToClass" v-on:onCloseCard = "close_card"></documentation-card>
          </div>
        </transition>
      </div>
    </div>
  </div>
</template>

<script>
import DocumentationCard from './DocumentationCard'
import DocumentationObject from './DocumentationObject'
import VueScrollTo from 'vue-scrollto'
import documentation from './documentation.json'

export default {
  name: 'DocumentationPanel',
  components: {
    DocumentationCard,
    DocumentationObject
  },
  data () {
    return {
      msg: 'Hello World!',
      documentation: documentation,
      showing: true,
      selected: {},
      search: '',
      show_card: false,
      view_type: 'over',
      expanded: true
    }
  },
  computed: {
  },
  methods: {
    select_method: function (o) {
      this.show_card = false

      if (o.type === 'field') {
        console.log(o.field, o.object)
        VueScrollTo.scrollTo('#' + o.field.type,
          500,
          {
            container: '#browser',
            duration: 500,
            easing: 'ease'
          }
        )
      } else {
        setTimeout(() => {
          console.log(o.object.name, o.method.name)
          this.show_card = true
          this.selected = o
        }, 20)
      }
    },
    performSearch: function (objs) {
      var that = this
      console.log('qq', objs)
      return objs.filter(o => {
        return o.name.indexOf(that.search) !== -1
      })
    },
    clearSearch: function () {
      this.search = ''
    },
    switch_view: function () {
      console.log('switching view')
      switch (this.view_type) {
        case 'horizontal':
          this.view_type = 'over'
          break
        case 'over':
          this.view_type = 'horizontal'
          break
      }
    },
    close_card: function () {
      this.show_card = false
    },
    goToClass: function (e) {
      this.clearSearch()
      this.close_card()

      VueScrollTo.scrollTo('#' + e.method.returning.type,
        500,
        {
          container: '#browser',
          duration: 500,
          easing: 'ease'
        }
      )
    }
  },
  created () {
  },
  mounted () {
  }
}
</script>

<style lang="less">
@import (reference) '../../../assets/css/variables.less';
@import (reference) '../../../assets/css/hacks.less';

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

  #clearSearch {
    font-size: 0.5em;
    color: #828282;

    i {
      font-size: 16.2px;
    }

    &:hover {
      color: white;
      background: transparent;
    }
  }

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
      color: white;
    }

    .placeHolder {
      color: rgba(255, 255, 255, 0.3);
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
      right: 10px;
      top: 0px;

      :hover {
        color: white;
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
      padding: 10px;

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
        display: flex;
        margin-top: 2em;
        margin-bottom: 1em;
        padding-left: 4px;
        font-size: 0.8em;
        font-weight: 600;
        background: #52534f;
        padding: 10px;
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
        margin-bottom: 20px;
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
          font-weight: 300;
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

.debug {
  font-size: 0.8rem;
  color: white;
}
</style>
