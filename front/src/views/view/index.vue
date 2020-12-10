<template>
  <div class="app-container">
    <el-tabs v-show="docId" v-model="active" type="card">
      <el-tab-pane name="info">
        <span slot="label"><i class="el-icon-document"></i> 接口信息</span>
        <doc-view ref="docView" url="/doc/view/detail" />
      </el-tab-pane>
      <el-tab-pane name="debug">
        <span slot="label"><i class="el-icon-s-promotion"></i> 调试接口</span>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script>
import DocView from '../doc/DocView'

export default {
  components: { DocView },
  data() {
    return {
      active: 'info',
      docId: ''
    }
  },
  created() {
    this.docId = this.$route.params.docId
    this.$nextTick(() => {
      this.$refs.docView.loadData(this.docId)
    })
  }
}
</script>
