<template>
  <div>
    <div>
      <h3>{{ $ts('swaggerMultiMethod') }}</h3>
      <el-form ref="allowMethodsRef" :model="setting" size="mini">
        <el-form-item prop="allowMethods">
          <el-select v-model="setting.allowMethod" @change="onSaveAllowMethods">
            <el-option v-for="method in allMethods" :key="method" :label="method" :value="method">
              {{ method }}
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>
<script>
export default {
  data() {
    return {
      setting: {
        allowMethod: ''
      },
      moduleId: '',
      allMethods: ['GET', 'POST', 'PUT', 'PATCH', 'DELETE', 'OPTIONS', 'HEAD']
    }
  },
  methods: {
    reload(moduleId) {
      if (moduleId) {
        this.moduleId = moduleId
      }
      this.loadSetting(this.moduleId)
    },
    loadSetting(moduleId) {
      this.get('/module/setting/swaggerSetting/get', { moduleId: moduleId }, resp => {
        this.setting = resp.data
      })
    },
    onSaveAllowMethods() {
      const data = {
        moduleId: this.moduleId,
        method: this.setting.allowMethod
      }
      this.post('/module/setting/swaggerSetting/allowMethod/set', data, () => {
        this.tipSuccess(this.$ts('updateSuccess'))
      })
    }
  }
}
</script>
