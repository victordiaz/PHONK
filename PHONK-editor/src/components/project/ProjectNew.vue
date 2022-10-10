<template>
  <div class id="editor_panel_new">
    <div class="left">
      <div class="form">
        <input
          id="projectname"
          ref="newproject"
          name="projectname"
          type="text"
          placeholder="MyProject..."
          class="form-control input-md project-input"
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
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Store from '../../Store'

export default {
  name: 'ProjectNew',
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
@import (reference) '../../assets/css/variables.less';

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

  .login_bottom {
    text-align: center;
  }

  input {
    border: 0 !important;
    border-radius: 2px;
  }

  .submit {
    display: flex;
    align-items: center;
    margin-left: 12px;
  }
}
</style>
