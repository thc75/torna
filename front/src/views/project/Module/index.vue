<template>
  <div style="padding: 10px;">
    <el-empty v-if="moduleData.length === 0" :description="$ts('noAppDescription')">
      <el-dropdown
        trigger="hover"
        @command="handleCommand"
      >
        <el-button type="primary" icon="el-icon-circle-plus" style="padding: 10px; font-size: 16px"></el-button>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item icon="el-icon-box" :command="onModuleAdd">{{ $ts('newModule') }}</el-dropdown-item>
          <el-dropdown-item icon="el-icon-download" divided :command="onImportSwagger">{{ $ts('importSwaggerDoc') }}</el-dropdown-item>
          <el-dropdown-item icon="el-icon-download" :command="onImportPostman">{{ $ts('importPostmanDoc') }}</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </el-empty>
    <el-tabs
      v-if="moduleData.length > 0"
      v-model="active"
      type="border-card"
      :before-leave="beforeLeave"
      class="module-tabs"
      @tab-click="onTabClick"
    >
      <el-tab-pane
        v-for="item in moduleData"
        :key="item.id"
        :label="item.name"
        :name="item.name"
        :title="item.name"
      >
        <span slot="label">
          {{ item.name }}
          <el-tooltip effect="dark" :content="$ts('syncSwaggerDoc')" placement="top">
            <el-button
              v-if="hasRole(`project:${projectId}`, [Role.admin, Role.dev])"
              v-show="module.id === item.id && item.type === 1"
              :loading="refreshSwaggerLoading"
              type="text"
              icon="el-icon-refresh el-icon--right"
              @click.stop="onRefreshSwagger(item)"
            />
          </el-tooltip>
        </span>
      </el-tab-pane>
      <!-- 新增应用 -->
      <el-tab-pane v-if="hasRole(`project:${projectId}`, [Role.admin, Role.dev])" name="_new_">
        <span slot="label">
          <el-dropdown
            trigger="hover"
            @command="handleCommand"
          >
            <el-button type="text" icon="el-icon-circle-plus" style="padding: 10px; color: #303133;font-size: 16px"></el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item icon="el-icon-box" :command="onModuleAdd">{{ $ts('newModule') }}</el-dropdown-item>
              <el-dropdown-item icon="el-icon-download" divided :command="onImportSwagger">{{ $ts('importSwaggerDoc') }}</el-dropdown-item>
              <el-dropdown-item icon="el-icon-download" :command="onImportPostman">{{ $ts('importPostmanDoc') }}</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </span>
      </el-tab-pane>
    </el-tabs>
    <doc-info v-show="module" ref="docInfo" :project-id="projectId" :module-id="module.id" />
    <!-- 导入json -->
    <import-swagger-dialog ref="importSwaggerDlg" :project-id="projectId" :success="reload" />
    <import-postman-dialog ref="importPostmanDlg" :project-id="projectId" :success="reload" />
  </div>
</template>
<style lang="scss">
.module-tabs {
  background: #FFF;
  border: 1px solid #DCDFE6;
  border-bottom: 0;
  -webkit-box-shadow: none;
  box-shadow: none;
  margin-bottom: 10px;

  .el-tabs__content {
    padding: 0;
  }
  // tabs换行显示
  .el-tabs__nav {
    white-space: normal;
  }
}
</style>
<script>
$addI18n({
  'noAppDescription': { 'zh': '当前没有应用', 'en': 'No application' }
})
import DocInfo from '../DocInfo'
import ImportSwaggerDialog from '../ImportSwaggerDialog'
import ImportPostmanDialog from '@/views/project/ImportPostmanDialog/index'

export default {
  name: 'Module',
  components: { ImportPostmanDialog, DocInfo, ImportSwaggerDialog },
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
      sidebarOpen: false,
      refreshSwaggerLoading: false,
      beforeLeave: function(activeName, oldActiveName) {
        return activeName !== '_new_'
      },
      active: ''
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
      this.active = item.name
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
      const loading = this.$loading({
        lock: true,
        text: $ts('synchronizing'),
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      })
      this.get('/module/refresh/swaggerV2', { moduleId: item.id }, () => {
        setTimeout(() => {
          loading.close()
          this.tipSuccess(this.$ts('syncSuccess'))
          this.$refs.docInfo.reload()
        }, 1500)
      }, () => loading.close())
    },
    onImportSwagger() {
      this.$refs.importSwaggerDlg.show()
    },
    onImportPostman() {
      this.$refs.importPostmanDlg.show()
    },
    onTabClick(tab) {
      const label = tab.label
      for (const module of this.moduleData) {
        if (label === module.name) {
          this.onModuleSelect(module)
          break
        }
      }
    }
  }
}
</script>
