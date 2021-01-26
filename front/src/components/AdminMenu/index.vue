<template>
  <el-menu
    class="torna-menu"
    router
    :default-active="currentActive"
  >
    <el-menu-item index="/admin/users">
      <i class="el-icon-user"></i>
      <span class="title">用户管理</span>
    </el-menu-item>
  </el-menu>
</template>
<script>
export default {
  data() {
    return {
      projectId: ''
    }
  },
  computed: {
    currentActive() {
      return this.$route.path
    }
  }
}
</script>
