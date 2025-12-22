<template>
  <div class="app-container">
    <el-form :model="config" size="mini" label-width="200px" style="width: 80%">
      <el-form-item :label="$t('DingDing.appKey')">
        <el-input
          v-model="config.dingdingAppKey.value"
          :placeholder="$t('DingDing.appKeyPlaceholder')"
        />
      </el-form-item>
      <el-form-item :label="$t('DingDing.appSecret')">
        <el-input
          v-model="config.dingdingAppSecret.value"
          :placeholder="$t('DingDing.appSecretPlaceholder')"
        />
      </el-form-item>
      <el-form-item :label="$t('DingDing.messageTemplate')">
        <el-input
          v-model="config.dingdingMessageTemplate.value"
          type="textarea"
          :rows="8"
          show-word-limit
          maxlength="250"
          :placeholder="$t('DingDing.messageTemplatePlaceholder')"
        />
        <span class="tip">{{ $t('DingDing.placeholderTip') }}</span>
        <table border="0" cellpadding="0" cellspacing="0" class="el-table">
          <tr><td>{modifyType}</td><td>{{ $t('DingDing.modifyType') }}</td></tr>
          <tr><td>{projectName}</td><td>{{ $t('DingDing.projectName') }}</td></tr>
          <tr><td>{appName}</td><td>{{ $t('DingDing.appName') }}</td></tr>
          <tr><td>{releaseNo}</td><td>{{ $t('DingDing.releaseNo') }}</td></tr>
          <tr><td>{docName}</td><td>{{ $t('DingDing.docName') }}</td></tr>
          <tr><td>{modifier}</td><td>{{ $t('DingDing.modifier') }}</td></tr>
          <tr><td>{modifyTime}</td><td>{{ $t('DingDing.modifyTime') }}</td></tr>
          <tr><td>{docViewUrl}</td><td>{{ $t('DingDing.docViewUrl') }}</td></tr>
          <tr><td>{@user}</td><td v-html="$t('DingDing.atUserTip')"></td></tr>
        </table>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="save">{{ $t('save') }}</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { saveAdminConfig, loadAdminConfig } from '../setting'
export default {
  name: 'DingDing',
  data() {
    return {
      config: {
        dingdingAppKey: { key: 'torna.dingtalk.app-key', value: '', remark: this.$t('DingDing.appKeyRemark') },
        dingdingAppSecret: { key: 'torna.dingtalk.app-secret', value: '', remark: this.$t('DingDing.appSecretRemark') },
        dingdingMessageTemplate: { key: 'torna.push.dingding-webhook-content', value: '', remark: this.$t('DingDing.messageTemplateRemark') }
      }
    }
  },
  methods: {
    reload() {
      loadAdminConfig(this.config)
    },
    save() {
      const configs = []
      for (const configKey in this.config) {
        configs.push(this.config[configKey])
      }
      saveAdminConfig(configs)
    }
  }
}
</script>
