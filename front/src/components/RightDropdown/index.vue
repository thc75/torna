<template>
  <el-dropdown trigger="click" @command="handleCommand">
    <el-avatar
      class="user-head"
      shape="square"
      size="medium"
      icon="el-icon-user-solid"
    />
    <el-dropdown-menu slot="dropdown">
      <el-dropdown-item :command="onResetPwd">
        <span>修改密码</span>
      </el-dropdown-item>
      <el-dropdown-item :command="doLogout" divided>
        <span style="display: block;">注销</span>
      </el-dropdown-item>
    </el-dropdown-menu>
  </el-dropdown>
</template>
<style>
.user-head {
  cursor: pointer;
  margin-top: 6px;margin-right: 10px;
}
</style>
<script>
export default {
  methods: {
    onResetPwd: function() {
      this.goRoute('/updatePassword')
    },
    doLogout() {
      this.logout()
      // this.$router.push(`/login?redirect=${this.$route.fullPath}`)
    }
  }
}
</script>
