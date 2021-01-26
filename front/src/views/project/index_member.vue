<template>
  <project-member :project-id="projectId" />
</template>
<script>
import ProjectMember from './ProjectMember'

export default {
  components: { ProjectMember },
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
