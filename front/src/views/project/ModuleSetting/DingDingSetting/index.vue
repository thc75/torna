<template>
  <div>
    <el-form
      ref="updateEnvForm"
      :model="config"
      size="mini"
    >
      <el-alert :title="$ts('dingdingWebhookUrlTip')" :closable="false"  />
      <el-form-item :label="$ts('dingdingWebhookUrl')">
        <el-input
          v-model="config.dingdingWebhookUrl.configValue"
          :placeholder="$ts('dingdingWebhookUrlPlacehoder')"
          show-word-limit
          maxlength="200"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="saveConfig">{{ $ts('save') }}</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
$addI18n({
  'dingdingWebhookUrl': { zh: '钉钉群机器人推送URL（Webhook）', en: 'DingDing group robot webhook URL' },
  'dingdingWebhookUrlPlacehoder': { zh: '输入完整带token参数的url', en: 'Input full url with token parameter' },
  'dingdingWebhookUrlTip': { zh: '当文档变更时推送消息到钉钉群。需要创建自定义机器人，添加关键字：文档', en: 'Push message to dingding group when doc is changed' }
})

import { listModuleConfig, saveModuleConfig } from '../moduleSetting'

export default {
  name: 'DingDingSetting',
  data() {
    return {
      moduleId: '',
      config: {
        dingdingWebhookUrl: { configValue: '', description: '钉钉群机器人webhook' }
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
