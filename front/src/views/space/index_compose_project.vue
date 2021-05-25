<template>
  <compose-project :space-id="spaceId" />
</template>
<script>
import ComposeProject from './ComposeProject'

export default {
  components: { ComposeProject },
  data() {
    return {
      spaceId: ''
    }
  },
  mounted() {
    this.spaceId = this.$route.params.spaceId
  }
}
</script>
