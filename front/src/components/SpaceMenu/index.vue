<template>
  <el-menu
    class="torna-menu"
    router
    :default-active="currentActive"
  >
    <el-menu-item :index="`/space/project/${spaceId}`">
      <i class="el-icon-s-grid"></i>
      <span class="title">项目列表</span>
    </el-menu-item>
    <el-menu-item :index="`/space/info/${spaceId}`">
      <i class="el-icon-info"></i>
      <span class="title">空间信息</span>
    </el-menu-item>
    <el-menu-item :index="`/space/member/${spaceId}`">
      <i class="el-icon-user"></i>
      <span class="title">空间成员</span>
    </el-menu-item>
    <el-menu-item :index="`/space/openuser/${spaceId}`">
      <i class="el-icon-collection-tag"></i>
      <span class="title">开放用户</span>
    </el-menu-item>
  </el-menu>
</template>
<script>
export default {
  data() {
    return {
      spaceId: ''
    }
  },
  computed: {
    currentActive() {
      return this.$route.path
    }
  },
  created() {
    this.spaceId = this.$route.params.spaceId
  }
}
</script>
