<template>
  <constant-info :project-id="projectId" />
</template>
<script>
import ConstantInfo from './ConstantInfo'

export default {
  components: { ConstantInfo },
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
