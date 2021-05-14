<template>
  <div class="sidebar-container">
    <logo />
    <el-menu
      class="torna-menu"
      router
      :default-active="currentActive"
    >
      <el-menu-item index="/dashboard">
        <i class="el-icon-house"></i>
        <span class="title">{{ $ts('spaceList') }}</span>
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
