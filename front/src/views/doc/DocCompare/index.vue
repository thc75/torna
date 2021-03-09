<template>
  <div>
    <el-row :gutter="2">
      <el-col :span="4">
        <el-tree
          ref="tree"
          :data="treeData"
          :props="defaultProps"
          :highlight-current="true"
          node-key="id"
          empty-text="无变更记录"
          @node-click="handleNodeClick"
        />
      </el-col>
      <el-col v-show="treeData.length > 0" :span="10">
        <div class="version-title" :class="{ 'has-fixed': needFix }">
          <el-alert :title="currentChange" type="info" effect="dark" :closable="false" />
        </div>
        <doc-compare-view ref="oldView" />
      </el-col>
      <el-col v-show="treeData.length > 0" :span="10">
        <div class="version-title" :class="{ 'has-fixed': needFix }">
          <el-alert title="当前版本" type="info" effect="dark" :closable="false" />
        </div>
        <doc-compare-view ref="currentView" />
      </el-col>
    </el-row>
  </div>
</template>
<style>
.version-title {
  margin-bottom: 20px;
}
.has-fixed {
  position: fixed;
  top: 0;
  display: block;
  height: 1px;
  width: 100%;
  z-index: 99999;
}
</style>
<script>
import DocCompareView from '../DocCompareView'
import md5 from 'js-md5'

export default {
  name: 'DocCompare',
  components: { DocCompareView },
  props: {
    docId: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      treeData: [],
      currentData: {},
      defaultProps: {
        children: 'children',
        label: 'modifierTime'
      },
      currentChange: '',
      dialogDiv: null,
      needFix: false
    }
  },
  watch: {
    docId(docId) {
      this.loadData(docId)
    }
  },
  mounted() {
    this.dialogDiv = this.$parent.$refs.dialog
    this.dialogDiv.addEventListener('scroll', this.handlerScroll)
  },
  methods: {
    handlerScroll() {
      const scrollTop = this.dialogDiv.scrollTop
      this.needFix = scrollTop > 80
    },
    loadData(docId) {
      if (docId) {
        this.$refs.currentView.loadData(docId)
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
    buildLabel(data) {
      return data.modifierTime
    },
    handleNodeClick(data) {
      this.currentChange = this.buildLabel(data)
      this.$refs.tree.setCurrentKey(data.id)
      this.get('/doc/snapshot/get', { id: data.id }, resp => {
        const data = resp.data
        const content = data.content
        const docInfo = JSON.parse(content)
        docInfo.requestParams = this.convertTree(docInfo.requestParams)
        docInfo.responseParams = this.convertTree(docInfo.responseParams)
        this.$refs.oldView.setData(docInfo)
        const oldData = this.$refs.oldView.getData()
        const currentData = this.$refs.currentView.getData()
        this.compare(oldData, currentData)
      })
    },
    compare(otherData, currentData) {
      this.compareParams(otherData.pathParams, currentData.pathParams)
      this.compareParams(otherData.headerParams, currentData.headerParams)
      this.compareParams(otherData.requestParams, currentData.requestParams)
      this.compareParams(otherData.responseParams, currentData.responseParams)
      this.compareParams(otherData.errorCodeParams, currentData.errorCodeParams)
    },
    compareParams(otherParams, thisParams) {
      const thisJson = {}
      for (const thisParam of thisParams) {
        // this.setChanged(thisParam)
        thisJson[thisParam.id] = thisParam
      }
      for (const otherParam of otherParams) {
        const thisParam = thisJson[otherParam.id]
        // 如果没找到，表示已删除
        if (!thisParam) {
          this.setChanged(otherParam)
        } else {
          const otherMd5 = md5(this.buildParamContent(otherParam))
          const thisMd5 = md5(this.buildParamContent(thisParam))
          if (otherMd5 !== thisMd5) {
            this.setChanged(otherParam)
            this.setChanged(thisParam)
          }
          const otherChildren = otherParam.children || []
          const thisChildren = thisParam.children || []
          if (otherChildren.length > 0 && thisChildren.length > 0) {
            this.compareParams(otherChildren, thisChildren)
          }
        }
      }
    },
    buildParamContent(param) {
      return `${param.name}:${param.type}:${param.required}:${param.description}:${param.example}`
    },
    setChanged(obj) {
      if (obj) {
        obj._changed = true
      }
    }
  }
}
</script>
