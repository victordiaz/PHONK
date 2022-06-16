<template>


  <div class = "gpalette">
    <div class = "header" v-on:click = "toggle()">
      <h1>Palette</h1>
    </div>
    <div v-show = "isVisible" id = "content">
      <div id = "search">
        <input />
      </div>

      <div class = "module_list">
        <h2> modules 1</h2>
        <ul>
          <li v-for = "m in modules" v-on:click = "addModule(m)">
            <img src="http://corescribe.s3.amazonaws.com/wp-content/uploads/2015/12/31095613/analysis-module.png">
            <p>{{m.name}}</p>
          </li>
        </ul>
      </div>

      <div class = "module_list">
        <h2> modules 2</h2>
        <ul>
          <li v-for = "m in modules" v-on:click = "addModule(m)">
            <img src="http://corescribe.s3.amazonaws.com/wp-content/uploads/2015/12/31095613/analysis-module.png">
            <p>{{m.name}}</p>
          </li>
        </ul>
      </div>

    </div>
  </div>
</template>

<script>
export default {
  name: 'GPalette',
  props: ['modules', 'amodules'],
  data () {
    return {
      isVisible: true
    }
  },
  computed: {
  },
  methods: {
    toggle: function () {
      this.isVisible = !this.isVisible
    },
    addModule: function (module) {
      this.amodules.push(module)
    }
  }
}
</script>

<style lang = "less">
@import (reference) "../../assets/css/variables.less";

.gpalette {
  color: black;
  background: rgb(241, 241, 241);
  border-bottom: 2px solid rgba(0, 0, 0, 0.2);
  height: 100%;
  position: absolute;
  right: 0;
  filter: drop-shadow(0 0 1px rgba(0,0,0,0.4)) drop-shadow(0 3px 4px rgba(0,0,0,0.4));

  .header {
    cursor: pointer;
    transform: translate3d(-12px, 72px, 0px) rotate3d(0, 0, 1, -90deg);
    transform-origin: left;
    padding: 6px 18px;
    position: absolute;
    border-radius: 2px 2px 0px 0px;
    font-weight: 600;
    color: gray;
    border-bottom: 0px solid rgba(0, 0, 0, 0.05);
    background: rgb(241, 241, 241);

    &:hover {
      background: rgb(222, 222, 222);
    }

    button {
      color: white;
    }
  }

  #search {
    box-sizing: content-box;
    width: 100%;
    display: inline-flex;

    input {
      width: 100%;
      background: rgba(0, 0, 0, 0.57);
      outline: none;
      margin: 8px;
      border: 2px solid rgba(0, 0, 0, 0.06);
      border-radius: 12px;
      padding: 2px 12px;
      color: white;
    }
  }

  .module_list {
    font-family: monospace;
    padding: 10px;
    border-bottom: 3px solid #e8e8e8;
    margin-bottom: 5px;

    h2 {
      padding: 0px 0px 2px 0px;
      color: gray;
      font-size: 0.7em;
    }

    li {
      cursor: pointer;
      display: inline-table;
      text-align: center;
      font-size: 0.7em;
      padding: 2px;

      &:hover {
        background: rgba(255, 255, 255, 0.8);
        border-radius: 2px;
      }
    }

    img {
      width: 50px;
      display: inline-block;
      float: left;
    }
  }

}

</style>
