<template>
  <div class id="editor_panel_new">
    <div class="left">
      <div class="form">
        <!-- info input -->
        <!--
        <label for="choosefolder">choose a folder</label>
        <select id="choosefolder" name="choosefolder" class="form-control">
          <option value="1">Option one</option>
          <option value="2">Option two</option>
        </select>
        -->

        <input
          id="projectname"
          ref="newproject"
          name="projectname"
          type="text"
          placeholder="MyProject..."
          class="form-control input-md"
          v-model="projectName"
          required
          @keyup.enter="create_project()"
        />

        <div class="submit">
          <button
            id="create"
            name="create"
            class="btn btn-success boxed"
            v-on:click="create_project()"
          >Create</button>
          <message-error v-show="!status">The project cannot be created</message-error>
        </div>
      </div>
    </div>

    <!--
    <div class="right">
      <p>Create a new project</p>
      <p>{{status}}</p>
    </div>
    -->
  </div>
</template>

<script>
import Store from '../Store'
import MessageError from './views/MessageError'

export default {
  name: 'ProjectNew',
  components: {
    MessageError
  },
  data () {
    return {
      projectName: '',
      status: true
    }
  },
  methods: {
    create_project: function () {
      Store.project_create(this.projectName)
    },
    project_created: function (status) {
      this.status = status
      console.log('created ' + status)
      if (status) {
        Store.state.show_load_project = false
        this.projectName = ''
        Store.emit('project_list_all')
      }
    },
    close () {
      Store.emit('toggle', 'new_project')
      this.status = true
    }
  },
  created () {
    Store.on('project_created', this.project_created)
    this.$nextTick(() => this.$refs.newproject.focus())
    // console.log('--------> created')
  },
  destroyed () {
    Store.removeListener('project_created', this.project_created)
  }
}
</script>

<style lang="less">
@import (reference) '../assets/css/variables.less';

#editor_panel_new {
  .left {
    flex: 1;
    padding: 0;
  }

  .form {
    display: flex;
  }

  label {
    font-size: 1em;
    line-height: 2em;
    text-align: center;
  }

  .placeHolder {
    color: @accentColor_1;
    font-family: 'Roboto Mono';
    font-size: 1rem;
  }

  /* do not group these rules */
  *::-webinput-placeholder {
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

  select,
  input {
    flex: 2;
    width: 100%;
    min-width: 20%;
    outline: none;
    border: none;
    width: 100%;
    box-sizing: border-box;
    color: black;
    font-size: 1rem;
    background: white;
    padding: 10px;
    border: 1px solid @accentColor_1;
  }

  .login_bottom {
    text-align: center;
  }

  .submit {
    display: flex;
    align-items: center;
    margin-left: 12px;
  }
}
</style>
