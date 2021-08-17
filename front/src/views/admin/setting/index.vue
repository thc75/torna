<template>
  <div class="app-container">
    <h4>基础配置</h4>
    <el-form :model="config" size="mini">
      <el-form-item label="允许注册">
        <el-switch
          v-model="config.regEnable.value"
          active-text="允许"
          active-value="true"
          inactive-text=""
          inactive-value="false"
          @change="onConfigChange(config.regEnable)"
        />
      </el-form-item>
    </el-form>
  </div>
</template>

<script>

export default {
  data() {
    return {
      config: {
        regEnable: { key: 'torna.register.enable', value: 'false' }
      }
    }
  },
  created() {
    this.loadConfig()
  },
  methods: {
    loadConfig() {
      this.get('/system/config/adminsetting', {}, resp => {
        this.config = resp.data
      })
    },
    onConfigChange(config) {
      this.post('/system/config/update', config, () => {
        this.tipSuccess($ts('updateSuccess'))
      })
    }
  }
}
</script>
