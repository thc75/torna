<template>
  <div>
    <el-dialog
      title="导出文档"
      :visible.sync="dialogVisible"
      :close-on-click-modal="false"
      @close="() => {
        resetForm('dialogForm')
      }"
    >
      <el-form
        ref="dialogForm"
        :model="dialogFormData"
        label-width="120px"
        size="mini"
      >
        <el-form-item label="导出形式">
          <template>
            <el-radio-group v-model="dialogFormData.style">
              <el-radio :label="1">单页</el-radio>
              <el-radio :label="2">多页</el-radio>
            </el-radio-group>
          </template>
        </el-form-item>
        <el-form-item label="格式">
          <template>
            <el-radio-group v-model="dialogFormData.type">
              <el-radio label="html">html</el-radio>
              <el-radio label="md">markdown</el-radio>
            </el-radio-group>
          </template>
        </el-form-item>
        <el-form-item label="选择文档">
          <el-radio-group v-model="dialogFormData.isAll">
            <el-radio :label="1">全部文档</el-radio>
            <el-radio :label="0">部分文档</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-show="dialogFormData.isAll === 0">
          <doc-tree ref="docTreeRef" readonly />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="loading" @click="onDialogSave">导 出</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import DocTree from '@/components/DocTree'
import ExportUtil from '@/utils/export'
const EXPORT_STYLE = {
  ALL_IN_ONE: 1,
  MULTI_PAGE: 2
}
const EXPORT_TYPE = {
  HTML: 'html',
  MARKDOWN: 'md'
}
export default {
  components: { DocTree },
  data() {
    return {
      dialogVisible: false,
      dialogFormData: {
        style: EXPORT_STYLE.ALL_IN_ONE,
        type: EXPORT_TYPE.HTML,
        isAll: 1
      },
      loading: false
    }
  },
  methods: {
    show(data) {
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.docTreeRef.setData(data)
      })
    },
    onDialogSave() {
      let keys
      if (this.dialogFormData.isAll === 1) {
        keys = this.$refs.docTreeRef.getAllKeys()
      } else {
        keys = this.$refs.docTreeRef.getCheckedAllKeys()
        if (!keys || keys.length === 0) {
          this.tipError('请勾选文档')
          return
        }
      }
      if (keys.length === 0) {
        return
      }
      this.loading = true
      this.post('/doc/detail/search', { docIdList: keys }, resp => {
        const docInfoList = resp.data
        this.doExport(docInfoList)
        this.loading = false
      })
    },
    doExport(docInfoList) {
      const type = this.dialogFormData.type
      const style = this.dialogFormData.style
      switch (type) {
        case EXPORT_TYPE.HTML:
          if (style === EXPORT_STYLE.ALL_IN_ONE) {
            ExportUtil.exportHtmlAllInOne(docInfoList)
          } else {
            ExportUtil.exportHtmlMultiPages(docInfoList)
          }
          break
        case EXPORT_TYPE.MARKDOWN:
          if (style === EXPORT_STYLE.ALL_IN_ONE) {
            ExportUtil.exportMarkdownAllInOne(docInfoList)
          } else {
            ExportUtil.exportMarkdownMultiPages(docInfoList)
          }
          break
        default:
      }
    }
  }
}
</script>
