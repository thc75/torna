<template>
  <div class="sidebar-container">
    <logo />
    <el-menu
      class="torna-menu"
      router
      :default-active="currentActive"
    >
      <el-menu-item :index="projectIndex">
        <i class="el-icon-s-grid"></i>
        <span class="title">{{ $t('projectList') }}</span>
      </el-menu-item>
      <el-menu-item :index="`/space/info/${spaceId}`">
        <i class="el-icon-info"></i>
        <span class="title">{{ $t('spaceInfo') }}</span>
      </el-menu-item>
      <el-menu-item :index="`/space/member/${spaceId}`">
        <i class="el-icon-user"></i>
        <span class="title">{{ $t('spaceMember') }}</span>
      </el-menu-item>
      <el-menu-item v-show="enableMeterSphere" v-if="hasRole(`space:${spaceId}`, [Role.admin])" :index="`/space/setting/${spaceId}`">
        <i class="el-icon-setting"></i>
        <span class="title">{{ $t('spaceSetting') }}</span>
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
      spaceData: {
        id: '',
        isCompose: 0
      },
      currentActive: '',
      enableMeterSphere: false
    }
  },
  computed: {
    projectIndex() {
      if (this.spaceData.isCompose) {
        return `/space/compose/${this.spaceId}`
      } else {
        return `/space/project/${this.spaceId}`
      }
    },
    spaceId() {
      return this.spaceData.id || this.$route.params.spaceId
    }
  },
  mounted() {
    this.currentActive = this.$route.path
    this.pmsConfig().then(config => {
      this.enableMeterSphere = config.enableMeterSphere
    })
  },
  methods: {
    setSpaceData(data) {
      this.spaceData = data
    }
  }
}
</script>
