<template>
  <pop-over arrow = "top" :posx = "posx" :posy = "posy">
    <div class = "popup_content">      
      <div class = "block" v-for="(v, name) in sharedState.preferences" :key="name">
        <h3>{{name}}</h3>
        <ul>
          <li v-for = "(value, prop) in v" :key="prop">
            <p>{{prop}}</p>
            <div class = "widget">
              <!-- boolean -->
              <input
                v-show = "valueType(value) === 'boolean'"
                type="checkbox"
                id="checkbox"
                v-model="sharedState.preferences[name][prop]"
              >
              <!-- number -->
              <div
                v-show="valueType(value) === 'number'" class="size">
                <button v-on:click="decrease_size" class="boxed">-</button>
                <input v-model="sharedState.preferences[name][prop]" readonly="readonly" />
                <button v-on:click="increase_size" class="boxed">+</button>
              </div>
            </div>
          </li>
        </ul>
      </div>
      <button class = "clean"
        v-on:click = "clearSettings">
          reset settings
      </button>
    </div>
  </pop-over>
</template>

<script>
import store from '../../Store'
import PopOver from '../views/PopOver'

export default {
  name: 'PreferencesPopover',
  components: {
    PopOver
  },
  data () {
    return {
      posx: '4px',
      posy: '58px',
      sharedState: store.state,
    }
  },
  methods: {
    valueType: function (value) {
      return typeof (value)
    },
    increase_size: function () {
      this.sharedState.preferences['editor']['text size'] += 1
      store.emit('font_changed')
    },
    decrease_size: function () {
      this.sharedState.preferences['editor']['text size'] -= 1
      store.emit('font_changed')
    },
    clearSettings: function () {
      store.clearSettings()
    }
  },
  watch: {
    'sharedState.preferences': {
      handler: function (newVal, oldVal) {
        console.log('watching', oldVal + ' ' + newVal)
        store.saveSettings()
      },
      deep: true
    }
    /*,
    "preferences['other']['WebIDE as default editor']": {
      handler: function (newVal, oldVal) {
        console.log('changed webid ' + newVal)
      }
    }
    */
  },
  created () {
  },
  destroyed () {
  }
}
</script>

<style lang = "less" scoped>
@import (reference) "../../assets/css/variables.less";

.popover {
  ul, li {
    width: 100%;
  }
}

ul {
  display: block;
}

li {
  display: inline-flex;
  align-items: center;
  padding-bottom: 6px;
  text-transform: capitalize;

  p {
    flex: 5;
  }
}

.widget {
  display: inline-flex;

  .size {
    // background: #ff356b;
    border: 1px solid var(--color-lines);

    button {
      border: none;
      color: var(--color-text-light);
      border: 1px solid var(--color-transparent);

      &:hover {
        color: var(--color-accent);
        background: none;
      }
    }

    input {
      color: var(--color-text-light);
      background: transparent;
    }
  }
}

button {
  padding: 8px 12px;
}

input {
  width: 31px;
  margin: 0px 0px;
  padding: 0;
  text-align: center;
  outline: none;
  color: var(--color-accent);
  border: 0px;
}

input[type=checkbox] {
  width: 20px;
  height: 20px;
  margin: 0px 0px;
  padding: 0;
  text-align: center;
  outline: none;
  color: var(--color-accent);
  border: 1px solid var(--color-lines);
  appearance: none;
  position: relative;
  cursor: pointer;
}

input[type=checkbox]:hover {
  // background: var(--color-accent);
  border-color: var(--color-accent);
  // appearance: none;
}


input[type=checkbox]:checked:after {
  content: '';
  position: absolute;
  background: var(--color-accent);
  top: 3px;
  left: 3px;
  bottom: 3px;
  right: 3px;
}

</style>
