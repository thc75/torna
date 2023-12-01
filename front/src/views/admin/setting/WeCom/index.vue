<template>
  <div class="app-container">
    <el-form :model="config" size="mini" label-width="200px" style="width: 80%">
      <el-form-item label="企业微信消息推送模板">
        <el-input
          v-model="config.weComMessageTemplate.value"
          type="textarea"
          :rows="8"
          show-word-limit
          maxlength="250"
          placeholder="企业微信消息推送模板"
        />
        <span class="tip">{xx}为变量占位符，消息推送将替换成实际内容，变量说明</span>
        <table border="0" cellpadding="0" cellspacing="0" class="el-table">
          <tr><td>{modifyType}</td><td>修改类型，枚举值：修改,删除</td></tr>
          <tr><td>{projectName}</td><td>项目名称</td></tr>
          <tr><td>{appName}</td><td>应用名称</td></tr>
          <tr><td>{docName}</td><td>文档名称</td></tr>
          <tr><td>{modifier}</td><td>修改人</td></tr>
          <tr><td>{modifyTime}</td><td>修改时间</td></tr>
          <tr><td>{docViewUrl}</td><td>文档预览地址</td></tr>
          <tr><td>{@user}</td><td>@用户，关注此文档的人都会被at，<b>前提：前往[个人中心]-[账号信息]填写企业微信手机号码</b></td></tr>
        </table>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="save">保存</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { saveAdminConfig, loadAdminConfig } from '../setting'
export default {
  name: 'weCom',
  data() {
    return {
      config: {
        weComMessageTemplate: { key: 'torna.push.weCom-webhook-content', value: '', remark: '企业微信消息推送模板' }
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
