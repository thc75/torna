<template>
  <div>
    <el-container>
      <el-aside :width="sidebarOpen ? '200px' : '40px'">
        <hamburger :is-active="sidebarOpen" class="hamburger-container" @toggleClick="toggleSideBar" />
        <ul v-show="sidebarOpen" class="module-menu el-menu">
          <li class="el-submenu is-active is-opened">
            <div class="el-submenu__title" style="padding-left: 20px;">
              <span slot="title">
                <i class="el-icon-box"></i>
                模块列表
                <el-dropdown
                  v-if="hasRole(`project:${projectId}`, [Role.admin, Role.dev])"
                  trigger="click"
                  style="margin-bottom: 5px;float: right"
                  @command="handleCommand"
                >
                  <el-button type="text" size="mini" icon="el-icon-circle-plus-outline"></el-button>
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item icon="el-icon-box" :command="onModuleAdd">新建模块</el-dropdown-item>
                    <el-dropdown-item icon="el-icon-download" divided :command="onImportSwagger">导入Swagger文档</el-dropdown-item>
                    <el-dropdown-item icon="el-icon-download" :command="onImportPostman">导入Postman文档</el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </span>
            </div>
            <ul role="menu" class="el-menu el-menu--inline">
              <li
                v-for="item in moduleData"
                :key="item.id"
                :class="{'is-active': isActive(item)}"
                class="el-menu-item"
                style="padding-left: 40px;"
                @click="onModuleSelect(item)"
              >
                <span>
                  {{ item.name }}
                </span>
                <el-tooltip effect="dark" content="同步Swagger文档" placement="bottom">
                  <el-button
                    v-if="hasRole(`project:${projectId}`, [Role.admin, Role.dev])"
                    v-show="module.id === item.id && item.type === 1"
                    :loading="refreshSwaggerLoading"
                    type="text"
                    icon="el-icon-refresh"
                    @click.stop="onRefreshSwagger(item)"
                  />
                </el-tooltip>
              </li>
            </ul>
          </li>
        </ul>
      </el-aside>
      <el-main style="padding-top: 0">
        <doc-info v-show="module" ref="docInfo" :project-id="projectId" :module-id="module.id" />
      </el-main>
    </el-container>
    <!-- 导入json -->
    <import-swagger-dialog ref="importSwaggerDlg" :project-id="projectId" :success="reload" />
    <import-postman-dialog ref="importPostmanDlg" :project-id="projectId" :success="reload" />
  </div>
</template>
<style lang="scss">
.hamburger-container {
  cursor: pointer;
}
.module-menu .el-submenu__title {
  height: 36px;
  line-height: 36px;
}
.module-menu .el-menu button {
  position: absolute;
  right: 10px;
  padding-top: 8px;
  padding-bottom: 0;
}
.module-menu .el-menu .is-active {
  outline: 0;
  background-color: #ecf5ff;
}
.module-menu .el-menu-item {
  height: 36px;
  line-height: 36px;
}
</style>
<script>
import Hamburger from '@/components/Hamburger'
import DocInfo from '../DocInfo'
import ImportSwaggerDialog from '../ImportSwaggerDialog'
import ImportPostmanDialog from '@/views/project/ImportPostmanDialog/index'

const current_module_key = 'torna-module-'
const sidebar_key = 'torna-projectsidebar-'

export default {
  name: 'Module',
  components: { ImportPostmanDialog, Hamburger, DocInfo, ImportSwaggerDialog },
  props: {
    projectId: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      module: '',
      moduleData: [],
      sidebarOpen: true,
      refreshSwaggerLoading: false
    }
  },
  watch: {
    projectId(id) {
      this.loadModule(id)
    }
  },
  methods: {
    reload(resp) {
      const moduleId = resp && resp.data.id
      this.loadModule(this.projectId, moduleId)
    },
    toggleSideBar() {
      this.setSidebarStatus(!this.sidebarOpen)
    },
    setSidebarStatus(open) {
      this.sidebarOpen = open
      this.setAttr(`${sidebar_key}${this.projectId}`, open)
    },
    initSidebarStatus(projectId) {
      const opened = this.getAttr(`${sidebar_key}${projectId}`)
      const sidebarOpen = opened ? opened === 'true' : true
      this.setSidebarStatus(sidebarOpen)
    },
    loadModule: function(projectId, moduleId) {
      if (projectId) {
        this.initSidebarStatus(projectId)
        this.get('/module/list', { projectId: projectId }, function(resp) {
          this.moduleData = resp.data
          const cacheModuleId = moduleId || this.getCacheModuleId()
          let currentModule
          if (cacheModuleId) {
            for (const module of this.moduleData) {
              if (module.id === cacheModuleId) {
                currentModule = module
                break
              }
            }
          }
          if (!currentModule && this.moduleData.length > 0) {
            currentModule = this.moduleData[0]
          }
          if (currentModule) {
            this.setCurrentModule(currentModule)
          }
        })
      }
    },
    isActive(item) {
      return this.module.id === item.id
    },
    onModuleSelect(item) {
      this.setCurrentModule(item)
    },
    getCurrentModuleKey() {
      return current_module_key + this.projectId
    },
    setCurrentModule(item) {
      this.module = item
      this.setAttr(this.getCurrentModuleKey(), item.id)
    },
    getCacheModuleId() {
      return this.getAttr(this.getCurrentModuleKey())
    },
    onModuleAdd() {
      this.$prompt('请输入模块名称', '新建模块', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /^.{1,64}$/,
        inputErrorMessage: '不能为空且长度在64以内'
      }).then(({ value }) => {
        const data = {
          name: value,
          projectId: this.projectId
        }
        this.post('/module/add', data, resp => {
          this.tipSuccess('创建成功')
          this.module = resp.data
          this.reload()
        })
      }).catch(() => {
      })
    },
    onRefreshSwagger(item) {
      this.refreshSwaggerLoading = true
      this.get('/module/refresh/swagger', { moduleId: item.id }, () => {
        this.refreshSwaggerLoading = false
        this.tipSuccess('同步成功')
        this.$refs.docInfo.reload()
      }, () => {
        this.refreshSwaggerLoading = false
      })
    },
    onImportSwagger() {
      this.$refs.importSwaggerDlg.show()
    },
    onImportPostman() {
      this.$refs.importPostmanDlg.show()
    }
  }
}
</script>
