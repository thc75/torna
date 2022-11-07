<template>
  <div style="padding: 0 20px;">
    <constant-info :project-id="projectId" />
  </div>
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
