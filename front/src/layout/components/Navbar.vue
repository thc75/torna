<template>
  <div class="navbar">
    <router-link class="logo" to="/">
      <img src="@/assets/images/logo.png" class="sidebar-logo">
      <h1 class="sidebar-title">Torna</h1>
    </router-link>
    <el-breadcrumb class="app-breadcrumb" separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: `/` }">首页</el-breadcrumb-item>
      <el-breadcrumb-item v-if="currentSpace" :to="{ path: `/space/${currentSpace.id}` }">{{ currentSpace.name }}</el-breadcrumb-item>
      <el-breadcrumb-item v-if="currentProject">{{ currentProject.name }}</el-breadcrumb-item>
    </el-breadcrumb>
    <div class="right-menu">
      <div v-if="isSuperAdmin()" class="right-menu-item">
        <admin-menu />
      </div>
      <div class="right-menu-item">
        <el-button type="success" size="mini" icon="el-icon-view" @click="goViewPage">浏览模式</el-button>
      </div>
      <div class="right-menu-item">
        <router-link to="/help" target="_blank">
          <el-button type="text" class="el-icon-question navbar-btn" style="color: #5a5e66" />
        </router-link>
      </div>
      <div class="right-menu-item">
        <right-dropdown />
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import RightDropdown from '@/components/RightDropdown'
import AdminMenu from '@/components/AdminMenu'

export default {
  components: {
    RightDropdown, AdminMenu
  },
  data() {
    return {
      isShowDefault: false,
      spaceData: []
    }
  },
  computed: {
    ...mapGetters([
      'sidebar',
      'avatar'
    ]),
    currentProject() {
      return this.$store.state.settings.currentProject
    },
    currentSpace() {
      return this.$store.state.settings.currentSpace
    }
  },
  methods: {
    goViewPage() {
      this.goRoute('/view')
    }
  }
}
</script>

<style lang="scss" scoped>
.navbar-btn {
  font-size: 24px;
}
.logo {
  float: left;
  margin-left: 10px;
  margin-right: 20px;
  display: inline-block;
}
.sidebar-logo {
  width: 32px;
  height: 32px;
  vertical-align: middle;
  margin-right: 2px;
}
.sidebar-title {
  display: inline-block;
  margin: 0;
  color: #000;
  font-weight: 600;
  line-height: 50px;
  font-size: 14px;
  font-family: Avenir, Helvetica Neue, Arial, Helvetica, sans-serif;
  vertical-align: middle;
}
.app-breadcrumb.el-breadcrumb {
  float: left;
  display: inline-block;
  line-height: 50px;
  font-size: 14px;
  margin-left: 8px;

  .no-redirect {
    color: #97a8be;
    cursor: text;
  }
}
.el-dropdown-link {
  cursor: pointer;
}
.el-icon-arrow-down {
  font-size: 12px;
}
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
