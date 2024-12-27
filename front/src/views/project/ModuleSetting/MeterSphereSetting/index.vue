<template>
  <div>
    <div v-show="spaceConfig">
      <el-alert type="info" :closable="false" style="margin-bottom: 10px">
        当前应用绑定MeterSphere模块，绑定后应用收到推送会把文档同步到MeterSphere
      </el-alert>
      <el-form ref="msModuleFrm" :model="formData" :rules="formRules">
        <el-form-item v-show="currentName && currentName.length > 0" label="当前绑定模块">
          {{ currentName }}
        </el-form-item>
        <el-form-item prop="msModuleId" label="绑定模块">
          <el-cascader ref="sel" v-model="formData.msModuleId" :props="props" :options="options" style="width: 350px" clearable></el-cascader>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveConfig">{{ $t('save') }}</el-button>
          <el-button type="primary" @click="syncMeterSphere">{{ $t('SwaggerSetting.synchronization') }}</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div v-show="!spaceConfig">
      <el-alert type="info" :closable="false">
        空间未配置MeterSphere参数 <span v-show="spaceId && spaceId.length > 0"> <el-link type="primary" @click="goRoute(`/space/setting/${spaceId}`)">前往配置</el-link> </span>
      </el-alert>
    </div>
  </div>
</template>
<script>
let that
export default {
  name: 'MeterSphereSetting',
  data() {
    return {
      moduleId: '',
      projectId: '',
      spaceId: '',
      currentName: '',
      formData: {
        msModuleId: ''
      },
      formRules: {
        msModuleId: [
          { required: true, message: this.$t('notEmpty'), trigger: 'blur' }
        ]
      },
      options: [],
      spaceConfig: false,
      props: {
        value: 'id',
        label: 'name',
        checkStrictly: true,
        lazy: true,
        lazyLoad(node, resolve) {
          const { level } = node
          if (!that || level >= 2) {
            resolve([])
            return
          }
          const msProjectId = node.value
          that.get('third/metersphere/module/list', { projectId: that.projectId, msProjectId: msProjectId }, resp => {
            const data = resp.data
            // for (const el of data) {
            //   el.leaf = true
            // }
            resolve(data)
          })
        }
      }
    }
  },
  methods: {
    reload(moduleId, projectId) {
      that = this
      this.moduleId = moduleId
      this.projectId = projectId
      this.get('third/metersphere/project/load', { projectId: projectId }, resp => {
        const data = resp.data
        this.spaceConfig = data.spaceConfig
        this.spaceId = data.spaceId
        this.options = data.projects || []
      })
      this.loadConfig(moduleId)
    },
    loadConfig(moduleId) {
      this.get('third/metersphere/module/load', { moduleId: moduleId }, resp => {
        const data = resp.data
        if (data) {
          this.currentName = data.name
        }
      })
    },
    saveConfig() {
      this.$refs.msModuleFrm.validate(valid => {
        if (valid) {
          const checkedNodes = this.$refs.sel.getCheckedNodes()
          const leaf = checkedNodes[0]
          const pathNodes = leaf.pathNodes
          const msProject = pathNodes[0]
          const msModule = pathNodes[pathNodes.length - 1]
          const label = pathNodes.slice(1).map(node => node.label).join('/')
          const data = {
            msProjectId: msProject.value,
            msProjectName: msProject.label,
            msModuleId: msModule.value,
            msModuleName: label,
            moduleId: this.moduleId
          }
          this.post('third/metersphere/module/save', data, resp => {
            if (resp.code === '0') {
              this.confirm(this.$t('SwaggerSetting.syncConfirm'), () => {
                this.syncMeterSphere()
              })
            }
          })
        }
      })
    },
    syncMeterSphere() {
      const data = {
        moduleId: this.moduleId
      }
      this.confirm(this.$t('SwaggerSetting.checkSynchronized'), () => {
        this.post('third/metersphere/module/sync', data, resp => {
          this.tipSuccess($t('syncSuccess'))
        })
      })
    }
  }
}
</script>
