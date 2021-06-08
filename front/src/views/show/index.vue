<template>
  <div v-show="hasData" class="app-container">
    <el-tabs active-name="info" type="card" @tab-click="onTabSelect">
      <el-tab-pane name="info">
        <span slot="label"><i class="el-icon-document"></i> {{ $ts('apiInfo') }}</span>
        <doc-view ref="docView" :show-opt-bar="false" :init-subscribe="false" :item="item" />
      </el-tab-pane>
      <el-tab-pane name="debug">
        <span slot="label"><i class="el-icon-s-promotion"></i> {{ $ts('debugApi') }}</span>
        <doc-debug :item="debugItem" :internal="false" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script>
import DocView from '../doc/DocView'
import DocDebug from '@/components/DocDebug'

export default {
  components: { DocView, DocDebug },
  data() {
    return {
      item: {},
      debugItem: {},
      hasData: false
    }
  },
  created() {
    const docId = this.$route.params.docId
    this.$nextTick(() => {
      if (docId) {
        this.get('/doc/viewShow', { id: docId }, function(resp) {
          const data = resp.data
          this.initDocInfoView(data)
          this.item = data
          this.setTitle(data.name)
          this.hasData = true
        })
      }
    })
  },
  methods: {
    onTabSelect(tab) {
      this.selectTab(tab.name)
    },
    selectTab(name) {
      this[`${name}Item`] = this.item
    }
  }
}
</script>
