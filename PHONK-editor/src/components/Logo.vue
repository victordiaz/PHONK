<template>
  <div>
  <h1 class = "logo">
    <img src = "/static/phonk_underlined.png" />
    <!-- phonk -->
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
@import (reference) "../assets/css/variables.less";

img {
  height: 22px;
  padding-top: 5px;
}

/* logo */
h1.logo {
  font-family: 'Roboto Condensed';
  font-size: 1em;
  text-shadow: 1px 1px 1px rgba(0, 0, 0, 0.1);
  font-weight: 100;
  margin: 0px;
  padding: 8px;

  user-select: none;
  cursor: default;
  // background: rgba(0, 0, 0, 0.1);

  color: @primaryTextColor;
  font-weight: 600;
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
