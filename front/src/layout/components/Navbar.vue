<template>
  <div class="navbar">
    <el-breadcrumb v-if="showBreadcrumb" class="app-breadcrumb" separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: `/` }">首页</el-breadcrumb-item>
      <el-breadcrumb-item v-if="currentSpace" :to="{ path: `/space/project/${currentSpace.id}` }">{{ currentSpace.name }}</el-breadcrumb-item>
      <el-breadcrumb-item v-if="currentProject">{{ currentProject.name }}</el-breadcrumb-item>
    </el-breadcrumb>
    <div class="right-menu">
      <div v-if="isSuperAdmin()" class="right-menu-item">
        <el-button type="primary" size="mini" @click="goRoute('/admin/users')">后台管理</el-button>
      </div>
      <div class="right-menu-item">
        <el-button type="success" size="mini" icon="el-icon-view" @click="goViewPage">浏览模式</el-button>
      </div>
      <div class="right-menu-item">
        <router-link to="/help" target="_blank">
          <el-tooltip placement="bottom" content="帮助文档">
            <el-button type="text" class="el-icon-question navbar-btn" style="color: #5a5e66" />
          </el-tooltip>
        </router-link>
      </div>
      <div class="right-menu-item">
        <user-message />
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
import UserMessage from '@/components/UserMessage'

export default {
  components: {
    RightDropdown, UserMessage
  },
  props: {
    showBreadcrumb: {
      type: Boolean,
      default: true
    }
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
  created() {
    this.initPerm()
  },
  methods: {
    goViewPage() {
      this.goRoute('/view')
    }
  }
}
</script>

<style lang="scss" scoped>

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
