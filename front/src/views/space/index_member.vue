<template>
  <space-member :space-id="spaceId" />
</template>
<script>
import SpaceMember from './SpaceMember'

export default {
  components: { SpaceMember },
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
