<template>
  <div :class="{'has-logo':showLogo}">
    <logo v-if="showLogo" :collapse="isCollapse" />
    <el-scrollbar wrap-class="scrollbar-wrapper">
      <el-menu
        class="menu-wrapper"
        :default-active="activeMenu"
        :collapse="isCollapse"
        :background-color="variables.menuBg"
        :text-color="variables.menuText"
        :default-openeds="opened"
        :unique-opened="false"
        :active-text-color="variables.menuActiveText"
        :collapse-transition="false"
        mode="vertical"
      >
        <!-- 只有一个子节点 -->
        <router-link to="/dashboard">
          <el-menu-item index="/dashboard" class="home-page">
            <i class="el-icon-s-home"></i>
            <span slot="title">空间首页</span>
          </el-menu-item>
        </router-link>
        <el-submenu index="/project">
          <template slot="title">
            <i class="el-icon-s-management"></i>
            <span>项目列表</span>
          </template>
          <div v-for="(item) in projects" :key="item.id">
            <router-link
              v-if="isShowProject(item)"
              :to="`/project/info/${item.id}`"
            >
              <el-menu-item :index="`/project/info/${item.id}`">
                <el-tooltip effect="light" placement="top" content="私有项目">
                  <i v-if="item.isPrivate" class="el-icon-lock private"></i>
                </el-tooltip>
                {{ item.name }}
              </el-menu-item>
            </router-link>
          </div>
        </el-submenu>
        <el-submenu v-if="isAdmin()" index="/admin">
          <template slot="title">
            <i class="el-icon-s-platform"></i>
            <span>后台管理</span>
          </template>
          <router-link to="/admin/openuser">
            <el-menu-item index="/admin/openuser">
              开放用户管理
            </el-menu-item>
          </router-link>
        </el-submenu>
      </el-menu>
    </el-scrollbar>
  </div>
</template>
<style scoped>
.el-menu--collapse .home-page {
  margin-left: -8px;
}
.el-menu-item .private {
  color: #ccc;
  float: right;
  position: absolute;
  top: 50%;
  right: 20px;
  margin-top: -7px;
}
</style>
<script>
import { mapGetters } from 'vuex'
import Logo from './Logo'
import variables from '@/styles/variables.scss'

export default {
  components: { Logo },
  data() {
    return {
      keyId: 0
    }
  },
  computed: {
    ...mapGetters([
      'sidebar'
    ]),
    projects() {
      return this.$store.state.settings.projects
    },
    projectChange() {
      return this.$store.state.event.projectChange
    },
    spaceId() {
      return this.$store.state.settings.spaceId
    },
    opened() {
      return ['/project']
    },
    activeMenu() {
      const route = this.$route
      const { meta, path } = route
      // if set path, the sidebar will highlight the path you set
      if (meta.activeMenu) {
        return meta.activeMenu
      }
      return path
    },
    showLogo() {
      return this.$store.state.settings.sidebarLogo
    },
    variables() {
      return variables
    },
    isCollapse() {
      return !this.sidebar.opened
    }
  },
  watch: {
    spaceId(newVal, oldVal) {
      this.loadMenu(newVal)
    },
    projectChange() {
      this.loadMenu(this.spaceId)
    }
  },
  methods: {
    /**
     * 是否显示项目
     * @param item
     * @returns {boolean}
     */
    isShowProject(item) {
      // 如果是公开项目，在空间内的成员都能访问
      if (!item.isPrivate) {
        return true
      }
      // 私有项目只有加入到项目中，才能访问
      const Roles = this.Roles
      return this.hasRole(`project:${item.id}`, [Roles.guest, Roles.dev, Roles.admin])
    },
    loadMenu(spaceId) {
      if (spaceId) {
        this.get('/space/project/list', { spaceId: spaceId }, resp => {
          this.$store.state.settings.projects = resp.data
        })
      }
    }
  }
}
</script>
