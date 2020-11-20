<template>
  <div>
    <el-container>
      <el-aside width="200px">
        <ul class="module-menu el-menu">
          <li class="el-submenu is-active is-opened">
            <div class="el-submenu__title" style="padding-left: 20px;">
              <span slot="title">
                模块列表
                <el-dropdown style="margin-bottom: 5px;float: right" @command="handleCommand">
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
                <span>{{ item.name }}</span>
              </li>
            </ul>
          </li>
        </ul>
      </el-aside>
      <el-main style="padding-top: 0">
        <doc-info ref="docInfo" :module-id="moduleId" />
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
      type: Number,
      default: 0
    }
  },
  data() {
    return {
      moduleId: 0,
      moduleData: []
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
      if (projectId > 0) {
        this.get('/project/doc/module/list', { projectId: projectId }, function(resp) {
          this.moduleData = resp.data
          if (this.moduleData.length > 0 && this.moduleId === 0) {
            this.moduleId = this.moduleData[0].id
          }
        })
      }
    },
    isActive(item) {
      return this.moduleId === item.id
    },
    onModuleSelect(item) {
      this.moduleId = item.id
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
        this.post('/project/module/add', data, function(resp) {
          this.tipSuccess('创建成功')
          this.moduleId = resp.data
          this.reload()
        })
      }).catch(() => {
      })
    },
    onImportSwagger() {
      this.$refs.importSwaggerDlg.show()
    }
  }
}
</script>
