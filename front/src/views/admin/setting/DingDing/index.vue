<template>
  <div class="app-container">
    <el-form :model="config" size="mini" label-width="200px" style="width: 80%">
      <el-form-item label="钉钉appKey">
        <el-input
          v-model="config.dingdingAppKey.value"
          placeholder="appKey"
        />
      </el-form-item>
      <el-form-item label="钉钉appSecret">
        <el-input
          v-model="config.dingdingAppSecret.value"
          placeholder="appKey"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="save">保存</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import {} from '../setting'
export default {
  name: 'DingDing',
  data() {
    return {
      config: {
        dingdingAppKey: { key: 'torna.dingtalk.app-key', value: '', remark: '钉钉appKey' },
        dingdingAppSecret: { key: 'torna.dingtalk.app-secret', value: '', remark: '钉钉appSecret' }
      }
    }
  },
  created() {
    this.loadAdminConfig(this.config)
  },
  methods: {
    save() {
      const configs = []
      for (const configKey in this.config) {
        configs.push(this.config[configKey])
      }
      this.saveAdminConfig(configs)
    }
  }
}
</script>
