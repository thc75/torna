<template>
  <div>
    <div v-if="spaceConfig">
      <el-alert type="info" :closable="false" style="margin-bottom: 10px">
        MeterSphere模块对应Torna中的应用
      </el-alert>
      <el-form ref="msModuleFrm" :model="formData" :rules="formRules">
        <el-form-item v-show="currentName && currentName.length > 0" label="当前配置">
          {{ currentName }}
        </el-form-item>
        <el-form-item prop="msModuleId" label="绑定模块">
          <el-cascader ref="sel" v-model="formData.msModuleId" :props="props" :options="options" style="width: 350px"></el-cascader>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveConfig">{{ $t('save') }}</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div v-else>
      <el-alert type="info" :closable="false">
        空间未配置MeterSphere参数 <span v-show="spaceId.length > 0"> <el-link type="primary" @click="goRoute(`/space/setting/${spaceId}`)">前往配置</el-link> </span>
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
        lazy: true,
        lazyLoad(node, resolve) {
          const { level } = node
          if (level >= 2) {
            resolve([])
            return
          }
          const msProjectId = node.value
          that.get('third/metersphere/module/list', { projectId: that.projectId, msProjectId: msProjectId }, resp => {
            const data = resp.data
            for (const el of data) {
              el.leaf = true
            }
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
          const msModule = pathNodes[1]
          const data = {
            msProjectId: msProject.value,
            msProjectName: msProject.label,
            msModuleId: msModule.value,
            msModuleName: msModule.label,
            moduleId: this.moduleId
          }
          this.post('third/metersphere/module/save', data, resp => {
            this.tipSuccess($t('saveSuccess'))
          })
        }
      })
    }
  }
}
</script>
