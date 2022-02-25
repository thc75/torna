<template>
  <div>
    <div class="select-area">
      <el-cascader
        v-model="currentProjectId"
        :options="projectData"
        :props="{ expandTrigger: 'hover', label: 'name', value: 'id', children: 'projects' }"
        class="project-select"
        size="mini"
        @change="onProjectSelect"
      />
    </div>
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
        :data="treeRows"
        :props="defaultProps"
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
            <http-method v-if="data.httpMethod" :method="data.httpMethod" />
            <span :class="{ 'deprecated': data.type === types.TYPE_DOC && data.deprecated !== '$false$' }">{{ node.label }}</span>
            <span v-if="data.type !== types.TYPE_DOC" class="tip">({{ data.apiCount }})</span>
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
    onProjectChange: {
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
      treeData: [],
      currentProjectId: '',
      docIdMap: {},
      projectData: [],
      expandKeys: [],
      expandAll: false,
      defaultProps: {
        children: 'children',
        label: 'label'
      },
      types: this.getEnums().FOLDER_TYPE
    }
  },
  computed: {
    treeRows() {
      const search = this.filterText.trim().toLowerCase()
      return this.searchDoc(search, this.treeData, this.searchContent, this.isFolder)
    },
    projectId() {
      return this.$store.state.settings.projectId
    }
  },
  watch: {
    projectId(val) {
      this.loadMenu(val)
    }
  },
  mounted() {
    if (this.loadInit) {
      this.init()
    }
  },
  methods: {
    init() {
      if (this.projectData.length === 0) {
        // 获取项目列表
        this.get('/doc/view/projects', { }, resp => {
          this.projectData = resp.data
          this.loadMenu(this.projectId)
        })
      }
    },
    reloadMenu() {
      this.loadMenu(this.projectId)
    },
    loadMenu(projectId) {
      if (projectId) {
        this.currentProjectId = projectId
        this.setProjectId(projectId)
        this.expandKeys = []
        this.get('/doc/view/dataByProject', { projectId: projectId }, resp => {
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
          if (row.type === this.getEnums().FOLDER_TYPE.TYPE_FOLDER) {
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
      if (this.expandAll) {
        if (row.type === this.getEnums().FOLDER_TYPE.TYPE_FOLDER) {
          this.expandKeys.push(row.id)
        }
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
    isFolder(row) {
      return !row.url
    },
    searchContent(searchText, row) {
      return (row.url && row.url.toLowerCase().indexOf(searchText) > -1) ||
        (row.label && row.label.toLowerCase().indexOf(searchText) > -1) ||
        (row.docId && row.docId.toLowerCase().indexOf(searchText) > -1)
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
    onProjectSelect(value) {
      const spaceId = value[0]
      const projectId = value[1]
      this.loadMenu(projectId)
      // 设置当前空间
      for (const data of this.projectData) {
        if (data.id === spaceId) {
          this.setCurrentSpace({
            id: data.id,
            name: data.name
          })
          break
        }
      }
    },
    onTriggerStatus(val) {
      this.expandAll = val
      this.reloadMenu()
    },
    setCurrentNode(currentNode) {
      if (currentNode) {
        const tree = this.$refs.tree
        tree.setCurrentKey(currentNode.id)
        this.expandKeys = [currentNode.parentId]
      }
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
<style lang="scss" scoped>
.menu-tree {
  padding: 10px;
  font-size: 14px !important;
}
.select-area {
  .project-select {
    padding: 10px 10px 0 10px;
    width: 100%
  }
  .el-radio-group {
    vertical-align: bottom;
  }
}
</style>
