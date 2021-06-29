<template>
  <div class="doc-tree">
    <el-input
      v-if="!viewMode"
      v-model="filterText"
      :placeholder="$ts('filterNameUrl')"
      size="mini"
      clearable
    >
    </el-input>
    <el-tree
      ref="tree"
      :data="treeRows"
      :props="props"
      node-key="id"
      show-checkbox
      @check="onCheckClick"
    >
      <span slot-scope="{ node, data }" class="custom-tree-node">
        <span>{{ node.label }}<span v-show="!data.isFolder && data.httpMethod" class="el-tree-label-tip">【{{ data.httpMethod }}】{{ data.url }}</span></span>
        <span v-if="!readonly && data.isFolder && isChecked(data)" @click.stop>
          <el-checkbox v-model="data.isShareFolder" :disabled="viewMode">{{ $ts('appendShare') }}</el-checkbox>
          <el-tooltip placement="top" :content="$ts('appendShareTip')">
            <i class="el-icon-question"></i>
          </el-tooltip>
        </span>
      </span>
    </el-tree>
  </div>
</template>
<script>
export default {
  props: {
    viewMode: {
      type: Boolean,
      default: false
    },
    readonly: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      treeData: [],
      props: {
        label: 'name',
        children: 'children',
        disabled: 'disabled'
      },
      filterText: ''
    }
  },
  computed: {
    treeRows() {
      let search = this.filterText.trim()
      if (!search) {
        return this.treeData
      }
      search = search.toLowerCase()
      return this.searchRow(search, this.treeData, this.searchContent, this.isFolder)
    }
  },
  methods: {
    load(moduleId, beforeFun, afterFun) {
      this.get('/doc/list', { moduleId: moduleId }, resp => {
        const data = resp.data
        if (beforeFun) {
          beforeFun.call(this, data)
        }
        this.treeData = this.convertTree(data)
        if (afterFun) {
          this.$nextTick(() => {
            afterFun.call(this, this.$refs.tree)
          })
        }
      })
    },
    isFolder(row) {
      return row.isFolder === 1
    },
    searchContent(searchText, row) {
      return (row.url && row.url.toLowerCase().indexOf(searchText) > -1) ||
        (row.name && row.name.toLowerCase().indexOf(searchText) > -1) ||
        (row.id && row.id.toLowerCase().indexOf(searchText) > -1)
    },
    onCheckClick(data, status) {
      const node = this.$refs.tree.getNode(data)
      if (!node.checked) {
        data.isShareFolder = false
        status.halfCheckedNodes.forEach(data => {
          data.isShareFolder = false
        })
      }
    },
    isChecked(data) {
      const keys = this.$refs.tree.getCheckedKeys()
      for (const key of keys) {
        if (key === data.id) {
          return true
        }
      }
      return false
    },
    // 包含半选节点
    getCheckedNodes() {
      return this.$refs.tree.getCheckedNodes(false, true)
    },
    getCheckedAllKeys() {
      const nodes = this.getCheckedNodes()
      return nodes.map(row => row.id)
    },
    getAllKeys() {
      return this.filterKeys(this.treeData)
    },
    filterKeys(treeData) {
      const keys = []
      const add = (data) => {
        data.forEach(row => {
          keys.push(row.id)
          const children = row.children
          if (children && children.length > 0) {
            add(children)
          }
        })
      }
      add(treeData)
      return keys
    },
    setCheckedKeys(keys) {
      this.$refs.tree.setCheckedKeys(keys)
    },
    setData(data) {
      this.treeData = data
    },
    getData() {
      return this.treeData
    },
    clear() {
      this.treeData = []
    },
    disable() {
      const tree = this.$refs.tree
      const disableRow = row => {
        // tree给含有默认勾选的树加disabled不生效
        // see: https://blog.csdn.net/qq_36341137/article/details/112644879
        tree.$set(row, 'disabled', true)
        const children = row.children || []
        children.forEach(child => disableRow(child))
      }
      this.treeData.forEach(row => {
        disableRow(row)
      })
    }
  }
}
</script>
<style lang="scss" scoped>
.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;
}
.el-tree-label-tip {color: #909399;font-size: 14px;margin: 6px;}
</style>
