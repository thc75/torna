<template>
  <div>
    <el-select v-model="currentSpaceId" size="mini" class="space-select" @change="onSpaceSelect">
      <el-option v-for="space in spaceData" :key="space.id" :value="space.id" :label="space.name">
        {{ space.name }}
      </el-option>
    </el-select>
    <div class="menu-tree">
      <el-input
        v-show="treeData.length > 0"
        v-model="filterText"
        prefix-icon="el-icon-search"
        :placeholder="$ts('apiFilter')"
        style="margin-bottom: 10px;"
        size="mini"
        clearable
      />
      <el-tree
        ref="tree"
        :data="treeData"
        :props="defaultProps"
        :filter-node-method="filterNode"
        :highlight-current="true"
        :expand-on-click-node="true"
        :default-expanded-keys="expandKeys"
        :empty-text="$ts('noData')"
        node-key="id"
        class="filter-tree"
        :indent="indent"
        :show-checkbox="showCheckbox"
        @node-click="onNodeClick"
      >
        <span slot-scope="{ node, data }">
          <span>
            <i :class="getClassName(data)"></i>
            <http-method v-if="data.httpMethod" :method="data.httpMethod" /> {{ node.label }}
            <dubbo-service-tip v-if="data.type === types.TYPE_FOLDER && data.docType === getEnums().DOC_TYPE.DUBBO" :doc-id="data.docId" />
          </span>
          <span v-if="showUrl && data.url" class="doc-select-url">
            {{ data.url }}
          </span>
        </span>
      </el-tree>
    </div>
  </div>
</template>
<script>
import HttpMethod from '@/components/HttpMethod'
import DubboServiceTip from '@/components/DubboServiceTip'

const file_typ_map = {
  '0': 'el-icon-s-management',
  '1': 'el-icon-box',
  '2': 'el-icon-folder',
  '3': ''
}

export default {
  components: { HttpMethod, DubboServiceTip },
  props: {
    nodeClick: {
      type: Function,
      default: () => {}
    },
    showCheckbox: {
      type: Boolean,
      default: false
    },
    loadInit: {
      type: Boolean,
      default: true
    },
    indent: {
      type: Number,
      default: 10
    },
    showUrl: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      filterText: '',
      currentSpaceId: '',
      treeData: [],
      docIdMap: {},
      spaceData: [],
      expandKeys: [],
      defaultProps: {
        children: 'children',
        label: 'label'
      },
      types: this.getEnums().FOLDER_TYPE
    }
  },
  watch: {
    filterText(val) {
      this.$refs.tree.filter(val)
    }
  },
  mounted() {
    if (this.loadInit) {
      this.init()
    }
  },
  methods: {
    init() {
      if (this.spaceData.length === 0) {
        this.get('/space/listNormal', {}, resp => {
          const data = resp.data
          if (data.length > 0) {
            this.spaceData = data
            this.currentSpaceId = data[0].id
            this.loadMenu(this.currentSpaceId)
          }
        })
      }
    },
    loadMenu(spaceId) {
      if (spaceId) {
        this.get('/doc/view/data', { spaceId: spaceId }, resp => {
          const data = resp.data
          this.initDocMap(data)
          const currentNode = this.getCurrentNode(data)
          this.treeData = this.convertTree(data)
          this.$nextTick(() => {
            this.setCurrentNode(currentNode)
          })
        })
      }
    },
    initDocMap(data) {
      const docIdMap = this.docIdMap
      data.forEach(row => {
        if (row.docId) {
          docIdMap[row.docId] = row.id
        }
      })
    },
    getCurrentNode(data) {
      const docId = this.$route.params.docId
      let currentNode
      for (let i = 0; i < data.length; i++) {
        const node = data[i]
        if (node.docId === docId) {
          currentNode = node
          break
        }
      }
      return currentNode
    },
    onSpaceSelect() {
      this.loadMenu(this.currentSpaceId)
    },
    setCurrentNode(currentNode) {
      if (currentNode) {
        const tree = this.$refs.tree
        tree.setCurrentKey(currentNode.id)
        this.expandKeys = [currentNode.parentId]
      }
    },
    filterNode(value, row) {
      if (!value) return true
      const searchText = value.toLowerCase()
      return (row.docId && row.docId.toLowerCase().indexOf(searchText) > -1) ||
        (row.label && row.label.toLowerCase().indexOf(searchText) > -1) ||
        (row.url && row.url.toLowerCase().indexOf(searchText) > -1)
    },
    getClassName(data) {
      return file_typ_map[data.type + '']
    },
    // 树点击事件
    onNodeClick(data, node, tree) {
      this.nodeClick(data, node, tree)
    },
    getTree() {
      return this.$refs.tree
    },
    /**
     * @param leafOnly 是否只是叶子节点，默认值为 false
     * @param includeHalfChecked 是否包含半选节点，默认值为 false
     */
    getCheckedNodes(leafOnly, includeHalfChecked) {
      if (leafOnly === undefined) {
        leafOnly = false
      }
      if (includeHalfChecked === undefined) {
        includeHalfChecked = false
      }
      return this.getTree().getCheckedNodes(leafOnly, includeHalfChecked)
    },
    getCheckedDocIds() {
      return this.getTree().getCheckedKeys(true)
    },
    setCheckedKeys(keys) {
      this.getTree().setCheckedKeys(keys)
    },
    /**
     * 取消勾选
     */
    clearChecked() {
      this.treeData.forEach(row => {
        this.getTree().setChecked(row.id, false, true)
      })
    }
  }
}
</script>
<style scoped>
.menu-tree {
  padding: 10px;
  font-size: 14px !important;
}
.space-select {
  padding: 10px 10px 0 10px;
}
.doc-select-url {
  margin-left: 10px;
  color: #909399;
}
</style>
