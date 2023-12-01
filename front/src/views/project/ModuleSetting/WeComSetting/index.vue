<template>
  <div>
    <el-form
      ref="updateEnvForm"
      :model="config"
      size="mini"
    >
      <el-alert :title="$t('WeComSetting.weComWebhookUrlTip')" :closable="false"  />
      <el-form-item :label="$t('WeComSetting.weComWebhookUrl')">
        <el-input
          v-model="config.weComWebhookUrl.configValue"
          :placeholder="$t('WeComSetting.weComWebhookUrlPlacehoder')"
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
  name: 'WeComSetting',
  data() {
    return {
      moduleId: '',
      config: {
        weComWebhookUrl: { configKey: 'weComWebhookUrl', configValue: '', description: '企业微信群机器人webhook' }
      }
    }
  },
  methods: {
    reload(moduleId) {
      this.moduleId = moduleId
      this.loadConfig(moduleId)
    },
    loadConfig() {
      const configKeys = Object.keys(this.config)
      listModuleConfig(this.moduleId, configKeys, (configs) => {
        for (const config of configs) {
          this.config[config.configKey] = config
        }
      })
    },
    saveConfig() {
      const items = []
      for (const configKey in this.config) {
        const obj = this.config[configKey]
        items.push(obj)
      }
      saveModuleConfig({
        moduleId: this.moduleId,
        items: items
      }, () => {
        this.tipSuccess($ts('saveSuccess'))
      })
    }
  }
}
</script>
