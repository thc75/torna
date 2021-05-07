<template>
  <div class="sidebar-container">
    <logo />
    <el-menu
      class="torna-menu"
      router
      :default-active="currentActive"
    >
      <el-menu-item :index="`/space/project/${spaceId}`">
        <i class="el-icon-s-grid"></i>
        <span class="title">{{ $ts('projectList') }}</span>
      </el-menu-item>
      <el-menu-item :index="`/space/info/${spaceId}`">
        <i class="el-icon-info"></i>
        <span class="title">{{ $ts('spaceInfo') }}</span>
      </el-menu-item>
      <el-menu-item :index="`/space/member/${spaceId}`">
        <i class="el-icon-user"></i>
        <span class="title">{{ $ts('spaceMember') }}</span>
      </el-menu-item>
      <el-menu-item v-if="spaceId && hasRole(`space:${spaceId}`, Role.admin)" :index="`/space/openuser/${spaceId}`">
        <i class="el-icon-collection-tag"></i>
        <span class="title">{{ $ts('openUser') }}</span>
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
