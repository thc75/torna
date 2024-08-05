<template>
  <div class="doc-tree">
    <el-tabs
      v-if="moduleData.length > 0"
      v-model="active"
      type="border-card"
      class="module-tabs"
      @tab-click="onTabClick"
    >
      <el-tab-pane
        v-for="item in moduleData"
        :key="item.id"
        :label="item.name"
        :name="item.id"
        :title="item.name"
      />
    </el-tabs>
    <el-input
      v-if="!viewMode"
      v-model="filterText"
      :placeholder="$t('filterNameUrl')"
      size="mini"
      clearable
    />
    <el-tree
      ref="tree"
      :data="treeRows"
      :props="props"
      node-key="id"
      show-checkbox
      :default-checked-keys="chooseModuleDocIds"
      :default-expanded-keys="chooseModuleDocIds"
      @check="onCheckClick"
    >
      <span slot-scope="{ node, data }" class="custom-tree-node">
        <span>{{ node.label }}<span v-show="!data.isFolder && data.httpMethod" class="el-tree-label-tip">【{{ data.httpMethod }}】{{ data.url }}</span></span>
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
    moduleData: {
      type: Array, // 指定类型为数组
      default: () => []
    },
    moduleSourceIdMap: {
      type: Object,
      default: () => {}
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
      filterText: '',
      moduleId: '',
      active: '',
      chooseModuleDocIds: []
    }
  },
  computed: {
    treeRows() {
      let search = this.filterText.trim()
      // const currentTab = this.active
      // this.setChooseModuleDocIdsData(this.moduleSourceIdMap[currentTab] || [])
      if (!search) {
        return this.treeData
      }
      search = search.toLowerCase()
      return this.searchRow(search, this.treeData, this.searchContent, this.isFolder)
    }
  },
  mounted() {
    if (this.moduleData) {
      var firstModule = this.moduleData[0]
      if (firstModule) {
        var moduleId = firstModule.id
        // 这里触发文档树选择第一个模块初始化
        if (this.moduleSourceIdMap) {
          // 初始化数据
          this.chooseModuleDocIds = this.moduleSourceIdMap[moduleId] || []
        }
        this.onModuleSelect(moduleId)
      }
    }
  },
  methods: {
    isFolder(row) {
      return row.isFolder === 1
    },
    searchContent(searchText, row) {
      return (row.url && row.url.toLowerCase().indexOf(searchText) > -1) ||
        (row.name && row.name.toLowerCase().indexOf(searchText) > -1) ||
        (row.id && row.id.toLowerCase().indexOf(searchText) > -1)
    },
    onCheckClick(data, status) {
      const currentTab = this.active
      // 初始化
      if (!this.moduleSourceIdMap[currentTab]) {
        this.$set(this.moduleSourceIdMap, currentTab, [])
      }
      this.moduleSourceIdMap[currentTab] = status.checkedKeys || []
      console.info('moduleSourceIdMap', JSON.stringify(this.moduleSourceIdMap))
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
    changeModuleDocIds() {
      const currentTab = this.active
      this.chooseModuleDocIds = this.moduleSourceIdMap[currentTab] || []
    },
    getModuleSourceIdMap() {
      return this.moduleSourceIdMap
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
    setChooseModuleDocIdsData(data) {
      this.chooseModuleDocIds = data
    },
    getData() {
      return this.treeData
    },
    setData(data) {
      this.treeData = data
    },
    clear() {
      this.active = ''
      this.treeData = []
      this.moduleSourceIdMap = {}
      this.chooseModuleDocIds = []
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
    },
    onTabClick(tab) {
      const name = tab.name
      this.active = name
      this.filterText = ''
      for (const module of this.moduleData) {
        if (name === module.id) {
          this.onModuleSelect(module.id)
          this.changeModuleDocIds()
          break
        }
      }
    },
    onModuleSelect(moduleId, callback) {
      console.info('onModuleSelect', moduleId)
      if (moduleId) {
        this.moduleId = moduleId
        this.active = moduleId
      }
      const searchData = {
        moduleId: this.moduleId
      }
      Object.assign(searchData, this.searchForm)
      this.post('/doc/list-v2', searchData, function(resp) {
        this.tableData = this.convertTree(resp.data)
        this.setData(this.tableData)
        callback && callback.call(this)
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
