<template>
  <div class="sidebar-container">
    <logo />
    <el-menu
      class="torna-menu"
      router
      :default-active="currentActive"
    >
      <el-menu-item :index="`/project/${projectId}`">
        <i class="el-icon-document"></i>
        <span class="title">接口文档</span>
      </el-menu-item>
      <el-menu-item :index="`/project/info/${projectId}`">
        <i class="el-icon-info"></i>
        <span class="title">项目信息</span>
      </el-menu-item>
      <el-menu-item :index="`/project/member/${projectId}`">
        <i class="el-icon-user"></i>
        <span class="title">项目成员</span>
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
  },
  created() {
    this.projectId = this.$route.params.projectId
  }
}
</script>
