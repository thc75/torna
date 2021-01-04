<template>
  <el-dropdown @command="handleCommand">
    <el-button size="mini" type="primary">
      管理员菜单 <i class="el-icon-arrow-down el-icon--right"></i>
    </el-button>
    <el-dropdown-menu slot="dropdown">
      <el-dropdown-item :command="() => goRoute('/admin/users')">用户管理</el-dropdown-item>
    </el-dropdown-menu>
  </el-dropdown>
</template>
