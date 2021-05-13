<template>
  <div class="navbar">
    <hamburger :is-active="sidebarView.opened" class="hamburger-container" @toggleClick="toggleSideBar" />

    <div class="right-menu">
      <div class="right-menu-item">
        <el-button type="primary" size="mini" icon="el-icon-monitor" @click="goAdminPage">{{ $ts('managementModel') }}</el-button>
      </div>
      <div class="right-menu-item">
        <user-message />
      </div>
      <div class="right-menu-item">
        <right-dropdown />
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Hamburger from '@/components/Hamburger'
import RightDropdown from '@/components/RightDropdown'
import UserMessage from '@/components/UserMessage'

export default {
  components: {
    Hamburger, RightDropdown, UserMessage
  },
  computed: {
    ...mapGetters([
      'sidebarView',
      'avatar'
    ])
  },
  methods: {
    toggleSideBar() {
      this.$store.dispatch('app/toggleSideBarView')
    },
    goAdminPage() {
      const docId = this.$route.params.docId
      if (docId) {
        this.get('/module/infoByDocId', { docId: docId }, resp => {
          const module = resp.data
          const projectId = module.projectId
          this.setProjectConfig(projectId, { moduleId: module.id })
          this.goProjectHome(projectId)
        })
      } else {
        const projectInfo = this.getCurrentProject()
        const projectId = projectInfo && projectInfo.id
        const uri = projectId ? this.getProjectHomeUrl(projectId) : '/'
        this.goRoute(uri)
      }
    }
  }
}
</script>
