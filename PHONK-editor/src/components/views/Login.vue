<template>
  <div id = "file_manager" class = "proto_panel">

    <h1> Login </h1>


    <form class="">
    <fieldset>

    <!-- info input -->
    <div class="form_group">
      <label for="password">Password</label>
      <input id="password" name="projectname" type="text" placeholder="placeholder" class="form-control input-md" required="">

      <button id="login" name="login" class="btn btn-success">Login</button>
    </div>

    </fieldset>
    </form>


  </div>
</template>

<script>
export default {
  name: 'Login',
  data () {
    return {
      msg: 'Hello World!'
    }
  }
}
</script>

<style lang='less'>
@import (reference) "../../assets/css/variables.less";

#login {

}

</style>
