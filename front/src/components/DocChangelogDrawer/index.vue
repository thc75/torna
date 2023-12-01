<template>
  <div>
    <el-drawer
      :title="$t('changeHistory')"
      :visible.sync="drawer"
      :direction="direction"
      size="35%"
      :with-header="true"
      :before-close="handleClose"
    >
      <doc-changelog ref="docChange" />
    </el-drawer>
  </div>
</template>
<script>
import DocChangelog from '@/components/DocChangelog'
export default {
  components: { DocChangelog },
  data() {
    return {
      drawer: false,
      direction: 'rtl',
      list: []
    }
  },
  methods: {
    show(docId) {
      this.drawer = true
      this.$nextTick(() => {
        document.body.style.overflowY = 'hidden'
        this.$refs.docChange.show(docId)
      })
    },
    handleClose() {
      this.drawer = false
      document.body.style.overflowY = 'auto'
    }
  }
}
</script>
