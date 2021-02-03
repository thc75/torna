<template>
  <div class="app-wrapper">
    <user-center-menu />
    <div class="main-container">
      <div :class="{'fixed-header':fixedHeader}">
        <navbar :show-breadcrumb="false" />
      </div>
      <app-main />
    </div>
  </div>
</template>

<script>
import { Navbar, AppMain, UserCenterMenu } from './components'
import ResizeMixin from './mixin/ResizeHandler'

export default {
  name: 'Layout',
  components: {
    Navbar,
    AppMain,
    UserCenterMenu
  },
  mixins: [ResizeMixin],
  computed: {
    fixedHeader() {
      return this.$store.state.settings.fixedHeader
    }
  }
}
</script>
<style lang="scss" scoped>
@import "~@/styles/variables.scss";
.main-container {
  margin-left: $sideBarUserWidth !important;
}
.fixed-header {
  width: calc(100% - #{$sideBarUserWidth});
}
</style>
