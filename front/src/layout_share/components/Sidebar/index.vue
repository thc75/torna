<template>
  <div :class="{'has-logo':showLogo}">
    <el-scrollbar wrap-class="scrollbar-wrapper">
      <doc-select ref="docSelect" :load-init="false" :show-space="false" :node-click="onNodeClick" />
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
import DocSelect from '@/components/DocSelect'

export default {
  components: { DocSelect },
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
      },
      types: this.getEnums().FOLDER_TYPE
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
      this.$nextTick(() => {
        this.get('/share/menu', { id: id }, resp => {
          const data = resp.data
          this.$refs.docSelect.loadData(data)
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
      if (data.type === this.types.TYPE_DOC) {
        const shareId = this.$route.params.shareId
        this.toRoute({ path: `/share/${shareId}/${data.id}` }, data.label)
      }
    }
  }
}
</script>
