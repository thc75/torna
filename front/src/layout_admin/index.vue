<template>
  <div class="app-wrapper">
    <admin-menu />
    <div class="main-container">
      <div :class="{'fixed-header':fixedHeader}">
        <navbar />
      </div>
      <app-main />
    </div>
  </div>
</template>

<script>
import { Navbar, AppMain } from './components'
import ResizeMixin from './mixin/ResizeHandler'
import AdminMenu from '@/components/AdminMenu'

export default {
  name: 'Layout',
  components: {
    Navbar,
    AppMain,
    AdminMenu
  },
  mixins: [ResizeMixin],
  computed: {
    fixedHeader() {
      return this.$store.state.settings.fixedHeader
    }
  }
}
</script>
