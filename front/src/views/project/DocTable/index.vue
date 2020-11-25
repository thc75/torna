<template>
  <div>
    <div style="margin-bottom: 10px;">
      <el-dropdown trigger="click" @command="handleCommand">
        <el-button type="primary" size="mini">
          新建接口 <i class="el-icon-arrow-down el-icon--right"></i>
        </el-button>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item icon="el-icon-document" :command="onDocNew">新建接口</el-dropdown-item>
          <el-dropdown-item icon="el-icon-folder" :command="onFolderAdd">新建分类</el-dropdown-item>
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
          <el-button v-if="isDoc(scope.row)" type="text" icon="el-icon-view" @click="onDocView(scope.row)">预览</el-button>
        </template>
      </el-table-column>
      <el-table-column
        prop="gmtCreate"
        label="添加时间"
        width="160"
      />
      <el-table-column
        label="操作"
        width="160"
      >
        <template slot-scope="scope">
          <el-link v-if="isFolder(scope.row)" type="primary" @click="onDocAdd(scope.row)">添加接口</el-link>
          <el-link type="primary" @click="onDocUpdate(scope.row)">修改</el-link>
          <el-popconfirm
            :title="`确定要删除 ${scope.row.name} 吗？`"
            @onConfirm="onDocRemove(scope.row)"
          >
            <el-link v-if="scope.row.children.length === 0" slot="reference" type="danger">删除</el-link>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog
      title="预览"
      :visible.sync="viewDialogVisible"
      width="70%"
    >
      <doc-view v-show="viewDocId" :doc-id="viewDocId" />
    </el-dialog>
  </div>
</template>
<script>
import DocView from '../../doc/DocView'
export default {
  name: 'DocTable',
  components: { DocView },
  props: {
    moduleId: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      tableData: [],
      tableSearch: '',
      viewDialogVisible: false,
      viewDocId: ''
    }
  },
  watch: {
    moduleId(moduleId) {
      this.loadTable(moduleId)
    }
  },
  methods: {
    reload() {
      this.loadTable(this.moduleId)
    },
    loadTable: function(moduleId) {
      this.get('/doc/list', { moduleId: moduleId }, function(resp) {
        const data = resp.data
        this.tableData = this.convertTree(data)
      })
    },
    onFolderUpdate(row) {
      this.$prompt('请输入分类名称', '修改分类', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputValue: row.name,
        inputPattern: /^.{1,64}$/,
        inputErrorMessage: '不能为空且长度在64以内'
      }).then(({ value }) => {
        const data = {
          id: row.id,
          name: value
        }
        this.post('/doc/folder/update', data, () => {
          this.tipSuccess('修改成功')
          this.reload()
        })
      }).catch(() => {
      })
    },
    onDocNew() {
      this.goRoute(`/doc/new/${this.moduleId}`)
    },
    onFolderAdd() {
      this.$prompt('请输入分类名称', '新建分类', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /^.{1,64}$/,
        inputErrorMessage: '不能为空且长度在64以内'
      }).then(({ value }) => {
        const data = {
          name: value,
          moduleId: this.moduleId
        }
        this.post('/doc/folder/add', data, () => {
          this.tipSuccess('创建成功')
          this.reload()
        })
      }).catch(() => {
      })
    },
    isDoc(row) {
      return !this.isFolder(row)
    },
    isFolder(row) {
      return row.isFolder === 1
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
    onDocRemove(row) {
      const data = {
        id: row.id
      }
      this.post('/doc/delete', data, () => {
        this.tipSuccess('删除成功')
        this.reload()
      })
    },
    onDocAdd(row) {
      this.goRoute(`/doc/new/${this.moduleId}/${row.id}`)
    },
    onDocUpdate: function(row) {
      if (row.isFolder) {
        this.onFolderUpdate(row)
      } else {
        this.goRoute(`/doc/edit/${this.moduleId}/${row.id}`)
      }
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
