<template>
  <div class="app-container">
    <el-form :model="config" size="mini" label-width="200px" style="width: 80%">
      <el-form-item :label="$t('WeCom.messageTemplate')">
        <el-input
          v-model="config.weComMessageTemplate.value"
          type="textarea"
          :rows="8"
          show-word-limit
          maxlength="250"
          :placeholder="$t('WeCom.messageTemplatePlaceholder')"
        />
        <span class="tip">{{ $t('WeCom.placeholderTip') }}</span>
        <table border="0" cellpadding="0" cellspacing="0" class="el-table">
          <tr><td>{modifyType}</td><td>{{ $t('WeCom.modifyType') }}</td></tr>
          <tr><td>{projectName}</td><td>{{ $t('WeCom.projectName') }}</td></tr>
          <tr><td>{appName}</td><td>{{ $t('WeCom.appName') }}</td></tr>
          <tr><td>{releaseNo}</td><td>{{ $t('WeCom.releaseNo') }}</td></tr>
          <tr><td>{docName}</td><td>{{ $t('WeCom.docName') }}</td></tr>
          <tr><td>{modifier}</td><td>{{ $t('WeCom.modifier') }}</td></tr>
          <tr><td>{modifyTime}</td><td>{{ $t('WeCom.modifyTime') }}</td></tr>
          <tr><td>{docViewUrl}</td><td>{{ $t('WeCom.docViewUrl') }}</td></tr>
          <tr><td>{@user}</td><td v-html="$t('WeCom.atUserTip')"></td></tr>
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
  name: 'WeCom',
  data() {
    return {
      config: {
        weComMessageTemplate: { key: 'torna.push.weCom-webhook-content', value: '', remark: this.$t('WeCom.messageTemplateRemark') }
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
