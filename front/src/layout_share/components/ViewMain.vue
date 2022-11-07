<template>
  <section class="app-main">
    <keep-alive>
      <router-view :key="key" />
    </keep-alive>
  </section>
</template>

<script>
export default {
  name: 'ViewMain',
  computed: {
    key() {
      return this.$route.fullPath
    }
  }
}
</script>

