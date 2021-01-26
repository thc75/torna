<template>
  <div class="app-wrapper">
    <navbar />
    <el-container style="margin: 20px; border: 1px solid #eee;">
      <el-aside width="200px">
        <user-center-menu />
      </el-aside>
      <el-main>
        <app-main style="min-height: 100%;" />
      </el-main>
    </el-container>
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
  mixins: [ResizeMixin]
}
</script>

<style lang="scss" scoped>
  @import "~@/styles/mixin.scss";
  @import "~@/styles/variables.scss";

  .app-wrapper {
    @include clearfix;
    position: relative;
    height: 100%;
    width: 100%;
    &.mobile.openSidebar{
      position: fixed;
      top: 0;
    }
  }
</style>
