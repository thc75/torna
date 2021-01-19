<template>
  <div>
    <el-menu
      :default-openeds="['1', '2', '3']"
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
      <el-submenu index="2">
        <template slot="title">
          <i class="el-icon-star-on"></i>
          <span>关注列表</span>
        </template>
        <el-menu-item index="/user/subscribe/doc">关注接口</el-menu-item>
      </el-submenu>
      <el-submenu index="3">
        <template slot="title">
          <i class="el-icon-bell"></i>
          <span>消息中心</span>
        </template>
        <el-menu-item index="/user/message">我的消息</el-menu-item>
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

