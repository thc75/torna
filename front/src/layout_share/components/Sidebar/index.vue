<template>
  <div :class="{'has-logo':showLogo}">
    <el-scrollbar wrap-class="scrollbar-wrapper">
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
          @node-click="onNodeClick"
        />
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

export default {
  data() {
    return {
      filterText: '',
      currentSpaceId: '',
      treeData: [],
      spaceData: [],
      expandKeys: [],
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
    }
  },
  watch: {
    filterText(val) {
      this.$refs.tree.filter(val)
    }
  },
  mounted() {
    this.loadMenu()
  },
  methods: {
    loadMenu() {
      const id = this.$route.params.shareId
      this.get('/share/menu', { id: id }, resp => {
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
        if (node.id === docId) {
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
        const shareId = this.$route.params.shareId
        this.goRoute(`/share/${shareId}/${data.id}`)
      }
    }
  }
}
</script>
