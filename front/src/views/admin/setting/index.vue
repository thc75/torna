<template>
  <div class="app-container">
    <el-tabs active-name="base" tab-position="left" @tab-click="tabChange">
      <el-tab-pane label="基础配置" name="base">
        <base-setting ref="base" />
      </el-tab-pane>
      <el-tab-pane label="钉钉配置" name="dingding">
        <ding-ding ref="dingding" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import BaseSetting from './BaseSetting'
import DingDing from './DingDing'

export default {
  components: { BaseSetting, DingDing },
  methods: {
    tabChange(tab) {
      const name = tab.name
      const ref = this.$refs[name]
      ref && ref.reload()
    }
  }
}
</script>
