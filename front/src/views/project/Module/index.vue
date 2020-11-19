<template>
  <div>
    <el-container>
      <el-aside width="200px">
        <el-menu
          :default-openeds="['0']"
          :default-active="`${moduleId}`"
          class="module-menu"
          @select="onModuleSelect"
        >
          <el-submenu index="0">
            <template slot="title">
              <span slot="title">
                模块列表
                <el-dropdown style="margin-bottom: 5px;float: right" @command="handleCommand">
                  <el-button type="text" size="mini" icon="el-icon-circle-plus-outline"></el-button>
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item icon="el-icon-box">新建模块</el-dropdown-item>
                    <el-dropdown-item icon="el-icon-download" divided :command="onImportSwagger">导入Swagger文档</el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </span>
            </template>
            <el-menu-item v-for="item in moduleData" :key="item.id" :index="`${item.id}`">
              <!--          <i class="el-icon-menu"></i>-->
              <span slot="title">{{ item.name }}</span>
            </el-menu-item>
          </el-submenu>
        </el-menu>
      </el-aside>
      <el-main>
        <doc-info ref="docInfo" :module-id="moduleId" />
      </el-main>
    </el-container>
    <!-- 导入json -->
    <import-swagger-dialog ref="importSwaggerDlg" :project-id="projectId" :success="reload" />
  </div>
</template>
<style>
.module-menu .el-submenu__icon-arrow { display: none }
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
          if (this.moduleData.length > 0) {
            this.moduleId = this.moduleData[0].id
          }
        })
      }
    },
    onModuleSelect(key) {
      this.moduleId = key
    },
    onImportSwagger() {
      this.$refs.importSwaggerDlg.show()
    }
  }
}
</script>
