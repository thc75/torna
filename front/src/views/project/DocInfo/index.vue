<template>
  <el-tabs v-model="activeName" @tab-click="handleClick">
    <el-tab-pane name="DocList">
      <span slot="label"><i class="el-icon-s-grid"></i> 接口列表</span>
      <doc-table ref="docTable" :project-id="projectId" :module-id="moduleIdDocList" />
    </el-tab-pane>
    <el-tab-pane name="EnumInfo">
      <span slot="label"><i class="el-icon-tickets"></i> 字典管理</span>
      <enum-info :project-id="projectId" :module-id="moduleIdEnumInfo" />
    </el-tab-pane>
    <el-tab-pane name="ModuleSetting">
      <span slot="label"><i class="el-icon-setting"></i> 模块配置</span>
      <module-setting :project-id="projectId" :module-id="moduleIdModuleSetting" />
    </el-tab-pane>
    <el-tab-pane v-if="hasRole(`project:${projectId}`, [Role.admin, Role.dev])" name="OpenApi">
      <span slot="label"><i class="el-icon-collection-tag"></i> OpenAPI</span>
      <module-open-api :module-id="moduleIdOpenApi" />
    </el-tab-pane>
  </el-tabs>
</template>

<script>
import DocTable from '../DocTable'
import ModuleSetting from '../ModuleSetting'
import ModuleOpenApi from '../ModuleOpenApi'
import EnumInfo from '../EnumInfo'

export default {
  name: 'DocInfo',
  components: { DocTable, ModuleSetting, ModuleOpenApi, EnumInfo },
  props: {
    moduleId: {
      type: String,
      default: ''
    },
    projectId: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      activeName: 'DocList',
      moduleIdDocList: '',
      moduleIdEnumInfo: '',
      moduleIdModuleSetting: '',
      moduleIdOpenApi: ''
    }
  },
  watch: {
    moduleId(moduleId) {
      this.loadData(moduleId)
    }
  },
  methods: {
    reload() {
      this.activeName = 'DocList'
      this.$refs.docTable.reload()
    },
    handleClick() {
      this.loadData(this.moduleId)
    },
    loadData(moduleId) {
      this[`moduleId${this.activeName}`] = moduleId
    }
  }
}
</script>
