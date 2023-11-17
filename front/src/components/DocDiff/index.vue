<template>
  <div>
    <el-row :gutter="2">
      <el-col :span="24">
        <div class="version-bar">
          <div style="float: right;display: inline-block">当前文档</div>
        </div>
        <div id="diffView" style="margin-top: 20px;"></div>
      </el-col>
    </el-row>
  </div>
</template>
<style>
.version-bar div {
  color: #303133;
}
.CodeMirror-merge, .CodeMirror-merge .CodeMirror {
  padding: 0;
  height: auto !important;
}

.CodeMirror-vscrollbar {
  height: auto;
  overflow-y: hidden;
  overflow-x: auto;
}
</style>
<script>
import { CodeMirror } from 'vue-codemirror'
import 'codemirror/lib/codemirror.css'
// merge js
import 'codemirror/addon/merge/merge.js'
// merge css
import 'codemirror/addon/merge/merge.css'
// google DiffMatchPatch
import DiffMatchPatch from 'diff-match-patch'
// DiffMatchPatch config with global
window.diff_match_patch = DiffMatchPatch
window.DIFF_DELETE = -1
window.DIFF_INSERT = 1
window.DIFF_EQUAL = 0
import MarkdownUtil from '@/utils/convert-markdown'

function initUI(oldStr, newStr) {
  const target = document.getElementById('diffView')
  target.innerHTML = ''
  CodeMirror.MergeView(target, {
    value: oldStr,
    origLeft: null,
    orig: newStr,
    connect: 'align',
    mode: 'text/html',
    lineNumbers: true,
    collapseIdentical: false,
    highlightDifferences: true,
    revertButtons: false,
    readOnly: true
  })
}

export default {
  name: 'DocMatchDiff',
  props: {
    docInfo: {
      type: Object,
      default: () => {}
    }
  },
  data() {
    return {
      treeData: [],
      defaultProps: {
        children: 'children',
        label: 'modifierTime'
      },
      currentChange: '',
      remarkOther: '',
      remarkCurrent: ''
    }
  },
  watch: {
    docInfo(docInfo) {
      this.loadData(docInfo.id)
    }
  },
  methods: {
    loadData(docId) {
      if (docId) {
        this.loadTree(docId)
      }
    },
    loadTree(docId) {
      this.get('/doc/snapshot/list', { docId: docId }, resp => {
        this.treeData = resp.data
        if (this.treeData.length > 0) {
          this.$nextTick(() => {
            this.handleNodeClick(this.treeData[0])
          })
        }
      })
    },
    handleNodeClick(data) {
      this.currentChange = data.modifierTime
      this.$refs.tree.setCurrentKey(data.id)
      this.get('/doc/snapshot/get', { id: data.id }, resp => {
        const data = resp.data
        const content = data.content
        const docInfo = JSON.parse(content)
        this.initDocInfo(docInfo)
        const currentData = this.docInfo
        this.compare(docInfo, currentData)
      })
    },
    compare(otherData, currentData) {
      this.remarkOther = otherData.remark
      this.remarkCurrent = currentData.remark
      const oldStr = MarkdownUtil.toMarkdown(otherData)
      const newStr = MarkdownUtil.toMarkdown(currentData)
      initUI(oldStr, newStr)
    },
    compareWithMd5(md5Old, md5New) {
      this.get('/doc/snapshot/compare-info', { md5Old: md5Old, md5New: md5New }, resp => {
        const data = resp.data
        const docOld = data.docOld
        const docNew = data.docNew
        this.currentChange = docNew.gmtModified
        this.initDocInfo(docOld)
        this.initDocInfo(docNew)
        this.compare(docOld, docNew)
      })
    }
  }
}
</script>
