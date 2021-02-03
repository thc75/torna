<template>
  <div :class="{'has-logo':showLogo}">
    <logo v-if="showLogo" :collapse="false" />
    <el-scrollbar wrap-class="scrollbar-wrapper">
      <el-select v-show="treeData.length > 0" v-model="currentSpaceId" size="mini" class="space-select" @change="onSpaceSelect">
        <el-option v-for="space in spaceData" :key="space.id" :value="space.id" :label="space.name">
          {{ space.name }}
        </el-option>
      </el-select>
      <div class="menu-tree">
        <el-input
          v-show="treeData.length > 0"
          v-model="filterText"
          prefix-icon="el-icon-search"
          placeholder="过滤：支持文档标题、url"
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
          empty-text="无数据"
          node-key="id"
          class="filter-tree"
          :indent="10"
          @node-click="onNodeClick"
        >
          <span slot-scope="{ node, data }">
            <span>
              <i :class="getClassName(data)"></i>
              {{ node.label }}
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
.space-select {
  padding: 10px 10px 0 10px;
}
</style>
<script>
import { mapGetters } from 'vuex'
import Logo from '@/components/Logo'

const file_typ_map = {
  '0': 'el-icon-s-management',
  '1': 'el-icon-box',
  '2': 'el-icon-folder',
  '3': 'el-icon-document'
}

export default {
  components: { Logo },
  data() {
    return {
      filterText: '',
      currentSpaceId: '',
      treeData: [],
      spaceData: [],
      expandKeys: [],
      defaultProps: {
        children: 'children',
        label: 'label'
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
    this.loadSpace()
  },
  methods: {
    loadSpace() {
      this.loadSpaceData((data, spaceId) => {
        this.spaceData = data
        this.currentSpaceId = spaceId
        this.loadMenu(spaceId)
      })
    },
    loadMenu(spaceId) {
      if (spaceId) {
        this.get('/doc/view/data', { spaceId: spaceId }, resp => {
          const data = resp.data
          const currentNode = this.getCurrentNode(data)
          this.treeData = this.convertTree(data)
          this.$nextTick(() => {
            this.setCurrentNode(currentNode)
          })
        })
      }
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
      if (data.docId) {
        this.goRoute(`/view/doc/${data.docId}`)
      }
    }
  }
}
</script>
