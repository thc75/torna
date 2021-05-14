<template>
  <div class="sidebar-container">
    <logo />
    <el-menu
      class="torna-menu"
      router
      :default-active="currentActive"
    >
      <el-menu-item :index="getProjectHomeUrl(projectId)">
        <i class="el-icon-document"></i>
        <span class="title">{{  $ts('apiDoc') }}</span>
      </el-menu-item>
      <el-menu-item :index="`/project/info/${projectId}`">
        <i class="el-icon-info"></i>
        <span class="title">{{ $ts('projectInfo') }}</span>
      </el-menu-item>
      <el-menu-item :index="`/project/member/${projectId}`">
        <i class="el-icon-user"></i>
        <span class="title">{{ $ts('projectMember') }}</span>
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
      // projectId: ''
    }
  },
  computed: {
    currentActive() {
      return this.$route.path
    },
    projectId() {
      return this.$store.state.settings.projectId
    }
  },
  created() {
    this.$store.state.settings.projectId = this.$route.params.projectId
  }
}
</script>
