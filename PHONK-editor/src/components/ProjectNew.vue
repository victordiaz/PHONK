<template>
  <div class = "" id="editor_panel_new">
    <div class = "left">
      <div class = "form">
        <!-- info input -->
        <!--
        <label for="choosefolder">choose a folder</label>
        <select id="choosefolder" name="choosefolder" class="form-control">
          <option value="1">Option one</option>
          <option value="2">Option two</option>
        </select>
        -->

        <input id="projectname" ref = "newproject" name="projectname" type="text" placeholder="MyProject" class="form-control input-md" v-model = "projectName" required="" @keyup.enter = "create_project()">

        <div class = "submit">
          <button id="create" name="create" class="btn btn-success" v-on:click = "create_project()">Create</button>
          <message-error v-show = "!status">The project cannot be created</message-error>
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
    project_created (status) {
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
    console.log('--------> created')
  },
  destroyed () {
    Store.remove_listener('project_created', this.project_created)
  }
}
</script>

<style lang='less'>
@import (reference) "../assets/css/variables.less";

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

  /* do not group these rules */
*::-webinput-placeholder {
    color: #bbbbbb;
}
*::-moz-placeholder {
    /* FF 19+ */
    color: #bbbbbb;
}
*:-ms-input-placeholder {
    /* IE 10+ */
    color: #bbbbbb;
}

  select, input {
    flex: 2;
    width: 100%;
    min-width: 20%;
    outline: none;
    border: none;
    // border-bottom: 1px solid @accentColor;
    width: 100%;
    box-sizing: border-box;
    color: black;
    font-size: 1em;
    background: transparent;
    padding: 10px;
    background: @backgroundColorSecondary;
  }

  .login_bottom {
    text-align: center;
  }

  .submit {
    display: flex;
    align-items: center;
    margin-left: 12px
  }

}

</style>
