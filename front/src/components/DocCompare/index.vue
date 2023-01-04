<template>
  <div>
    <el-dialog
      :title="$ts('viewDoc')"
      :visible.sync="dlgShow"
      width="80%"
      append-to-body
    >
      <el-row :gutter="10">
        <el-col :span="24">
          <doc-view ref="docViewOld" :show-opt-bar="false" :init-subscribe="false" />
        </el-col>
<!--        <el-col :span="12">-->
<!--          <el-divider content-position="center">{{ $ts('currentDoc') }}</el-divider>-->
<!--          <doc-view ref="docViewCurrent" :show-opt-bar="false" :init-subscribe="false" />-->
<!--        </el-col>-->
      </el-row>
    </el-dialog>
  </div>
</template>
<script>
import { init_docInfo_view } from '@/utils/common'
export default {
  name: 'DocCompare',
  // 异步加载，处理组件循环依赖问题
  components: { DocView: () => import('@/components/DocView') },
  data() {
    return {
      dlgShow: false
    }
  },
  methods: {
    show(md5Old, docId) {
      this.dlgShow = true
      this.$nextTick(() => {
        // this.get('/doc/view/detail', { id: docId }, function(resp) {
        //   const data = resp.data
        //   init_docInfo_view(data)
        //   this.$refs.docViewCurrent.setData(data)
        // })
        this.get('doc/snapshot/docinfo', { md5: md5Old }, resp => {
          const data = resp.data
          // console.log(data)
          init_docInfo_view(data)
          this.$refs.docViewOld.setData(data)
        })
      })
    }
  }
}
</script>
