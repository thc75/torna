<template>
  <space-info :space-id="spaceId" />
</template>
<script>
import SpaceInfo from './SpaceInfo'

export default {
  components: { SpaceInfo },
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
