<template>
  <div class="doc-view-container">
    <el-tabs v-show="load" v-model="active" @tab-click="onTabSelect">
      <el-tab-pane name="info">
        <span slot="label"><i class="el-icon-document" /> {{ $ts('apiInfo') }}</span>
        <doc-view ref="docView" :show-opt-bar="false" :init-subscribe="false" :item="infoItem" />
      </el-tab-pane>
      <el-tab-pane v-if="isShowDebug && infoItem.debugEnvs.length > 0" name="debug">
        <span slot="label"><i class="el-icon-s-promotion" /> {{ $ts('debugApi') }}</span>
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
      mockItem: {},
      isShowDebug: false
    }
  },
  created() {
    const docId = this.$route.params.docId
    const shareId = this.$route.params.shareId
    if (docId) {
      this.$nextTick(() => {
        this.get('/doc/share/view', { docId: docId, shareConfigId: shareId }, function(resp) {
          this.load = true
          const data = resp.data
          this.initDocInfoView(data)
          this.item = data
          this.infoItem = data
          this.setTitle(data.name)
          this.showDebug()
        })
      })
    }
  },
  methods: {
    onTabSelect(tab) {
      this[`${tab.name}Item`] = this.item
    },
    showDebug() {
      const shareId = this.$route.params.shareId
      if (shareId) {
        this.get('/share/get', { id: shareId }, resp => {
          this.isShowDebug = resp.data.isShowDebug === 1
        })
      }
    }
  }
}
</script>
