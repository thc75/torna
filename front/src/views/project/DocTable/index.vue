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
          <el-button v-show="tableData.length > 0" type="primary" size="mini" @click="onExport">导出</el-button>
        </div>
      </div>
    </div>
    <u-table
      ref="plTreeTable"
      :data="tableRows"
      row-id="id"
      use-virtual
      :treeConfig="{ children: 'children', iconClose: 'el-icon-arrow-right', iconOpen: 'el-icon-arrow-down', expandAll: true}"
      :height="tableHeight"
      :row-height="30"
      border
    >
      <u-table-column
        :tree-node="true"
        prop="name"
        label="文档名称"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          {{ scope.row.name }}
          <div v-if="isDoc(scope.row)" style="display: inline-block;">
            <div v-if="!scope.row.isShow">
              <el-tooltip placement="bottom" content="隐藏">
                <svg-icon icon-class="eye" svg-style="color: #e6a23c" />
              </el-tooltip>
            </div>
          </div>
        </template>
      </u-table-column>
      <u-table-column
        prop="url"
        label="请求路径"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <http-method v-if="scope.row.httpMethod && scope.row.url" :method="scope.row.httpMethod" />
          <span style="margin-left: 5px;">{{ scope.row.url }}</span>
        </template>
      </u-table-column>
      <u-table-column
        prop="author"
        label="维护人"
        width="100"
      />
      <u-table-column
        prop="modifierName"
        label="最后修改人"
        width="100"
      />
      <u-table-column
        prop="gmtModified"
        label="修改时间"
        width="100"
      >
        <template slot-scope="scope">
          <time-tooltip :time="scope.row.gmtModified" />
        </template>
      </u-table-column>
      <u-table-column
        v-if="hasRole(`project:${projectId}`, [Role.dev, Role.admin])"
        label="操作"
        width="160"
      >
        <template slot-scope="scope">
          <div>
            <el-link v-if="isFolder(scope.row)" type="primary" :underline="false" @click="onDocAdd(scope.row)">添加文档</el-link>
            <router-link v-if="!isFolder(scope.row) && scope.row.isShow" :to="`/view/${scope.row.id}`" target="_blank">
              <el-link type="success" :underline="false">预览</el-link>
            </router-link>
            <el-link type="primary" :underline="false" @click="onDocUpdate(scope.row)">修改</el-link>
            <el-dropdown v-if="scope.row.children.length === 0" @command="handleCommand">
              <span class="el-dropdown-link">
                更多 <i class="el-icon-arrow-down el-icon--right"></i>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item v-if="!isFolder(scope.row)" icon="el-icon-document-copy" :command="() => { onDocCopy(scope.row) }">
                  复制
                </el-dropdown-item>
                <el-dropdown-item
                  icon="el-icon-delete"
                  class="danger"
                  :command="() => { onDocRemove(scope.row) }"
                >
                  删除
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </template>
      </u-table-column>
      <u-table-column
        v-else
        label="操作"
        width="80"
      >
        <template slot-scope="scope">
          <div v-if="!isFolder(scope.row)">
            <router-link v-if="scope.row.isShow" :to="`/view/${scope.row.id}`" target="_blank">
              <el-link type="success">预览</el-link>
            </router-link>
          </div>
        </template>
      </u-table-column>
    </u-table>
    <doc-export-dialog ref="exportDialog" />
  </div>
</template>
<style lang="scss">
.table-right {
  float: right;
  margin-bottom: 10px;
  .table-right-item {
    display: inline-block;
  }
}
.el-dropdown-link {
  cursor: pointer;
  color: #409EFF;
  font-size: 13px;
  font-weight: 500;
}
.el-icon-arrow-down {
  font-size: 12px;
}
</style>
<script>
import HttpMethod from '@/components/HttpMethod'
import SvgIcon from '@/components/SvgIcon'
import TimeTooltip from '@/components/TimeTooltip'
import DocExportDialog from '@/components/DocExportDialog'

export default {
  name: 'DocTable',
  components: { HttpMethod, SvgIcon, TimeTooltip, DocExportDialog },
  props: {
    projectId: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      moduleId: '',
      tableHeight: 0,
      tableData: [],
      tableSearch: '',
      loading: false
    }
  },
  computed: {
    tableRows() {
      let search = this.tableSearch.trim()
      if (!search) {
        return this.tableData
      }
      search = search.toLowerCase()
      const data = []
      for (const row of this.tableData) {
        // 如果是分类，需要找分类中的子文档
        if (this.isFolder(row)) {
          const children = row.children || []
          const newChildren = children.filter(child => {
            return this.searchContent(search, child)
          })
          // 如果找到了
          if (newChildren.length > 0) {
            const rowCopy = Object.assign({}, row)
            rowCopy.children = newChildren
            data.push(rowCopy)
          }
        } else {
          // 不是分类找到了直接加入
          if (this.searchContent(search, row)) {
            data.push(row)
          }
        }
      }
      return data
    }
  },
  created() {
    this.initHeight()
    window.addEventListener('resize', this.initHeight)
  },
  destroyed() {
    window.removeEventListener('resize', this.initHeight)
  },
  methods: {
    refreshTable() {
      this.loadTable(function() {
        this.tipSuccess('刷新成功')
      })
    },
    loadTable(callback) {
      this.reload(this.moduleId, callback)
    },
    reload(moduleId, callback) {
      if (moduleId) {
        this.moduleId = moduleId
      }
      this.loading = true
      this.get('/doc/list', { moduleId: this.moduleId }, function(resp) {
        this.tableData = this.convertTree(resp.data)
        callback && callback.call(this)
        this.loading = false
      })
    },
    initHeight() {
      this.tableHeight = window.innerHeight - 165
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
    searchContent(searchText, row) {
      return (row.url && row.url.toLowerCase().indexOf(searchText) > -1) ||
        (row.name && row.name.toLowerCase().indexOf(searchText) > -1) ||
        (row.id && row.id.toLowerCase().indexOf(searchText) > -1)
    },
    onDocRemove(row) {
      this.confirm(`确定要删除 ${row.name} 吗？`, () => {
        const data = {
          id: row.id
        }
        this.post('/doc/delete', data, () => {
          this.tipSuccess('删除成功')
          this.loadTable()
        })
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
    onDocCopy(row) {
      this.goRoute(`/doc/copy/${this.moduleId}/${row.id}`)
    },
    onExport() {
      this.$refs.exportDialog.show(this.tableData)
    }
  }
}
</script>
