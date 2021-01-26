<template>
  <module :project-id="projectId" />
</template>
<script>
import Module from './Module'

export default {
  components: { Module },
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
