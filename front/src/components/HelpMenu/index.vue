<template>
  <el-menu
    :default-active="activeIndex"
    mode="horizontal"
    text-color="#303133"
    router
  >
    <el-menu-item index="/" style="padding-left: 0">
      <logo no-style />
    </el-menu-item>
    <el-menu-item index="/help">帮助中心</el-menu-item>
    <el-menu-item index="/openapi">OpenAPI文档</el-menu-item>
  </el-menu>
</template>
<script>
import Logo from '@/components/Logo'
export default {
  components: { Logo },
  data() {
    return {
      activeIndex: ''
    }
  },
  created() {
    this.activeIndex = this.$route.path
  }
}
</script>
