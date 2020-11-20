<template>
  <div>
    <div style="margin-bottom: 10px;">
      <el-dropdown trigger="click" @command="handleCommand">
        <el-button type="primary" size="mini">
          新建接口 <i class="el-icon-arrow-down el-icon--right"></i>
        </el-button>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item icon="el-icon-document">新建接口</el-dropdown-item>
          <el-dropdown-item icon="el-icon-folder">新建分类</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
      <el-input
        v-model="tableSearch"
        prefix-icon="el-icon-search"
        clearable
        size="mini"
        placeholder="过滤: 支持名称、路径"
        style="width: 300px;float: right"
      />
    </div>
    <el-table
      :data="tableData"
      row-key="id"
      border
      default-expand-all
      :tree-props="{children: 'children', hasChildren: 'hasChildren'}"
      :cell-style="cellStyleSmall()"
      :header-cell-style="headCellStyleSmall()"
      :row-class-name="tableRowClassName"
    >
      <el-table-column
        prop="name"
        label="文档名称"
      />
      <el-table-column
        prop="url"
        label="请求路径"
      />
      <el-table-column
        label="文档内容"
        width="80"
      >
        <template slot-scope="scope">
          <el-button v-if="scope.row.children.length === 0" type="text" icon="el-icon-view" @click="onDocView(scope.row)">预览</el-button>
        </template>
      </el-table-column>
      <el-table-column
        prop="gmtCreate"
        label="添加时间"
        width="160"
      />
      <el-table-column
        label="操作"
        width="100"
      >
        <template slot-scope="scope">
          <el-link type="primary" icon="el-icon-edit" @click="onDocWrite(scope.row)">编辑</el-link>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog
      title="预览"
      :visible.sync="viewDialogVisible"
      width="70%"
    >
      <doc-view v-show="viewDocId > 0" :doc-id="viewDocId" />
    </el-dialog>
  </div>
</template>
<script>
import DocView from '../DocView'
export default {
  name: 'DocTable',
  components: { DocView },
  props: {
    moduleId: {
      type: Number,
      default: 0
    }
  },
  data() {
    return {
      tableData: [],
      tableSearch: '',
      viewDialogVisible: false,
      viewDocId: 0
    }
  },
  watch: {
    // filterText(val) {
    //   this.$refs.tree.filter(val)
    // },
    moduleId(moduleId) {
      this.loadTable(moduleId)
    }
  },
  methods: {
    loadTable: function(moduleId) {
      this.get('/project/doc/list', { moduleId: moduleId }, function(resp) {
        const data = resp.data
        this.tableData = this.convertTree(data)
      })
    },
    tableRowClassName({ row, index }) {
      row.hidden = false
      if (this.tableSearch.length === 0) {
        return ''
      }
      const searchText = this.tableSearch.toLowerCase()
      const find = (row.name && row.name.toLowerCase().indexOf(searchText) > -1) || (
        row.url && row.url.toLowerCase().indexOf(searchText) > -1
      )
      // 没有找到，隐藏
      if (!find) {
        row.hidden = true
        return 'hidden-row'
      }
      return ''
    },
    onDocWrite: function(row) {
      // this.goRoute(`/platform/doc/edit/${row.routeId}`)
    },
    onDocView: function(row) {
      this.viewDialogVisible = true
      this.$nextTick(() => {
        this.viewDocId = row.id
      })
    }
  }
}
</script>
