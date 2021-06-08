<template>
  <div class="sidebar-container">
    <logo :collapse="false" />
    <el-menu
      :default-openeds="['1', '2', '3']"
      :router="true"
      :default-active="currentActive"
    >
      <el-submenu index="1">
        <template slot="title">
          <i class="el-icon-user"></i>
          <span>{{ $ts('baseSetting') }}</span>
        </template>
        <el-menu-item index="/user">{{ $ts('accountInfo') }}</el-menu-item>
      </el-submenu>
      <el-submenu index="2">
        <template slot="title">
          <i class="el-icon-star-on"></i>
          <span>{{ $ts('subscription') }}</span>
        </template>
        <el-menu-item index="/user/subscribe/doc">{{ $ts('subscribeApi') }}</el-menu-item>
      </el-submenu>
      <el-submenu index="3">
        <template slot="title">
          <i class="el-icon-bell"></i>
          <span>{{ $ts('messageCenter') }}</span>
        </template>
        <el-menu-item index="/user/message">{{ $ts('myMessage') }}</el-menu-item>
      </el-submenu>
      <el-menu-item index="/user/systemSetting">
        <i class="el-icon-setting"></i>
        <span slot="title">{{ $ts('systemSetting') }}</span>
      </el-menu-item>
    </el-menu>
  </div>
</template>
<script>
import Logo from '@/components/Logo'
export default {
  name: 'UserCenterMenu',
  components: { Logo },
  computed: {
    currentActive() {
      return this.$route.path
    }
  }
}
</script>
<style lang="scss" scoped>
@import "~@/styles/variables.scss";
.sidebar-container {
  width: $sideBarUserWidth !important;
}
.main-container {
  margin-left: $sideBarUserWidth !important;
}
</style>
