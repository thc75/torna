<template>
  <div>
    <el-form
      ref="updateEnvForm"
      :model="config"
      size="mini"
    >
      <el-alert :title="$ts('dingdingWebhookUrlTip')" :closable="false"  />
      <el-form-item :label="$ts('dingdingWebhookUrl')">
        <el-input v-model="config.dingdingWebhookUrl" :placeholder="$ts('dingdingWebhookUrlPlacehoder')" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary">{{ $ts('save') }}</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
$addI18n({
  'dingdingWebhookUrl': { zh: '钉钉群机器人推送URL（Webhook）', en: 'DingDing group robot webhook URL' },
  'dingdingWebhookUrlPlacehoder': { zh: '输入完整带token参数的url', en: 'Input full url with token parameter' },
  'dingdingWebhookUrlTip': { zh: '当文档变更时推送消息到钉钉群。需要创建自定义机器人', en: 'Push message to dingding group when doc is changed' }
})
export default {
  name: 'DingDingSetting',
  data() {
    return {
      config: {
        dingdingWebhookUrl: ''
      }
    }
  }
}
</script>
