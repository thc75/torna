<template>
  <div>
    <div>
      <el-dropdown v-if="hasRole(`project:${projectId}`, [Role.dev, Role.admin])" trigger="click" @command="handleCommand">
        <el-button type="primary" size="mini">
          {{ $ts('createDoc') }} <i class="el-icon-arrow-down el-icon--right"></i>
        </el-button>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item icon="el-icon-document" :command="onDocNew">{{ $ts('createDoc') }}</el-dropdown-item>
          <el-dropdown-item icon="el-icon-folder" :command="onFolderAdd">{{ $ts('createFolder') }}</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
      <div class="table-right">
        <div class="table-right-item">
          <el-input
            v-model="tableSearch"
            prefix-icon="el-icon-search"
            clearable
            size="mini"
            :placeholder="$ts('apiFilter')"
            style="width: 300px;"
          />
        </div>
        <div class="table-right-item">
          <el-tooltip placement="top" :content="$ts('refreshTable')">
            <el-button type="primary" size="mini" icon="el-icon-refresh" @click="refreshTable" />
          </el-tooltip>
        </div>
        <div class="table-right-item">
          <el-button v-show="tableData.length > 0" type="primary" size="mini" @click="onExport">{{ $ts('export') }}</el-button>
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
        :label="$ts('docName')"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          {{ scope.row.name }}
          <div v-if="isDoc(scope.row)" style="display: inline-block;">
            <div v-if="!scope.row.isShow">
              <el-tooltip placement="bottom" :content="$ts('hidden')">
                <svg-icon icon-class="eye" svg-style="color: #e6a23c" />
              </el-tooltip>
            </div>
          </div>
        </template>
      </u-table-column>
      <u-table-column
        prop="url"
        label="URL"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <http-method v-if="scope.row.httpMethod && scope.row.url" :method="scope.row.httpMethod" />
          <span style="margin-left: 5px;">{{ scope.row.url }}</span>
        </template>
      </u-table-column>
      <u-table-column
        prop="author"
        :label="$ts('maintainer')"
        width="120"
        show-overflow-tooltip
      />
      <u-table-column
        prop="modifierName"
        :label="$ts('modifierName')"
        width="120"
        show-overflow-tooltip
      />
      <u-table-column
        prop="gmtModified"
        :label="$ts('updateTime')"
        width="110"
      >
        <template slot-scope="scope">
          <time-tooltip :time="scope.row.gmtModified" />
        </template>
      </u-table-column>
      <u-table-column
        v-if="hasRole(`project:${projectId}`, [Role.dev, Role.admin])"
        :label="$ts('operation')"
        :width="$width(160, { 'en': 190 })"
      >
        <template slot-scope="scope">
          <div>
            <el-link v-if="isFolder(scope.row)" type="primary" @click="onDocAdd(scope.row)">{{ $ts('createDoc') }}</el-link>
            <el-link v-if="!isFolder(scope.row)" type="success" :underline="false" @click="openLink(`/view/${scope.row.id}`)">{{ $ts('preview') }}</el-link>
            <el-link type="primary" @click="onDocUpdate(scope.row)">{{ $ts('update') }}</el-link>
            <el-dropdown v-if="scope.row.children.length === 0" @command="handleCommand">
              <span class="el-dropdown-link">
                {{ $ts('more') }} <i class="el-icon-arrow-down el-icon--right"></i>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item v-if="!isFolder(scope.row)" icon="el-icon-document-copy" :command="() => { onDocCopy(scope.row) }">
                  {{ $ts('copy') }}
                </el-dropdown-item>
                <el-dropdown-item
                  icon="el-icon-delete"
                  class="danger"
                  :command="() => { onDocRemove(scope.row) }"
                >
                  {{ $ts('delete') }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </template>
      </u-table-column>
      <u-table-column
        v-else
        :label="$ts('operation')"
        width="80"
      >
        <template slot-scope="scope">
          <div v-if="!isFolder(scope.row)">
            <el-link v-if="scope.row.isShow" type="success" :underline="false" @click="openLink(`/view/${scope.row.id}`)">{{ $ts('preview') }}</el-link>
          </div>
        </template>
      </u-table-column>
    </u-table>
    <doc-export-dialog ref="exportDialog" />
  </div>
</template>
<style lang="scss">
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
      return this.searchRow(search, this.tableData, this.searchContent, this.isFolder)
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
        this.tipSuccess(this.$ts('refreshSuccess'))
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
      this.$prompt(this.$ts('inputFolderMsg'), this.$ts('updateFolderTitle'), {
        confirmButtonText: this.$ts('ok'),
        cancelButtonText: this.$ts('cancel'),
        inputValue: row.name,
        inputPattern: /^.{1,64}$/,
        inputErrorMessage: this.$ts('notEmptyLengthLimit', 64)
      }).then(({ value }) => {
        const data = {
          id: row.id,
          name: value
        }
        this.post('/doc/folder/update', data, () => {
          this.tipSuccess(this.$ts('updateSuccess'))
          this.reload()
        })
      }).catch(() => {
      })
    },
    onDocNew() {
      this.goRoute(`/doc/new/${this.moduleId}`)
    },
    onFolderAdd() {
      this.$prompt(this.$ts('inputFolderMsg'), this.$ts('newFolderTitle'), {
        confirmButtonText: this.$ts('ok'),
        cancelButtonText: this.$ts('cancel'),
        inputPattern: /^.{1,64}$/,
        inputErrorMessage: this.$ts('notEmptyLengthLimit', 64)
      }).then(({ value }) => {
        const data = {
          name: value,
          moduleId: this.moduleId
        }
        this.post('/doc/folder/add', data, () => {
          this.tipSuccess(this.$ts('createSuccess'))
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
      this.confirm(this.$ts('deleteConfirm', row.name), () => {
        const data = {
          id: row.id
        }
        this.post('/doc/delete', data, () => {
          this.tipSuccess(this.$ts('deleteSuccess'))
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
