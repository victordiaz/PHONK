<template>
  <div>
  <h1 class = "logo">
    phonk!
    <!--
    <span class="accent">&gt;</span>Proto<span class="bolder">coder<span></span></span><span class="accent">_ </span>
    -->
  </h1>
  </div>
</template>

<script>
export default {
  name: 'Logo'
}
</script>

<style lang='less' scoped>
@import "../assets/css/variables.less";

img {
  width: 52px;
  height: 52px;
}

/* logo */
h1.logo {
  font-family: 'Source Code Pro';
  font-size: 1em;
  text-shadow: 1px 1px 1px rgba(0, 0, 0, 0.1);
  font-weight: 100;
  margin: 0px;
  padding: 8px;

  user-select: none;
  cursor: default;
  // background: rgba(0, 0, 0, 0.1);

  color: @primaryTextColor;
  font-weight: 800;
  text-align: center;

  .accent {
    color: @accentColor;
    font-weight: 500;
  }

  .bolder {
    font-weight: 500;
  }
}

</style>
