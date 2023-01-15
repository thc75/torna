<template>
  <div class="app-container">
    <el-tabs active-name="base" tab-position="left">
      <el-tab-pane label="基础配置" name="base">
        <base-setting />
      </el-tab-pane>
      <el-tab-pane label="钉钉配置" name="dingding">
        <ding-ding />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import BaseSetting from './BaseSetting'
import DingDing from './DingDing'

export default {
  components: { BaseSetting, DingDing }
}
</script>
