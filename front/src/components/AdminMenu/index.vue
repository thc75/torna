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
        <span class="title">{{ $t('userManagement') }}</span>
      </el-menu-item>
      <el-menu-item index="/admin/template">
        <i class="el-icon-tickets"></i>
        <span class="title">{{ $t('templateSetting') }}</span>
      </el-menu-item>
      <el-menu-item index="/admin/setting">
        <i class="el-icon-setting"></i>
        <span class="title">{{ $t('systemSetting') }}</span>
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
