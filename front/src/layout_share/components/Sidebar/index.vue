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
          const rows = resp.data
          this.$refs.docSelect.loadData(rows)
          const docId = this.$route.params.docId
          if (!docId && rows.length > 0) {
            const data = this.findFirstPage(rows)
            this.onNodeClick(data)
          }
        })
      })
    },
    findFirstPage(rows) {
      for (const row of rows) {
        if (row.type === this.types.TYPE_DOC) {
          return row
        } else if (row.children && row.children.length > 0) {
          return this.findFirstPage(row.children)
        }
      }
      return {}
    },
    // 树点击事件
    onNodeClick(data) {
      if (!data) {
        return
      }
      if (data.type === this.types.TYPE_DOC) {
        const shareId = this.$route.params.shareId
        this.toRoute({ path: `/share/${shareId}/${data.id}` }, data.label)
        this.$nextTick(() => {
          this.$refs.docSelect.setCurrentNode(data)
        })
      }
    }
  }
}
</script>
