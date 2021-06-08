<template>
  <div>
    <h3>
      {{ moduleVO.name }}
      <popover-update
        v-if="hasRole(`project:${projectId}`, [Role.admin, Role.dev])"
        :value="moduleVO.name"
        :on-show="() => {return moduleVO.name}"
        :on-save="onSaveName"
      />
      <div v-if="hasRole(`project:${projectId}`, [Role.admin])" style="float: right">
        <el-button type="danger" size="mini" @click="onModuleDelete">{{ $ts('deleteModule') }}</el-button>
      </div>
    </h3>
    <el-tabs v-model="activeName" type="border-card" @tab-click="onTabClick">
      <el-tab-pane :label="$ts('commonHeader')" name="globalHeaders">
        <global-headers ref="globalHeaders" />
      </el-tab-pane>
      <el-tab-pane :label="$ts('commonRequest')" name="globalParams">
        <global-params ref="globalParams" />
      </el-tab-pane>
      <el-tab-pane :label="$ts('commonResponse')" name="globalReturns">
        <global-returns ref="globalReturns" />
      </el-tab-pane>
      <el-tab-pane :label="$ts('debugEnv')" name="debugEnv">
        <debug-env ref="debugEnv" />
      </el-tab-pane>
      <el-tab-pane v-if="moduleVO.type === 1" :label="$ts('swaggerSetting')" name="swaggerSetting">
        <swagger-setting ref="swaggerSetting" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script>
import PopoverUpdate from '@/components/PopoverUpdate'
import GlobalHeaders from '@/components/ModuleSetting/GlobalHeaders'
import GlobalParams from '@/components/ModuleSetting/GlobalParams'
import GlobalReturns from '@/components/ModuleSetting/GlobalReturns'
import DebugEnv from '@/components/ModuleSetting/DebugEnv'
import SwaggerSetting from '@/components/ModuleSetting/SwaggerSetting'

export default {
  name: 'ModuleSetting',
  components: { PopoverUpdate, GlobalHeaders, GlobalParams, GlobalReturns, DebugEnv, SwaggerSetting },
  props: {
    projectId: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      activeName: 'globalHeaders',
      moduleId: '',
      moduleVO: {
        id: '',
        name: '',
        type: 0
      }
    }
  },
  methods: {
    reload(moduleId) {
      if (moduleId) {
        this.moduleId = moduleId
      }
      this.loadModuleInfo(this.moduleId)
      this.activeName = 'globalHeaders'
      this.$refs.globalHeaders.reload(this.moduleId)
    },
    onTabClick(tab) {
      this.$refs[tab.name].reload(this.moduleId)
    },
    loadModuleInfo(moduleId) {
      this.get('/module/info', { moduleId: moduleId }, resp => {
        this.moduleVO = resp.data
      })
    },
    onSaveName(value, done) {
      const param = {
        id: this.moduleVO.id,
        name: value
      }
      this.post('/module/name/update', param, () => {
        this.tipSuccess(this.$ts('updateSuccess'))
        this.moduleVO.name = value
        done()
      })
    },
    onModuleDelete() {
      this.confirm(this.$ts('deleteModuleConfirm'), () => {
        this.post('/module/delete', { id: this.moduleId }, () => {
          alert(this.$ts('deleteSuccess'))
          location.reload()
        })
      })
    }
  }
}
</script>
