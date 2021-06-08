<template>
  <div class="app-wrapper">
    <project-menu />
    <div class="main-container">
      <div :class="{'fixed-header':fixedHeader}">
        <navbar />
      </div>
      <app-main />
    </div>
  </div>
</template>

<script>
import { Navbar, AppMain } from './components'
import ResizeMixin from './mixin/ResizeHandler'
import ProjectMenu from '@/components/ProjectMenu'

export default {
  name: 'Layout',
  components: {
    Navbar,
    AppMain,
    ProjectMenu
  },
  mixins: [ResizeMixin],
  computed: {
    fixedHeader() {
      return this.$store.state.settings.fixedHeader
    },
    currentProjectId() {
      return this.$route.params.projectId || this.$store.state.settings.projectId
    }
  },
  mounted() {
    const projectId = this.currentProjectId
    this.initCurrentInfo(projectId)
  },
  methods: {
    initCurrentInfo(projectId) {
      let fromData = this.getFrom()
      if (!fromData) {
        if (projectId) {
          this.get('/project/space', { projectId: projectId }, resp => {
            fromData = resp.data
            this.setTitle(fromData.projectName)
            this.setCurrentInfo({
              id: fromData.spaceId,
              name: fromData.spaceName
            }, {
              id: fromData.projectId,
              name: fromData.projectName
            })
          })
        }
      } else {
        this.setTitle(fromData.projectName)
        this.setCurrentInfo({
          id: fromData.spaceId,
          name: fromData.spaceName
        }, {
          id: fromData.projectId,
          name: fromData.projectName
        })
      }
    }
  }
}
</script>
