<template>
  <div :class="classObj" class="app-wrapper">
    <div v-if="device==='mobile'&&sidebarView.opened" class="drawer-bg" @click="handleClickOutside" />
    <sidebar id="leftPanelView" class="sidebar-container-view" />
    <div id="rightPanelView" class="main-container-view">
      <div id="resizeBarView" class="resize-bar"></div>
      <div id="navBarView" :class="{'fixed-header':fixedHeader}">
        <navbar />
      </div>
      <view-main />
    </div>
  </div>
</template>

<script>
import { Navbar, Sidebar, ViewMain } from './components'
import ResizeMixin from './mixin/ResizeHandler'
import { ResizeBar } from '@/utils/resizebar'

export default {
  name: 'LayoutView',
  components: {
    Navbar,
    Sidebar,
    ViewMain
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
    }
  },
  mounted() {
    new ResizeBar(this, {
      leftPanel: 'leftPanelView',
      rightPanel: 'rightPanelView',
      resizeBar: 'resizeBarView',
      navBar: 'navBarView'
    })
  },
  destroyed() {
    document.body.style.overflowY = 'auto'
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

  .fixed-header {
    position: fixed;
    top: 0;
    right: 0;
    z-index: 9;
    width: calc(100% - #{$sideBarViewWidth});
    transition: width 0.28s;
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
