<template>
  <div class="app-wrapper">
    <space-menu ref="spaceMenu" />
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
import SpaceMenu from '@/components/SpaceMenu'

export default {
  name: 'Layout',
  components: {
    Navbar,
    AppMain,
    SpaceMenu
  },
  mixins: [ResizeMixin],
  data() {
    return {
      space: ''
    }
  },
  computed: {
    fixedHeader() {
      return this.$store.state.settings.fixedHeader
    }
  },
  created() {
    const spaceId = this.$route.params.spaceId
    this.loadData(spaceId)
  },
  methods: {
    loadData(spaceId) {
      if (!this.space) {
        this.get('/space/info', { spaceId: spaceId }, resp => {
          this.space = resp.data
          this.$refs.spaceMenu.setSpaceData(this.space)
          this.setCurrentInfo(this.space, '')
        })
      }
    }
  }
}
</script>
