<template>
  <div :class="classObj" class="app-wrapper">
    <sidebar id="leftPanel" class="sidebar-container-view" />
    <div id="rightPanel" :class="{hasDocViewTabs:docViewTabs}" class="main-container-view">
      <div id="resizeBar" class="resize-bar"></div>
      <div id="navBar" :class="{'fixed-header':fixedHeader}">
        <navbar />
        <tabs-router v-show="docViewTabShow" v-if="docViewTabSwitch" />
      </div>
      <view-main />
    </div>
  </div>
</template>

<script>
import { Navbar, Sidebar, ViewMain } from './components'
import TabsRouter from '@/components/TabsRouter'
import ResizeMixin from './mixin/ResizeHandler'
import { ResizeBar } from '@/utils/resizebar'

export default {
  name: 'LayoutView',
  components: {
    Navbar,
    Sidebar,
    ViewMain,
    TabsRouter
  },
  mixins: [ResizeMixin],
  computed: {
    sidebarView() {
      return this.$store.state.app.sidebarView
    },
    device() {
      return this.$store.state.app.device
    },
    fixedHeader() {
      return this.$store.state.settings.fixedHeader
    },
    classObj() {
      return {
        hideSidebarView: !this.sidebarView.opened,
        openSidebarView: this.sidebarView.opened,
        withoutAnimation: this.sidebarView.withoutAnimation,
        mobile: this.device === 'mobile'
      }
    },
    docViewTabShow() {
      return this.$store.state.tabsRouter.showTabsView
    },
    docViewTabSwitch() {
      return this.$store.state.settings.docViewTabSwitch
    },
    docViewTabs() {
      return this.docViewTabSwitch && this.docViewTabShow
    }
  },
  mounted() {
    this.ResizeBar = new ResizeBar(this, {
      leftPanel: 'leftPanel',
      rightPanel: 'rightPanel',
      resizeBar: 'resizeBar',
      navBar: 'navBar'
    })
  },
  destroyed() {
    this.ResizeBar.destroyed()
  },
  methods: {
    handleClickOutside() {
      this.$store.dispatch('app/closeSideBarView', { withoutAnimation: false })
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
  .drawer-bg {
    background: #000;
    opacity: 0.3;
    width: 100%;
    top: 0;
    height: 100%;
    position: absolute;
    z-index: 999;
  }

  .hideSidebar .fixed-header {
    width: 0
  }

  .hideSidebarView .fixed-header {
    width: 100%;
  }

  .mobile .fixed-header {
    width: 100%;
  }
</style>
