<template>
  <div class="navbar">
    <hamburger :is-active="sidebarView.opened" class="hamburger-container" @toggleClick="toggleSideBar" />

    <div class="right-menu">
      <div class="right-menu-item">
        <el-button type="primary" size="mini" icon="el-icon-monitor" @click="goAdminPage">管理模式</el-button>
      </div>
      <div class="right-menu-item">
        <right-dropdown />
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Hamburger from '@/components/Hamburger'
import RightDropdown from '@/components/RightDropdown'

export default {
  components: {
    Hamburger, RightDropdown
  },
  computed: {
    ...mapGetters([
      'sidebarView',
      'avatar'
    ]),
    moduleId() {
      return this.$store.state.settings.moduleId
    }
  },
  methods: {
    toggleSideBar() {
      this.$store.dispatch('app/toggleSideBarView')
    },
    goAdminPage() {
      const uri = this.moduleId ? `/project/info/${this.moduleId}` : '/'
      this.goRoute(uri)
    }
  }
}
</script>

<style lang="scss" scoped>
.navbar {
  height: 50px;
  overflow: hidden;
  position: relative;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);

  .hamburger-container {
    line-height: 46px;
    height: 100%;
    float: left;
    cursor: pointer;
    transition: background .3s;
    -webkit-tap-highlight-color:transparent;

    &:hover {
      background: rgba(0, 0, 0, .025)
    }
  }

  .breadcrumb-container {
    float: left;
  }

}
</style>
