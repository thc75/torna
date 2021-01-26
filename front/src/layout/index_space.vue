<template>
  <div class="app-wrapper">
    <div class="main-container">
      <navbar />
      <el-container>
        <el-aside style="width: auto;">
          <space-menu />
        </el-aside>
        <el-main style="padding: 0">
          <app-main style="min-height: 100%;" />
        </el-main>
      </el-container>
    </div>
  </div>
</template>

<script>
import { Navbar, AppMain } from './components'
import ResizeMixin from './mixin/ResizeHandler'
import SpaceMenu from '@/components/SpaceMenu'

export default {
  name: 'Layout',
  components: {
    Navbar,
    AppMain,
    SpaceMenu
  },
  mixins: [ResizeMixin],
  data() {
    return {
      space: ''
    }
  },
  created() {
    const spaceId = this.$route.params.spaceId
    this.loadData(spaceId)
  },
  methods: {
    loadData(spaceId) {
      if (!this.space) {
        this.get('/space/info', { spaceId: spaceId }, resp => {
          this.space = resp.data
          this.setCurrentInfo(this.space, '')
        })
      }
    }
  }
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
