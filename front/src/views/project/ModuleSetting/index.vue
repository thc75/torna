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
        <el-button type="danger" size="mini" @click="onModuleDelete">删除模块</el-button>
      </div>
    </h3>
    <el-tabs v-model="activeName" type="border-card" @tab-click="onTabClick">
      <el-tab-pane label="公共请求头" name="globalHeaders">
        <global-headers ref="globalHeaders" />
      </el-tab-pane>
      <el-tab-pane label="公共请求参数" name="globalParams">
        <global-params ref="globalParams" />
      </el-tab-pane>
      <el-tab-pane label="公共返回参数" name="globalReturns">
        <global-returns ref="globalReturns" />
      </el-tab-pane>
      <el-tab-pane label="调试环境" name="debugEnv">
        <debug-env ref="debugEnv" />
      </el-tab-pane>
      <el-tab-pane v-if="moduleVO.type === 1" label="Swagger设置" name="swaggerSetting">
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
        this.tipSuccess('修改成功')
        this.moduleVO.name = value
        done()
      })
    },
    onModuleDelete() {
      this.confirm('确认要删除该模块吗？', () => {
        this.post('/module/delete', { id: this.moduleId }, () => {
          alert('删除成功')
          location.reload()
        })
      })
    }
  }
}
</script>
