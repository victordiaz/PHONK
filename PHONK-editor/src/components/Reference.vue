<template>
  <div id = "panel_quick_reference" class = "proto_panel">
    <ul v-for="file in files">
      <li> {{file.text}} </li>
    </ul>
  </div>
</template>

<script>
export default {
  name: 'Reference',
  data () {
    return {
      msg: 'Hello World!',
      files: [
        { type: 'text', text: 'qq.png' },
        { type: 'text', text: 'qq.png' },
        { type: 'text', text: 'qq.png' },
        { type: 'text', text: 'qq.png' },
        { type: 'text', text: 'qq.png' },
        { type: 'text', text: 'qq.png' },
        { type: 'text', text: 'qq.png' },
        { type: 'text', text: 'qq.png' },
      ]
    }
  }
}
</script>

<style lang='less'>

#panel_quick_reference {
  color: white;
  font-family: 'Open Sans';
  text-transform: uppercase;

  .content {
    margin: 0;
    text-transform: lowercase;
    padding-bottom: 3px;
    margin-bottom: 5px;
  }
}


</style>

<!-- REFERENCE

description

[________________] SEARCH

list of big objects

bo1       bo2
-----     -----
method1   method1
method2   method2
method3
method4
method5


-->

<!-- QUICK REFERENCE
___________________________ SEARCH

-->
