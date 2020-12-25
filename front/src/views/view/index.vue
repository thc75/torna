<template>
  <div class="app-container">
    <el-tabs v-show="load" v-model="active" type="card">
      <el-tab-pane name="info">
        <span slot="label"><i class="el-icon-document"></i> 接口信息</span>
        <doc-view ref="docView" :item="item" />
      </el-tab-pane>
      <el-tab-pane name="debug">
        <span slot="label"><i class="el-icon-s-promotion"></i> 调试接口</span>
        <doc-debug :item="item" />
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
      item: {}
    }
  },
  created() {
    const docId = this.$route.params.docId
    this.$nextTick(() => {
      if (docId) {
        this.get('/doc/view/detail', { id: docId }, function(resp) {
          this.load = true
          const data = resp.data
          this.initDocInfo(data)
          this.item = data
        })
      }
    })
  }
}
</script>
