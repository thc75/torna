<template>
  <error-code :project-id="projectId" />
</template>
<script>
import ErrorCode from './ErrorCode'

export default {
  components: { ErrorCode },
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
