<template>
  <div class="app-container">
    <el-tabs active-name="base" tab-position="left" @tab-click="tabChange">
      <el-tab-pane name="base" :label="$t('SystemSetting.basicConfig')">
        <base-setting ref="base" />
      </el-tab-pane>
      <el-tab-pane name="i18n" :label="$t('SystemSetting.internationalizationConfig')">
        <i18n-setting ref="i18n" />
      </el-tab-pane>
      <el-tab-pane name="dingding" :label="$t('SystemSetting.dingdingConfig')">
        <ding-ding ref="dingding" />
      </el-tab-pane>
      <el-tab-pane name="weCom" :label="$t('SystemSetting.weComConfig')">
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
