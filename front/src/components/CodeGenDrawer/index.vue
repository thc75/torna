<template>
  <div>
    <el-drawer
      :title="$t('codeGenerate')"
      :visible.sync="drawer"
      :direction="direction"
      size="25%"
      :with-header="true"
      :before-close="handleClose"
    >
      <template-tree
        ref="templateTree"
        :init-load="false"
        style="margin: 10px"
        :btn-text="`[${$t('generate')}]`"
        :btn-click="onGen"
      />
    </el-drawer>
    <el-dialog
      :title="fileInfo.docName"
      close-on-press-escape
      lock-scroll
      :visible.sync="resultDlgShow"
    >
      <el-button
        type="text"
        icon="el-icon-document-copy"
        @click="onCopy"
      >
        {{ $t('copyCode') }}
      </el-button>
      <el-button
        icon="el-icon-copy-document"
        type="text"
        @click="onCopyAndClose"
      >
        {{ $t('copyAndClose') }}
      </el-button>
      <codemirror
        v-model="fileInfo.content"
        :options="cmOptions"
      />
    </el-dialog>
  </div>
</template>
<script>
import TemplateTree from '@/components/TemplateTree'
import { codemirror } from 'vue-codemirror'
import 'codemirror/theme/neat.css'
import 'codemirror/lib/codemirror.css'
require('codemirror/mode/htmlembedded/htmlembedded')

export default {
  components: { TemplateTree, codemirror },
  data() {
    return {
      drawer: false,
      direction: 'rtl',
      list: [],
      docId: '',
      resultDlgShow: false,
      fileInfo: {
        content: '',
        docName: ''
      },
      cmOptions: {
        value: '',
        mode: 'text/html',
        lineNumbers: true,
        theme: 'neat',
        readOnly: true
      }
    }
  },
  methods: {
    show(docId) {
      this.drawer = true
      this.docId = docId
      this.$nextTick(() => {
        document.body.style.overflowY = 'hidden'
        this.$refs.templateTree.reload()
      })
    },
    onGen(data) {
      this.post('doc/gen/generate', { docId: this.docId, templateId: data.id }, resp => {
        this.resultDlgShow = true
        this.fileInfo = resp.data
      })
    },
    handleClose() {
      this.drawer = false
      document.body.style.overflowY = 'auto'
    },
    onCopy() {
      this.copyText(this.fileInfo.content)
    },
    onCopyAndClose() {
      this.onCopy()
      this.resultDlgShow = false
      this.handleClose()
    }
  }
}
</script>
<style>
.CodeMirror {
  height: auto !important;
}
</style>
