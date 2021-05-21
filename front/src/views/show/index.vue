<template>
  <div class="app-container">
    <doc-view v-show="hasData" ref="docView" :show-opt-bar="false" :init-subscribe="false" :item="item" />
  </div>
</template>
<script>
import DocView from '../doc/DocView'

export default {
  components: { DocView },
  data() {
    return {
      item: {},
      hasData: false
    }
  },
  created() {
    const docId = this.$route.params.docId
    this.$nextTick(() => {
      if (docId) {
        this.get('/doc/view', { id: docId }, function(resp) {
          const data = resp.data
          this.initDocInfoView(data)
          this.item = data
          this.setTitle(data.name)
          this.hasData = true
        })
      }
    })
  }
}
</script>
