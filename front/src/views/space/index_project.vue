<template>
  <space-project :space-id="spaceId" />
</template>
<script>
import SpaceProject from './SpaceProject'

export default {
  components: { SpaceProject },
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
