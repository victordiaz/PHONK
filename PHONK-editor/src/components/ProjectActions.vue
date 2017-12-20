<template>
  <div id = "project-options">
    <div id = "project-actions" class ="">
    <button id = "project-run" v-on:click="run" class = ""> Run </button>
    <button id = "project-save" class = ""> Save </button>
    <button id = "project-save-as" class = ""> Save As </button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ProjectActions',
  data () {
    return {
    }
  },
  methods: {
    run: function () {
      console.log('dispatch: run')
      // store.$emit('run')
    }
  }
}
</script>

<style lang='less'>
@import "../assets/css/variables.less";

#project-options {
  user-select: none;
  padding: 22px 0px 15px 0px;
}

</style>
