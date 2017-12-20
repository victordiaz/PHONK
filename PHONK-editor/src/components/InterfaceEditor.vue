<template>
  <div id = "interface_editor" class = "proto_panel">
    <div id = "toolbar"><h1>UI name</h1></div>

    <div id = "working_area">
      <div id = "device">
        <img v-for = "(e, index) in elements" v-bind:style = "{ top:e.y, left:e.x, width:e.width, height:e.height }" v-bind:src = "e.image" v-on:click="selected_view = index">{{e.image}}</img>
      </div>

      <!--
      <ul>
        <li v-for = "e in elements">{{e.id}} {{e.x}} {{e.y}} {{e.width}} {{e.height}}</li>
      </ul>
    -->

      <div id = "panels">
        <div id = "ui_toolbox">
          <button v-for = "t in types">{{t}}</button>
        </div>
        <div id = "view_properties">
          {{selected_view.name}}{{selected_view.type}}
          <ul>
            <li v-for = "(p, key) in selected_view.properties">{{key}} <span>{{p}}</span></li>
          </ul>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
import Store from '../Store'

export default {
  name: 'InterfaceEditor',
  data () {
    return {
      types: [],
      elements: [
        /*
        { id: '1', x: '0px', y: '0px', width: '100px', height: '200px', image: 'data:image/png;base64,baseUrlVar' },
        { id: '2', x: '200px', y: '100px', width: '200px', height: '200px' }
        */
      ],
      selected_view: -1
      /*
      selected_view: {
        name: 'slider',
        type: 'slider',
        image: 'base64',
        osc: '/osc/to/computer',
        properties: {
          x: 100,
          y: 200,
          width: 100,
          height: 200,
          color: 'red'
        }
      }
      */
    }
  },
  methods: {
    listed_types: function (data) {
      for (var i in data) this.types.push(data[i])
    },
    views_get_all: function (data) {
      console.log(data)
      Store.clearArray(this.elements)
      for (var i in data) {
        data[i].x = data[i].x / 1080 * 300 + 'px'
        data[i].y = data[i].y / 1794 * 500 + 'px'
        data[i].width = data[i].width / 1080 * 300 + 'px'
        data[i].height = data[i].height / 1794 * 500 + 'px'

        this.elements.push(data[i])
      }
    }
  },
  route: {
    data () {
      this.title = 'InterfaceEditor'
    }
  },
  created () {
    Store.views_list_types()
    setInterval(function () {
      Store.views_get_all()
    }, 2500)
    Store.on('views_list_types', this.listed_types)
    Store.on('views_get_all', this.views_get_all)
  },
  mounted () {
  },
  destroyed () {
  }
}
</script>
<style lang = "less">
@import "../assets/css/variables.less";

#interface_editor {
  background: rgba(255, 255, 255, 1);
  width: 100%;
  height: 100%;
  z-index: 1;

  #toolbar {
    background: yellow;
    height: 50px;
    border-bottom: 2px solid black;
  }

  #working_area {
    display: flex;
    align-items: center;
    justify-content: center;

    #device {
      width: 300px;
      height: 500px;
      background: #484848;
      position: relative;
      box-shadow: 0px 0px 12px -1px black;

      & > img {
        position: absolute;
        background: yellow;
        box-sizing: border-box;

        &:hover {
          border: 1px solid yellow;
        }
      }
    }
  }

  #panels {
    width: 300px;
  }
  #ui_toolbox {
    background: black;
    width: 100%;
    height: 200px;
  }

  #view_properties {
    background: yellow;
    width: 100%;
    height: 200px;
  }



}

</style>
