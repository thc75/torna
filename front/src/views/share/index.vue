<template>
  <div class="app-container">
    <el-tabs v-show="load" v-model="active" type="card" @tab-click="onTabSelect">
      <el-tab-pane name="info">
        <span slot="label"><i class="el-icon-document"></i> {{ $ts('apiInfo') }}</span>
        <doc-view ref="docView" :show-opt-bar="false" :init-subscribe="false" :item="infoItem" />
      </el-tab-pane>
      <el-tab-pane name="debug">
        <span slot="label"><i class="el-icon-s-promotion"></i> {{ $ts('debugApi') }}</span>
        <doc-debug :item="debugItem" />
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
      active: 'info',
      load: false,
      item: {},
      infoItem: {},
      debugItem: {},
      mockItem: {}
    }
  },
  created() {
    const docId = this.$route.params.docId
    this.$nextTick(() => {
      if (docId) {
        this.get('/doc/view', { id: docId }, function(resp) {
          this.load = true
          const data = resp.data
          this.initDocInfoView(data)
          this.item = data
          this.infoItem = data
          this.setTitle(data.name)
        })
      }
    })
  },
  methods: {
    onTabSelect(tab) {
      this[`${tab.name}Item`] = this.item
    }
  }
}
</script>
