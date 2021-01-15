<template>
  <div>
    <el-menu
      :default-openeds="['1']"
      :router="true"
      :default-active="currentActive"
    >
      <el-submenu index="1">
        <template slot="title">
          <i class="el-icon-user"></i>
          <span>基本设置</span>
        </template>
        <el-menu-item index="/user">账号信息</el-menu-item>
      </el-submenu>
    </el-menu>
  </div>
</template>
<script>
export default {
  name: 'UserCenterMenu',
  computed: {
    currentActive() {
      return this.$route.path
    }
  }
}
</script>

