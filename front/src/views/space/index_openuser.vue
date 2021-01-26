<template>
  <open-user :space-id="spaceId" />
</template>
<script>
import OpenUser from './OpenUser'

export default {
  components: { OpenUser },
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
