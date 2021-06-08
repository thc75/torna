<template>
  <div>
    <el-dialog
      :title="$ts('exportDoc')"
      :visible.sync="dialogVisible"
    >
      <el-form
        ref="dialogForm"
        :model="dialogFormData"
        label-width="120px"
        size="mini"
      >
        <el-form-item :label="$ts('exportAs')">
          <template>
            <el-radio-group v-model="dialogFormData.style">
              <el-radio :label="1">{{ $ts('singlePage') }}</el-radio>
              <el-radio :label="2">{{ $ts('multiPage') }}</el-radio>
            </el-radio-group>
          </template>
        </el-form-item>
        <el-form-item :label="$ts('fileType')">
          <template>
            <el-radio-group v-model="dialogFormData.type">
              <el-radio label="html">html</el-radio>
              <el-radio label="md">markdown</el-radio>
            </el-radio-group>
          </template>
        </el-form-item>
        <el-form-item :label="$ts('selectDoc')">
          <el-radio-group v-model="dialogFormData.isAll">
            <el-radio :label="1">{{ $ts('allDocs') }}</el-radio>
            <el-radio :label="0">{{ $ts('partDocs') }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-show="dialogFormData.isAll === 0">
          <doc-tree ref="docTreeRef" readonly />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">{{ $ts('dlgCancel') }}</el-button>
        <el-button type="primary" :loading="loading" @click="onDialogSave">{{ $ts('dlgExport') }}</el-button>
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
          this.tipError(this.$ts('pleaseCheckDoc'))
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
