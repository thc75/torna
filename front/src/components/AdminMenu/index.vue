<template>
  <div class="sidebar-container">
    <logo :collapse="true" />
    <el-menu
      class="torna-menu"
      router
      :default-active="currentActive"
    >
      <el-menu-item index="/admin/users">
        <i class="el-icon-user"></i>
        <span class="title">{{ $ts('userManagement') }}</span>
      </el-menu-item>
      <el-menu-item index="/admin/setting">
        <i class="el-icon-setting"></i>
        <span class="title">{{ $ts('systemSetting') }}</span>
      </el-menu-item>
    </el-menu>
  </div>
</template>
<script>
import Logo from '@/components/Logo'
export default {
  components: { Logo },
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
