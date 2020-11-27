<template>
  <el-tabs v-model="activeName" @tab-click="handleClick">
    <el-tab-pane label="接口列表" name="DocList">
      <doc-table ref="docTable" :project-id="projectId" :module-id="moduleIdDocList" />
    </el-tab-pane>
    <el-tab-pane label="模块配置" name="ModuleSetting">
      <module-setting :project-id="projectId" :module-id="moduleIdModuleSetting" />
    </el-tab-pane>
  </el-tabs>
</template>

<script>
import DocTable from '../DocTable'
import ModuleSetting from '../ModuleSetting'

export default {
  name: 'DocInfo',
  components: { DocTable, ModuleSetting },
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
      moduleIdModuleSetting: ''
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
