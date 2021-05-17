<template>
  <div class="navbar">
    <hamburger :is-active="sidebarView.opened" class="hamburger-container" @toggleClick="toggleSideBar" />
    <div class="right-menu">
      <div class="right-menu-item">
        <language-select />
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Hamburger from '@/components/Hamburger'
import LanguageSelect from '@/components/LanguageSelect'

export default {
  components: {
    Hamburger, LanguageSelect
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
