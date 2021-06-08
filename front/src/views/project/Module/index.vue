<template>
  <div>
    <el-container>
      <el-aside :width="sidebarOpen ? '200px' : '40px'">
        <hamburger :is-active="sidebarOpen" :padding="0" class="hamburger-container" style="margin-bottom: 5px" @toggleClick="toggleSideBar" />
        <ul v-show="sidebarOpen" class="module-menu el-menu">
          <li class="el-submenu is-active is-opened">
            <div class="el-submenu__title" style="padding-left: 20px;">
              <span slot="title">
                {{ $ts('moduleList') }}
                <el-dropdown
                  v-if="hasRole(`project:${projectId}`, [Role.admin, Role.dev])"
                  trigger="click"
                  style="margin-bottom: 5px;float: right"
                  @command="handleCommand"
                >
                  <el-button type="text" size="mini" icon="el-icon-circle-plus-outline"></el-button>
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item icon="el-icon-box" :command="onModuleAdd">{{ $ts('newModule') }}</el-dropdown-item>
                    <el-dropdown-item icon="el-icon-download" divided :command="onImportSwagger">{{ $ts('importSwaggerDoc') }}</el-dropdown-item>
                    <el-dropdown-item icon="el-icon-download" :command="onImportPostman">{{ $ts('importPostmanDoc') }}</el-dropdown-item>
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
                <el-tooltip effect="dark" :content="$ts('syncSwaggerDoc')" placement="bottom">
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
        <ul v-show="!sidebarOpen" class="el-menu small-menu">
          <li class="small-menu-item">
            <el-dropdown
              v-if="hasRole(`project:${projectId}`, [Role.admin, Role.dev])"
              trigger="click"
              style="margin-bottom: 5px;"
              @command="handleCommand"
            >
              <el-button type="text" icon="el-icon-circle-plus-outline"></el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item icon="el-icon-box" :command="onModuleAdd">{{ $ts('newModule') }}</el-dropdown-item>
                <el-dropdown-item icon="el-icon-download" divided :command="onImportSwagger">{{ $ts('importSwaggerDoc') }}</el-dropdown-item>
                <el-dropdown-item icon="el-icon-download" :command="onImportPostman">{{ $ts('importPostmanDoc') }}</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </li>
          <li
            v-for="item in moduleData"
            :key="item.id"
            class="small-menu-item"
            :class="{'is-active': isActive(item)}"
            @click="onModuleSelect(item)"
          >
            <el-tooltip placement="right" :content="item.name">
              <div>{{ item.name.substring(0, 1).toUpperCase() }}</div>
            </el-tooltip>
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

.small-menu {
  .small-menu-item {
    list-style: none;
    cursor: pointer;
    padding: 10px;
    width: 40px;
    height: 40px;
  }
  .is-active {
    outline: 0;
    background-color: #ecf5ff;
    color: #409EFF;
  }
  button {
    color: #909399;
    padding: 0;
    font-size: 16px;
  }
}
</style>
<script>
import Hamburger from '@/components/Hamburger'
import DocInfo from '../DocInfo'
import ImportSwaggerDialog from '../ImportSwaggerDialog'
import ImportPostmanDialog from '@/views/project/ImportPostmanDialog/index'

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
      this.setProjectConfig(this.projectId, { sidebar: open })
    },
    initSidebarStatus(projectId) {
      const opened = this.getProjectConfig(projectId).sidebar || false
      this.setSidebarStatus(opened)
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
    setCurrentModule(item) {
      this.module = item
      this.setProjectConfig(this.projectId, { moduleId: item.id })
    },
    getCacheModuleId() {
      return this.getProjectConfig(this.projectId).moduleId
    },
    onModuleAdd() {
      this.$prompt(this.$ts('inputModuleName'), this.$ts('newModule'), {
        confirmButtonText: this.$ts('ok'),
        cancelButtonText: this.$ts('cancel'),
        inputPattern: /^.{1,64}$/,
        inputErrorMessage: this.$ts('notEmptyLengthLimit', 64)
      }).then(({ value }) => {
        const data = {
          name: value,
          projectId: this.projectId
        }
        this.post('/module/add', data, resp => {
          this.tipSuccess(this.$ts('createSuccess'))
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
        this.tipSuccess(this.$ts('syncSuccess'))
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
