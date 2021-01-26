<template>
  <div class="app-wrapper">
    <div class="main-container">
      <navbar />
      <el-container>
        <el-aside style="width: auto;">
          <admin-menu />
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
import AdminMenu from '@/components/AdminMenu'

export default {
  name: 'Layout',
  components: {
    Navbar,
    AppMain,
    AdminMenu
  },
  mixins: [ResizeMixin],
  mounted() {
    const projectId = this.$route.params.projectId
    this.initCurrentInfo(projectId)
  },
  methods: {
    initCurrentInfo(projectId) {
      let fromData = this.getFrom()
      if (!fromData) {
        this.get('/project/space', { projectId: projectId }, resp => {
          fromData = resp.data
          this.setTitle(fromData.projectName)
          this.setCurrentInfo({
            id: fromData.spaceId,
            name: fromData.spaceName
          }, {
            id: fromData.projectId,
            name: fromData.projectName
          })
        })
      } else {
        this.setTitle(fromData.projectName)
        this.setCurrentInfo({
          id: fromData.spaceId,
          name: fromData.spaceName
        }, {
          id: fromData.projectId,
          name: fromData.projectName
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
