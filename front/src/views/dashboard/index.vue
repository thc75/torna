<template>
  <div class="app-container">
    <space-list />
  </div>
</template>
<script>
import SpaceList from './SpaceList'
export default {
  components: { SpaceList },
  created() {
    this.setCurrentInfo('', '')
  }
}
</script>

