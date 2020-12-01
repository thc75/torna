<template>
  <div>
    <el-container>
      <el-aside width="200px">
        <ul class="module-menu el-menu">
          <li class="el-submenu is-active is-opened">
            <div class="el-submenu__title" style="padding-left: 20px;">
              <span slot="title">
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
                <el-tooltip effect="dark" content="同步Swagger文档" placement="top">
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
        <doc-info ref="docInfo" :project-id="projectId" :module-id="module.id" />
      </el-main>
    </el-container>
    <!-- 导入json -->
    <import-swagger-dialog ref="importSwaggerDlg" :project-id="projectId" :success="reload" />
  </div>
</template>
<style>
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
import DocInfo from '../DocInfo'
import ImportSwaggerDialog from '../ImportSwaggerDialog'

export default {
  name: 'Module',
  components: { DocInfo, ImportSwaggerDialog },
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
      refreshSwaggerLoading: false
    }
  },
  watch: {
    projectId(id) {
      this.loadModule(id)
    }
  },
  methods: {
    reload() {
      this.loadModule(this.projectId)
    },
    loadModule: function(projectId) {
      if (projectId) {
        this.get('/module/list', { projectId: projectId }, function(resp) {
          this.moduleData = resp.data
          if (this.moduleData.length > 0 && !this.module) {
            this.module = this.moduleData[0]
          }
        })
      }
    },
    isActive(item) {
      return this.module.id === item.id
    },
    onModuleSelect(item) {
      this.module = item
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
    }
  }
}
</script>
