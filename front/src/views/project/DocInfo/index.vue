<template>
  <el-tabs v-model="activeName" @tab-click="handleClick">
    <el-tab-pane name="DocList">
      <span slot="label"><i class="el-icon-s-grid"></i> {{ $ts('apiList') }}</span>
      <doc-table ref="moduleIdDocList" :project-id="projectId" />
    </el-tab-pane>
    <el-tab-pane name="EnumInfo">
      <span slot="label"><i class="el-icon-tickets"></i> {{ $ts('dictionaryManagement') }}</span>
      <enum-info ref="moduleIdEnumInfo" :project-id="projectId" />
    </el-tab-pane>
    <el-tab-pane v-if="hasRole(`project:${projectId}`, [Role.admin, Role.dev])" name="ModuleSetting">
      <span slot="label"><i class="el-icon-setting"></i> {{ $ts('moduleSetting') }}</span>
      <module-setting ref="moduleIdModuleSetting" :project-id="projectId" />
    </el-tab-pane>
    <el-tab-pane v-if="hasRole(`project:${projectId}`, [Role.admin, Role.dev])" name="OpenApi">
      <span slot="label"><i class="el-icon-collection-tag"></i> OpenAPI</span>
      <module-open-api ref="moduleIdOpenApi" />
    </el-tab-pane>
    <el-tab-pane v-if="hasRole(`project:${projectId}`, [Role.admin, Role.dev])" name="ShareConfig">
      <span slot="label"><i class="el-icon-share"></i> {{ $ts('shareManagement') }}</span>
      <share-config ref="moduleIdShareConfig" />
    </el-tab-pane>
  </el-tabs>
</template>

<script>
import DocTable from '../DocTable'
import ModuleSetting from '../ModuleSetting'
import ModuleOpenApi from '../ModuleOpenApi'
import EnumInfo from '../EnumInfo'
import ShareConfig from '../ShareConfig'

export default {
  name: 'DocInfo',
  components: { DocTable, ModuleSetting, ModuleOpenApi, EnumInfo, ShareConfig },
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
      activeName: '',
      isLoad: true
    }
  },
  watch: {
    moduleId(moduleId) {
      this.initActive()
      if (moduleId) {
        this.loadData(moduleId)
      }
    }
  },
  methods: {
    reload() {
      if (this.moduleId) {
        this.loadData(this.moduleId)
      }
    },
    initActive() {
      const query = this.$route.query
      const active = query.id
      this.activeName = active || 'DocList'
    },
    handleClick() {
      this.loadData(this.moduleId)
    },
    loadData(moduleId) {
      if (this.isLoad) {
        this.isLoad = false
        this.addInit(() => {
          this.doLoad(moduleId)
        })
      } else {
        this.doLoad(moduleId)
      }
    },
    doLoad(moduleId) {
      const ref = this.$refs[`moduleId${this.activeName}`]
      ref && ref.reload(moduleId)
    }
  }
}
</script>
