<template>
  <div class="navbar">
    <el-breadcrumb v-if="showBreadcrumb" class="app-breadcrumb" separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: `/` }">{{ $ts('home') }}</el-breadcrumb-item>
      <el-breadcrumb-item v-if="currentSpace" :to="{ path: spaceRoute }">{{ currentSpace.name }}</el-breadcrumb-item>
      <el-breadcrumb-item v-if="currentProject">{{ currentProject.name }}</el-breadcrumb-item>
    </el-breadcrumb>
    <div class="right-menu">
      <div v-if="isSuperAdmin()" class="right-menu-item">
        <el-button type="primary" size="mini" @click="goRoute('/admin/users')">{{ $ts('adminManage') }}</el-button>
      </div>
      <div class="right-menu-item">
        <el-button type="success" size="mini" icon="el-icon-view" @click="goViewPage">{{ $ts('previewModel') }}</el-button>
      </div>
      <div class="right-menu-item">
        <el-tooltip placement="bottom" :content="$ts('helpCenter')">
          <el-button type="text" class="el-icon-question navbar-btn" @click="openLink('/help')" />
        </el-tooltip>
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
      return this.getCurrentProject()
    },
    currentSpace() {
      return this.getSpace()
    },
    spaceRoute() {
      const path = this.$route.path
      return path.indexOf('compose') > -1 ? `/space/compose/${this.currentSpace.id}` : `/space/project/${this.currentSpace.id}`
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
