<template>
  <div class="app-container">
    <el-tabs active-name="base" tab-position="left" @tab-click="tabChange">
      <el-tab-pane label="基础配置" name="base">
        <base-setting ref="base" />
      </el-tab-pane>
      <el-tab-pane label="国际化设置" name="i18n">
        <i18n-setting ref="i18n" />
      </el-tab-pane>
      <el-tab-pane label="钉钉配置" name="dingding">
        <ding-ding ref="dingding" />
      </el-tab-pane>
      <el-tab-pane label="企业微信配置" name="weCom">
        <weCom ref="weCom" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import BaseSetting from './BaseSetting'
import DingDing from './DingDing'
import WeCom from './WeCom'
import I18nSetting from './I18nSetting'

export default {
  components: { BaseSetting, I18nSetting, DingDing, WeCom },
  methods: {
    tabChange(tab) {
      const name = tab.name
      const ref = this.$refs[name]
      ref && ref.reload()
    }
  }
}
</script>
