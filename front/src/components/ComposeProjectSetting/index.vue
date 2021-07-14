<template>
  <div>
    <div>
      网关模式：<el-checkbox v-model="setting.gatewayMode"></el-checkbox>
    </div>
    <el-tabs active-name="commonParam">
      <el-tab-pane :label="$ts('commonRequest')" name="commonParam"></el-tab-pane>
    </el-tabs>
  </div>
</template>
<script>
export default {
  props: {
    projectId: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      setting: {
        gatewayMode: false

      },
      globalParams: [],
    }
  }
}
</script>
