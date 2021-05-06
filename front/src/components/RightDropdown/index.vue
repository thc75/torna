<template>
  <el-dropdown trigger="click" @command="handleCommand">
    <el-avatar
      class="user-head"
      shape="square"
      size="medium"
      icon="el-icon-user-solid"
    />
    <el-dropdown-menu slot="dropdown" class="user-drop-menu">
      <el-dropdown-item :command="onUserInfo">
        <span>{{ $ts('userCenter') }}</span>
      </el-dropdown-item>
      <el-dropdown-item :command="doLogout" divided>
        <span>{{ $ts('logout') }}</span>
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
    onUserInfo: function() {
      this.goRoute('/user')
    },
    doLogout() {
      this.logout()
      // this.$router.push(`/login?redirect=${this.$route.fullPath}`)
    }
  }
}
</script>
