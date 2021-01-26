<template>
  <project-info :project-id="projectId" />
</template>
<script>
import ProjectInfo from './ProjectInfo'

export default {
  components: { ProjectInfo },
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
