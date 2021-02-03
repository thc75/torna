<template>
  <div class="app-container">
    <h3>账号信息</h3>
    <el-tabs v-model="activeName" type="card">
      <el-tab-pane label="基本信息" name="1">
        <user-info />
      </el-tab-pane>
      <el-tab-pane label="修改密码" name="2">
        <update-password />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script>
import UserInfo from './UserInfo'
import UpdatePassword from './UpdatePassword'
export default {
  components: { UserInfo, UpdatePassword },
  data() {
    return {
      activeName: '1'
    }
  }
}
</script>
