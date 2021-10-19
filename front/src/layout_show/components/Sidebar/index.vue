<template>
  <div :class="{'has-logo':showLogo}">
    <el-scrollbar wrap-class="scrollbar-wrapper">
      <div class="menu-tree">
        <el-radio-group v-model="expandAll" size="mini" style="padding-bottom: 10px;" @change="onTriggerStatus">
          <el-radio-button :label="true">{{ $ts('expand') }}</el-radio-button>
          <el-radio-button :label="false">{{ $ts('collapse') }}</el-radio-button>
        </el-radio-group>
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
          :data="treeRows"
          :props="defaultProps"
          :filter-node-method="filterNode"
          :highlight-current="true"
          :expand-on-click-node="true"
          :default-expanded-keys="expandKeys"
          :empty-text="$ts('noData')"
          node-key="id"
          class="filter-tree"
          @node-click="onNodeClick"
        >
          <span slot-scope="{ node, data }">
            <span>
              <i :class="{ 'el-icon-folder': data.isFolder }"></i>
              <http-method v-if="data.httpMethod" :method="data.httpMethod" /> {{ node.label }}
            </span>
          </span>
        </el-tree>
      </div>
    </el-scrollbar>
  </div>
</template>
<style>
.menu-tree {
  padding: 10px;
  font-size: 14px !important;
}
</style>
<script>
import { mapGetters } from 'vuex'
import HttpMethod from '@/components/HttpMethod'

export default {
  components: { HttpMethod },
  data() {
    return {
      filterText: '',
      currentSpaceId: '',
      treeData: [],
      expandKeys: [],
      expandAll: false,
      defaultProps: {
        children: 'children',
        label: 'name'
      }
    }
  },
  computed: {
    ...mapGetters([
      'sidebarView'
    ]),
    showLogo() {
      return this.$store.state.settings.sidebarLogo
    },
    treeRows() {
      const search = this.filterText.trim().toLowerCase()
      return this.searchDoc(search, this.treeData, this.searchContent, this.isFolder)
    }
  },
  watch: {
    filterText(val) {
      this.$refs.tree.filter(val)
    }
  },
  mounted() {
    this.expandAll = (this.getAttr(this.getTriggerStatusKey()) || 'false') === 'true'
    this.loadMenu()
  },
  methods: {
    loadMenu() {
      this.expandKeys = []
      const id = this.$route.params.showId
      this.get('/compose/doc/menu', { projectId: id }, resp => {
        const data = resp.data
        this.treeData = this.convertTree(data)
        const currentNode = this.getCurrentNode(data)
        this.$nextTick(() => {
          this.setCurrentNode(currentNode)
        })
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
      return (row.id && row.id.toLowerCase().indexOf(searchText) > -1) ||
        (row.name && row.name.toLowerCase().indexOf(searchText) > -1) ||
        (row.url && row.url.toLowerCase().indexOf(searchText) > -1)
    },
    // 树点击事件
    onNodeClick(data, node, tree) {
      if (!data.isFolder) {
        const showId = this.$route.params.showId
        this.goRoute(`/show/${showId}/${data.docId}`)
      }
    },
    onTriggerStatus(val) {
      this.setAttr(this.getTriggerStatusKey(), val)
      this.loadMenu()
    },
    getTriggerStatusKey() {
      return `torna.doc.show.tree.trigger`
    },
    searchDoc(search, rows, searchHandler, folderHandler) {
      if (search.length === 0) {
        this.addExpandKeys(rows)
        return rows
      }
      if (!folderHandler) {
        folderHandler = (row) => {
          return row.isFolder === 1
        }
      }
      const ret = []
      for (const row of rows) {
        // 找到分类
        if (folderHandler(row)) {
          this.addExpandKey(row)
          if (searchHandler(search, row)) {
            ret.push(row)
          } else {
            // 分类名字没找到，需要从子文档中找
            const children = row.children || []
            const searchedChildren = this.searchDoc(search, children, searchHandler, folderHandler)
            // 如果子文档中有
            if (searchedChildren && searchedChildren.length > 0) {
              const rowCopy = Object.assign({}, row)
              rowCopy.children = searchedChildren
              ret.push(rowCopy)
            }
          }
        } else {
          // 不是分类且被找到
          if (searchHandler(search, row)) {
            ret.push(row)
          }
        }
      }
      return ret
    },
    addExpandKeys(rows) {
      if (this.expandAll) {
        rows.forEach(row => {
          if (this.isFolder(row)) {
            this.addExpandKey(row)
          }
          const children = row.children
          if (children && children.length > 0) {
            this.addExpandKeys(children)
          }
        })
      }
    },
    addExpandKey(row) {
      if (this.expandAll && this.isFolder(row)) {
        this.expandKeys.push(row.id)
      }
    },
    searchContent(searchText, row) {
      return (row.url && row.url.toLowerCase().indexOf(searchText) > -1) ||
        (row.name && row.name.toLowerCase().indexOf(searchText) > -1) ||
        (row.docId && row.docId.toLowerCase().indexOf(searchText) > -1)
    },
    isFolder(row) {
      return row.isFolder === 1
    }
  }
}
</script>
