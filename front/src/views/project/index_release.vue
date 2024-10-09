
<template>
  <project-release :project-id="projectId" />
</template>
<script>
import ProjectRelease from './ProjectRelease'

export default {
  components: { ProjectRelease },
  data() {
    return {
      projectId: ''
    }
  },
  mounted() {
    this.projectId = this.$route.params.projectId
  }
}
</script>
