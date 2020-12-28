<template>
  <div>
    <div>
      <el-dropdown v-if="hasRole(`project:${projectId}`, [Role.dev, Role.admin])" trigger="click" @command="handleCommand">
        <el-button type="primary" size="mini">
          新建接口 <i class="el-icon-arrow-down el-icon--right"></i>
        </el-button>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item icon="el-icon-document" :command="onDocNew">新建接口</el-dropdown-item>
          <el-dropdown-item icon="el-icon-folder" :command="onFolderAdd">新建分类</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
      <div class="table-right">
        <div class="table-right-item">
          <el-input
            v-model="tableSearch"
            prefix-icon="el-icon-search"
            clearable
            size="mini"
            placeholder="过滤: 支持ID、名称、路径"
            style="width: 300px;"
          />
        </div>
        <div class="table-right-item">
          <el-tooltip placement="top" content="刷新表格">
            <el-button type="primary" size="mini" icon="el-icon-refresh" @click="refreshTable" />
          </el-tooltip>
        </div>
        <div class="table-right-item">
          <el-dropdown v-show="tableData.length > 0" trigger="click" @command="handleCommand">
            <el-button type="primary" size="mini">
              导出 <i class="el-icon-arrow-down el-icon--right"></i>
            </el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item :command="onExportMarkdownSinglePage">导出markdown(单页)</el-dropdown-item>
              <el-dropdown-item :command="onExportMarkdownMultiPages">导出markdown(多页)</el-dropdown-item>
              <el-dropdown-item divided :command="onExportHtmlSinglePage">导出html(单页)</el-dropdown-item>
              <el-dropdown-item :command="onExportHtmlMultiPages">导出html(多页)</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
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
      >
        <template slot-scope="scope">
          {{ scope.row.name }}
          <el-tag
            v-if="!scope.row.isShow"
            type="warning"
            disable-transitions>
            隐藏
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="url"
        label="请求路径"
      >
        <template slot-scope="scope">
          <el-tag
            v-if="scope.row.url"
            :type="getTagType(scope.row)"
            disable-transitions>
            {{ scope.row.httpMethod }}
          </el-tag>
          <span style="margin-left: 5px;">{{ scope.row.url }}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="文档内容"
        width="80"
      >
        <template slot-scope="scope">
          <router-link v-if="scope.row.isShow" :to="`/view/doc/${scope.row.id}`" target="_blank">
            <el-button v-if="isDoc(scope.row)" type="text" icon="el-icon-view">预览</el-button>
          </router-link>
        </template>
      </el-table-column>
      <el-table-column
        prop="gmtCreate"
        label="添加时间"
        width="160"
      />
      <el-table-column
        v-if="hasRole(`project:${projectId}`, [Role.dev, Role.admin])"
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
  </div>
</template>
<style lang="scss">
.table-right {
  float: right;
  margin-bottom: 10px;
  .table-right-item {
    display: inline-block;
    //margin-left: 4px;
  }
}

.cell .el-tag {
  height: inherit !important;
  padding: 0 4px !important;
  line-height: inherit !important;
}
</style>
<script>
import ExportUtil from '@/utils/export'

const tagMap = {
  'GET': 'info',
  'POST': '',
  'PUT': 'warning',
  'DELETE': 'danger',
  'HEAD': 'success'
}

export default {
  name: 'DocTable',
  props: {
    moduleId: {
      type: String,
      default: ''
    },
    projectId: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      tableData: [],
      tableSearch: ''
    }
  },
  watch: {
    moduleId(moduleId) {
      this.loadTable(moduleId)
    }
  },
  methods: {
    refreshTable() {
      this.reload(function() {
        this.tipSuccess('刷新成功')
      })
    },
    reload(callback) {
      this.loadTable(this.moduleId, callback)
    },
    loadTable: function(moduleId, callback) {
      this.get('/doc/list', { moduleId: moduleId }, function(resp) {
        this.tableData = this.convertTree(resp.data)
        callback && callback.call(this)
      })
    },
    getTagType(row) {
      return tagMap[row.httpMethod] || ''
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
      const find = (row.id && row.id.toLowerCase().indexOf(searchText) > -1) ||
        (row.name && row.name.toLowerCase().indexOf(searchText) > -1) ||
        (row.url && row.url.toLowerCase().indexOf(searchText) > -1)
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
    onExportMarkdownSinglePage() {
      ExportUtil.exportMarkdownAllInOne(this.moduleId)
    },
    onExportMarkdownMultiPages() {
      ExportUtil.exportMarkdownMultiPages(this.moduleId)
    },
    onExportHtmlSinglePage() {
      ExportUtil.exportHtmlAllInOne(this.moduleId)
    },
    onExportHtmlMultiPages() {
      ExportUtil.exportHtmlMultiPages(this.moduleId)
    }
  }
}
</script>
