<template>
  <div>
    <svg width="60" height="30" viewBox="0 0 55 20" fill="none" xmlns="http://www.w3.org/2000/svg">
      <path d="M3.7793 14.3066H5.68652C6.38379 14.3066 7.01367 14.207 7.57617 14.0078C8.13867 13.8086 8.61914 13.5303 9.01758 13.1729C9.41016 12.8213 9.71191 12.4023 9.92285 11.916C10.1396 11.4297 10.248 10.8994 10.248 10.3252C10.248 9.70996 10.1396 9.15039 9.92285 8.64648C9.71191 8.14258 9.41016 7.70898 9.01758 7.3457C8.61914 6.98828 8.13867 6.70996 7.57617 6.51074C7.01367 6.30566 6.38379 6.20312 5.68652 6.20312H1.30957V19H3.7793V14.3066ZM3.7793 12.3027V8.20703H5.68652C6.03809 8.20703 6.34277 8.2627 6.60059 8.37402C6.8584 8.48535 7.0752 8.6377 7.25098 8.83105C7.4209 9.02441 7.54688 9.25 7.62891 9.50781C7.7168 9.76562 7.76074 10.0439 7.76074 10.3428C7.76074 10.6006 7.7168 10.8496 7.62891 11.0898C7.54688 11.3242 7.4209 11.5322 7.25098 11.7139C7.0752 11.8955 6.8584 12.0391 6.60059 12.1445C6.34277 12.25 6.03809 12.3027 5.68652 12.3027H3.7793ZM20.4082 19V6.20312H17.9648V11.4502H14.3438V6.20312H11.8916V19H14.3438V13.4541H17.9648V19H20.4082ZM42.0205 19V6.20312H39.542L39.5332 14.4209L36 6.20312H33.5127V19H36V10.7471L39.5596 19H42.0205ZM48.208 13.7441L51.3721 19H54.3076L49.8428 11.8633L54.1494 6.20312H51.126L48.0146 10.501L46.8545 12.0654V6.20312H44.376V19H46.8545V15.2295L48.208 13.7441Z" fill="white"/>
      <rect id="rect_phone" :class="{'rect_phone_slide': b}" x="23.75" y="3.25" width="6.5" height="10.5" stroke="#FFC700" stroke-width="2.5"/>
    </svg>
  </div>
</template>

<script>
import Store from '../../Store'

export default {
  name: 'PhonkLogo',
  data () {
    return {
      b: false
    }
  },
  methods: {
    toggle_load_project: function () {
      this.b = !this.b
    }
  },
  created () {
    Store.on('toggle_load_project', this.toggle_load_project)
  },
  destroyed () {
    Store.removeListener('toggle_load_project', this.toggle_load_project)
  }
}
</script>

<style lang='less' scoped>
@import (reference) '../../assets/css/variables.less';

svg {
  margin-left: 8px;
}

rect {
  transition: transform 0.3s;
}
.rect_phone_slide {
  transform: translate3d(0px, 8px, 0px);
}
</style>
