<template>
  <div>
    <el-form
      ref="updateEnvForm"
      :model="config"
      size="mini"
    >
      <el-alert :title="$t('DingDingSetting.dingdingWebhookUrlTip')" :closable="false"  />
      <el-form-item :label="$t('DingDingSetting.dingdingWebhookUrl')">
        <el-input
          v-model="config.dingdingWebhookUrl.configValue"
          :placeholder="$t('DingDingSetting.dingdingWebhookUrlPlacehoder')"
          show-word-limit
          maxlength="200"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="saveConfig">{{ $t('save') }}</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
import { listModuleConfig, saveModuleConfig } from '../moduleSetting'

export default {
  name: 'DingDingSetting',
  data() {
    return {
      moduleId: '',
      config: {
        dingdingWebhookUrl: { configKey: 'dingdingWebhookUrl', configValue: '', description: '钉钉群机器人webhook' }
      }
    }
  },
  methods: {
    reload(moduleId) {
      this.moduleId = moduleId
      this.loadConfig(moduleId)
    },
    loadConfig() {
      this.clearForm()
      const configKeys = Object.keys(this.config)
      listModuleConfig(this.moduleId, configKeys, (configs) => {
        for (const config of configs) {
          this.config[config.configKey] = config
        }
      })
    },
    clearForm() {
      for (const configKey in this.config) {
        this.config[configKey].configValue = ''
      }
    },
    saveConfig() {
      const items = []
      for (const configKey in this.config) {
        const obj = this.config[configKey]
        obj.configKey = configKey
        items.push(obj)
      }
      saveModuleConfig({
        moduleId: this.moduleId,
        items: items
      }, () => {
        this.tipSuccess($t('saveSuccess'))
      })
    }
  }
}
</script>
