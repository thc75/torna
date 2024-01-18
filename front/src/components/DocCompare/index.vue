<template>
  <div>
    <el-dialog
      :title="$t('viewDoc')"
      :visible.sync="dlgShow"
      width="70%"
      append-to-body
    >
      <doc-view v-show="docInfo.type === getEnums().DOC_TYPE.HTTP" ref="docViewOld" :show-opt-bar="false" :init-subscribe="false" />
      <div v-show="docInfo.type === getEnums().DOC_TYPE.CUSTOM" v-html="docInfo.description"></div>
      <mavon-editor
        v-show="docInfo.type === getEnums().DOC_TYPE.MARKDOWN"
        v-model="docInfo.description"
        :boxShadow="false"
        :subfield="false"
        defaultOpen="preview"
        :editable="false"
        :toolbarsFlag="false"
      />
    </el-dialog>
  </div>
</template>
<script>
import { init_docInfo_view } from '@/utils/common'
import { mavonEditor } from 'mavon-editor'
export default {
  name: 'DocCompare',
  // 异步加载，处理组件循环依赖问题
  components: { DocView: () => import('@/components/DocView'), mavonEditor },
  data() {
    return {
      dlgShow: false,
      docInfo: {
        type: -1,
        description: ''
      }
    }
  },
  methods: {
    show(md5) {
      this.dlgShow = true
      this.$nextTick(() => {
        // this.get('/doc/view/detail', { id: docId }, function(resp) {
        //   const data = resp.data
        //   init_docInfo_view(data)
        //   this.$refs.docViewCurrent.setData(data)
        // })
        this.get('doc/snapshot/docinfo', { md5: md5 }, resp => {
          const data = resp.data
          init_docInfo_view(data)
          Object.assign(this.docInfo, data)
          if (data.type === this.getEnums().DOC_TYPE.HTTP) {
            this.$refs.docViewOld.setData(data);
          }
        })
      })
    }
  }
}
</script>
