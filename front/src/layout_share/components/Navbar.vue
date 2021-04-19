<template>
  <div class="navbar">
    <hamburger :is-active="sidebarView.opened" class="hamburger-container" @toggleClick="toggleSideBar" />
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Hamburger from '@/components/Hamburger'

export default {
  components: {
    Hamburger
  },
  computed: {
    ...mapGetters([
      'sidebarView',
      'avatar'
    ])
  },
  methods: {
    toggleSideBar() {
      this.$store.dispatch('app/toggleSideBarView')
    }
  }
}
</script>
