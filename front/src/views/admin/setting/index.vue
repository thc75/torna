<template>
  <div class="app-container">
    <h4>基础配置</h4>
    <el-form :model="config" size="mini" label-width="200px" style="width: 80%">
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
      <el-form-item label="文档变更钉钉推送" style="display: none;">
        <el-input v-model="config.dingdingWebhookUrl.value" placeholder="输入钉钉机器人Webhook地址" clearable @change="onConfigChange(config.dingdingWebhookUrl)" />
        <el-link type="primary" href="https://developers.dingtalk.com/document/robots/custom-robot-access" target="_blank">机器人设置</el-link>
        <span class="info-tip">
          设置自定义关键字，添加：文档
          <el-popover
            placement="bottom"
            title="示意图"
            width="500"
            trigger="hover"
          >
            <img src="/static/images/dingding_webhook.png" width="450px" />
            <i slot="reference" class="el-icon-question"></i>
          </el-popover>
        </span>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>

export default {
  data() {
    return {
      config: {
        regEnable: { key: 'torna.register.enable', value: 'false' },
        dingdingWebhookUrl: { key: 'torna.push.dingding-webhook-url', value: '' }
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
