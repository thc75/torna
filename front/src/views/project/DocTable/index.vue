<template>
  <div>
    <div>
      <el-dropdown v-if="hasRole(`project:${projectId}`, [Role.dev, Role.admin])" trigger="click" @command="handleCommand">
        <el-button type="primary" size="mini">
          {{ $t('createDoc') }} <i class="el-icon-arrow-down el-icon--right"></i>
        </el-button>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item icon="el-icon-document" :command="onDocNew">{{ $t('createDoc') }}</el-dropdown-item>
          <el-dropdown-item icon="el-icon-folder" :command="onFolderAdd">{{ $t('createFolder') }}</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
      <div class="table-right">
        <el-radio-group v-model="searchForm.status" size="mini" @change="() => loadTable()">
          <el-radio-button label="null">全部</el-radio-button>
          <el-radio-button label="0">未完成</el-radio-button>
          <el-radio-button label="5">进行中</el-radio-button>
          <el-radio-button label="10">已完成</el-radio-button>
        </el-radio-group>
        <el-radio-group v-model="triggerStatus" size="mini" @change="onTriggerStatus">
          <el-radio-button label="1">{{ $t('expand') }}</el-radio-button>
          <el-radio-button label="0">{{ $t('collapse') }}</el-radio-button>
        </el-radio-group>
        <div class="table-right-item">
          <el-input
            v-model="tableSearch"
            prefix-icon="el-icon-search"
            clearable
            size="mini"
            :placeholder="$t('apiFilter')"
            style="width: 300px;"
          />
        </div>
        <div class="table-right-item">
          <el-tooltip placement="top" :content="$t('refreshTable')">
            <el-button type="primary" size="mini" icon="el-icon-refresh" @click="refreshTable" />
          </el-tooltip>
        </div>
        <div class="table-right-item">
          <el-dropdown split-button type="primary" trigger="click" size="mini" @click="onExport">
            {{ $t('export') }}
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item @click.native="onExportPostman">{{ $t('export') + ' postman(Collection v2.1)' }}</el-dropdown-item>
              <el-dropdown-item @click.native="onCopyPostmanUrl">
                {{ `${$t('copy')} postman 导入URL` }}
                <el-popover
                  placement="bottom-end"
                  trigger="hover"
                  width="800"
                >
                  <img :src="`${getBaseUrl()}/static/images/postmanimport.jpg`" style="width: 100%" />
                  <i slot="reference" class="el-icon-question" style="margin-left: 10px" @click.stop="() => {}" />
                </el-popover>
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
    </div>
    <u-table
      ref="plTreeTable"
      :data="tableRows"
      row-id="id"
      use-virtual
      :treeConfig="{
        children: 'children',
        iconClose: 'el-icon-arrow-right',
        iconOpen: 'el-icon-arrow-down',
        expandAll: triggerStatus === '1'
      }"
      :height="tableHeight"
      :row-height="30"
      border
    >
      <u-table-column
        :tree-node="true"
        prop="name"
        :label="$t('docName')"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          {{ scope.row.name }}
          <div v-if="isDoc(scope.row)" class="el-table-cell-icon">
            <div v-if="!scope.row.isShow">
              <el-tooltip placement="top" :content="$t('hidden')">
                <svg-icon icon-class="eye" svg-style="color: #e6a23c" />
              </el-tooltip>
            </div>
          </div>
          <div v-if="scope.row.isLocked" class="el-table-cell-icon">
            <el-tooltip placement="top" :content="$t('lockDocDesc')">
              <i class="el-icon-lock"></i>
            </el-tooltip>
          </div>
          <div v-if="scope.row.isFolder === 0 && scope.row.type === getEnums().DOC_TYPE.MARKDOWN" class="el-table-cell-icon">
            <el-tooltip placement="top" :content="$t('mardown')">
              <el-tag>md</el-tag>
            </el-tooltip>
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
        prop="version"
        :label="$t('version')"
        width="80"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <div v-if="scope.row.isFolder === 0">
            <span style="margin-right: 5px;">{{ scope.row.version }}</span>
            <popover-update
              v-if="hasRole(`project:${projectId}`, [Role.dev, Role.admin])"
              :title="$t('version')"
              :maxlength="20"
              :show-icon="true"
              :is-validate="false"
              :value="`${scope.row.version}`"
              :on-show="() => {return scope.row.version}"
              :on-save="(val, call) => onSaveVersion(scope.row.id, val, call)"
            />
          </div>
        </template>
      </u-table-column>
      <u-table-column
        prop="author"
        :label="$t('maintainer')"
        width="120"
        show-overflow-tooltip
      />
      <u-table-column
        prop="modifierName"
        :label="$t('modifierName')"
        width="120"
        show-overflow-tooltip
      />
      <u-table-column
        prop="gmtModified"
        :label="$t('updateTime')"
        width="110"
      >
        <template slot-scope="scope">
          <time-tooltip :time="scope.row.gmtModified" />
        </template>
      </u-table-column>
      <u-table-column
        v-if="hasRole(`project:${projectId}`, [Role.dev, Role.admin])"
        prop="orderIndex"
        :label="$t('orderIndex')"
        width="80"
      >
        <template slot-scope="scope">
          <popover-update
            :title="$t('orderIndex')"
            is-number
            :show-icon="false"
            :value="`${scope.row.orderIndex}`"
            :on-show="() => {return scope.row.orderIndex}"
            :on-save="(val, call) => onSaveOrderIndex(scope.row.id, val, call)"
          />
        </template>
      </u-table-column>
      <u-table-column
        prop="status"
        :label="$t('status')"
        width="80"
      >
        <template slot-scope="scope">
          <div v-if="!isFolder(scope.row)">
            <el-dropdown v-if="hasRole(`project:${projectId}`, [Role.dev, Role.admin])" trigger="click" @command="handleCommand">
              <span class="el-dropdown-link">
                <doc-status-tag :status="scope.row.status" />
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item v-for="item in getEnums().DOC_STATUS" :key="item.value" :command="() => onUpdateStatus(scope.row, item.value)">
                  <doc-status-tag :status="item.value" />
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
            <span v-else>
              <doc-status-tag :status="scope.row.status" />
            </span>
          </div>
        </template>
      </u-table-column>
      <u-table-column
        v-if="hasRole(`project:${projectId}`, [Role.dev, Role.admin])"
        :label="$t('operation')"
        :width="$width(160, { 'en': 190 })"
      >
        <template slot-scope="scope">
          <div class="icon-operation">
            <el-link v-if="isFolder(scope.row)" type="primary" icon="el-icon-document-add" :title="$t('createDoc')" @click="onDocAdd(scope.row)" />
            <el-link v-if="isFolder(scope.row)" type="primary" icon="el-icon-folder-add" :title="$t('createFolder')" @click="onDocFolderAdd(scope.row)" />
            <el-link v-if="!isFolder(scope.row)" type="success" icon="el-icon-view" :title="$t('preview')" :underline="false" @click="openLink(getViewUrl(scope.row))" />
            <el-link type="primary" icon="el-icon-edit" :title="$t('update')" @click="onDocUpdate(scope.row)" />
            <el-link v-if="!isFolder(scope.row)" type="info" icon="el-icon-document-copy" :title="$t('copy')" @click="onDocCopy(scope.row)" />
            <el-dropdown v-if="scope.row.children.length === 0" @command="handleCommand">
              <span class="el-dropdown-link">
                <i class="el-icon-more el-icon--right"></i>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item
                  icon="el-icon-delete"
                  class="danger"
                  :title="$t('delete')"
                  :command="() => { onDocRemove(scope.row) }"
                >
                </el-dropdown-item>
                <el-dropdown-item
                  v-if="!isFolder(scope.row) && !scope.row.isLocked"
                  :command="() => { onDocLock(scope.row) }"
                >
                  <el-tooltip placement="top" :content="$t('lockDocDesc')">
                    <span class="el-icon-lock"></span>
                  </el-tooltip>
                </el-dropdown-item>
                <el-dropdown-item
                  v-if="scope.row.isLocked"
                  icon="el-icon-unlock"
                  :title="$t('unlock')"
                  :command="() => { onDocUnLock(scope.row) }"
                >
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </template>
      </u-table-column>
      <u-table-column
        v-else
        :label="$t('operation')"
        width="80"
      >
        <template slot-scope="scope">
          <div v-if="!isFolder(scope.row)">
            <el-link v-if="scope.row.isShow" type="success" icon="el-icon-view" :title="$t('preview')" :underline="false" @click="openLink(getViewUrl(scope.row))" />
          </div>
        </template>
      </u-table-column>
    </u-table>
    <doc-export-dialog ref="exportDialog" />
    <!-- dialog -->
    <el-dialog
      :title="$t('updateFolderTitle')"
      :close-on-click-modal="false"
      :visible.sync="updateFolderDlgShow"
    >
      <el-form
        ref="updateFolderForm"
        :model="rowData"
        :rules="updateFolderFormRules"
        label-width="120px"
        style="width: 500px;"
      >
        <el-form-item :label="$t('parentNode')" prop="parentId">
          <el-select v-model="rowData.parentId">
            <el-option v-for="item in folders" :key="item.id" :value="item.id" :label="item.name" :disabled="item.disabled">
              {{ item.name }}
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('categoryName')" prop="name">
          <el-input v-model="rowData.name" show-word-limit maxlength="100" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="updateFolderDlgShow = false">{{ $t('dlgCancel') }}</el-button>
        <el-button type="primary" @click="onFolderSave">{{ $t('dlgSave') }}</el-button>
      </div>
    </el-dialog>
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
import DocStatusTag from '@/components/DocStatusTag'
import HttpMethod from '@/components/HttpMethod'
import SvgIcon from '@/components/SvgIcon'
import TimeTooltip from '@/components/TimeTooltip'
import DocExportDialog from '@/components/DocExportDialog'
import PopoverUpdate from '@/components/PopoverUpdate'

export default {
  name: 'DocTable',
  components: { DocStatusTag, HttpMethod, SvgIcon, TimeTooltip, DocExportDialog, PopoverUpdate },
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
      loading: false,
      triggerStatus: '1',
      docViewTabs: false,
      rowData: {
        id: '',
        name: '',
        parentId: ''
      },
      folders: [],
      emptyNode: { id: '', name: $t('empty') },
      updateFolderDlgShow: false,
      updateFolderFormRules: {
        name: [
          { required: true, message: this.$t('notEmpty'), trigger: 'blur' }
        ]
      },
      searchForm: {
        status: null
      },
      token: ''
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
    },
    postmanUrl() {
      return `${this.getBaseUrl()}/module/postman/${this.token}`
    }
  },
  created() {
    this.initHeight()
    window.addEventListener('resize', this.initHeight)
  },
  mounted() {
    if (this.projectId) {
      this.setProjectId(this.projectId)
    }
    this.docViewTabs = this.$store.state.settings.docViewTabSwitch
  },
  destroyed() {
    window.removeEventListener('resize', this.initHeight)
  },
  methods: {
    onUpdateStatus(row, status) {
      if (row.status === status) {
        return
      }
      this.post('/doc/status/update', {
        id: row.id,
        status: status
      }, resp => {
        row.status = status
        this.tipSuccess($t('operateSuccess'))
      })
    },
    refreshTable() {
      this.loadTable(function() {
        this.tipSuccess(this.$t('refreshSuccess'))
      })
    },
    loadTable(callback) {
      this.reload(this.moduleId, callback)
    },
    reload(moduleId, callback) {
      if (moduleId) {
        this.moduleId = moduleId
      }
      this.triggerStatus = this.getAttr(this.getTriggerStatusKey()) || '1'
      this.loading = true
      const searchData = {
        moduleId: this.moduleId
      }
      Object.assign(searchData, this.searchForm)
      this.post('/doc/list-v2', searchData, function(resp) {
        this.tableData = this.convertTree(resp.data)
        callback && callback.call(this)
        this.loading = false
      })
      this.loadToken(moduleId)
    },
    loadToken(moduleId) {
      if (moduleId) {
        this.get('/module/token/get', { moduleId: moduleId }, function(resp) {
          this.token = resp.data
        })
      }
    },
    initHeight() {
      this.tableHeight = window.innerHeight - 185
    },
    onSaveOrderIndex(id, orderIndex, callback) {
      callback()
      this.updateOrderIndex(id, orderIndex)
    },
    onSaveVersion(id, version, callback) {
      callback()
      this.updateVersion(id, version)
    },
    updateVersion(id, version) {
      this.post('/doc/version/update', { id: id, version: version }, resp => {
        this.loadTable()
      })
    },
    updateOrderIndex(id, orderIndex) {
      this.post('/doc/orderindex/update', { id: id, orderIndex: orderIndex }, resp => {
        this.loadTable()
      })
    },
    onFolderUpdate(row) {
      Object.assign(this.rowData, row)
      this.loadFolderData((folders) => {
        const folderIds = this.getFolderIds(row)
        for (const folder of folders) {
          folder.disabled = false
          for (const folderId of folderIds) {
            if (folderId === folder.id) {
              folder.disabled = true
              break
            }
          }
        }
        this.folders = folders
        this.updateFolderDlgShow = true
      })
    },
    onFolderSave() {
      this.$refs.updateFolderForm.validate(valid => {
        if (valid) {
          const data = {
            id: this.rowData.id,
            name: this.rowData.name,
            parentId: this.rowData.parentId
          }
          this.post('/doc/folder/update', data, () => {
            this.updateFolderDlgShow = false
            this.tipSuccess(this.$t('updateSuccess'))
            this.reload()
          })
        }
      })
    },
    loadFolderData(callback) {
      this.get('/doc/folder/list', { moduleId: this.moduleId }, resp => {
        const folders = [this.emptyNode].concat(resp.data)
        callback && callback.call(this, folders)
      })
    },
    getFolderIds(row) {
      if (!this.isFolder(row)) {
        return []
      }
      let ids = []
      ids.push(row.id)
      // 找出下面的子目录
      if (row.children && row.children.length > 0) {
        const folders = row.children.filter(item => item.isFolder === 1)
        for (const child of folders) {
          const idArr = this.getFolderIds(child)
          ids = ids.concat(idArr)
        }
      }
      return ids
    },
    onDocNew() {
      this.goRoute(`/doc/new/${this.moduleId}`)
    },
    onFolderAdd() {
      this.$prompt(this.$t('inputFolderMsg'), this.$t('newFolderTitle'), {
        confirmButtonText: this.$t('ok'),
        cancelButtonText: this.$t('cancel'),
        inputPattern: /^.{1,64}$/,
        inputErrorMessage: this.$t('notEmptyLengthLimit', 64)
      }).then(({ value }) => {
        const data = {
          name: value,
          moduleId: this.moduleId
        }
        this.post('/doc/folder/add', data, () => {
          this.tipSuccess(this.$t('createSuccess'))
          this.reload()
        })
      }).catch(() => {
      })
    },
    onTriggerStatus(val) {
      this.setAttr(this.getTriggerStatusKey(), val)
      if (val === '1') {
        this.$refs.plTreeTable.setAllTreeExpansion()
      } else {
        this.$refs.plTreeTable.clearTreeExpand()
      }
    },
    getTriggerStatusKey() {
      return `torna.doc.table.trigger.${this.moduleId}`
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
      this.confirm(this.$t('deleteConfirm', row.name), () => {
        const data = {
          id: row.id
        }
        this.post('/doc/delete', data, () => {
          this.tipSuccess(this.$t('deleteSuccess'))
          this.loadTable()
        })
      })
    },
    onDocLock(row) {
      this.post('/doc/lock', { id: row.id }, () => {
        this.tipSuccess(this.$t('operateSuccess'))
        this.loadTable()
      })
    },
    onDocUnLock(row) {
      this.post('/doc/unlock', { id: row.id }, () => {
        this.tipSuccess(this.$t('operateSuccess'))
        this.loadTable()
      })
    },
    onDocAdd(row) {
      this.pmsNextOrderIndex(row.children).then(order => {
        this.goRoute(`/doc/new/${this.moduleId}/${row.id}?order=${order}`)
      })
    },
    onDocFolderAdd(row) {
      this.$prompt(this.$t('inputFolderMsg'), `${row.name} - ${this.$t('newFolderTitle')}`, {
        confirmButtonText: this.$t('ok'),
        cancelButtonText: this.$t('cancel'),
        inputPattern: /^.{1,64}$/,
        inputErrorMessage: this.$t('notEmptyLengthLimit', 64)
      }).then(({ value }) => {
        const data = {
          name: value,
          moduleId: this.moduleId,
          parentId: row.id
        }
        this.post('/doc/folder/add', data, () => {
          this.tipSuccess(this.$t('createSuccess'))
          this.reload()
        })
      }).catch(() => {
      })
    },
    onDocUpdate: function(row) {
      if (row.isFolder) {
        this.onFolderUpdate(row)
      } else {
        if (row.type !== this.getEnums().DOC_TYPE.HTTP) {
          this.goRoute(`/doc/edit_custom/${this.moduleId}/${row.id}`)
        } else {
          this.goRoute(`/doc/edit/${this.moduleId}/${row.id}`)
        }
      }
    },
    onDocCopy(row) {
      if (row.type === this.getEnums().DOC_TYPE.CUSTOM || row.type === this.getEnums().DOC_TYPE.MARKDOWN) {
        this.goRoute(`/doc/copy_custom/${this.moduleId}/${row.id}`)
      } else {
        this.goRoute(`/doc/copy/${this.moduleId}/${row.id}`)
      }
    },
    onExport() {
      this.$refs.exportDialog.show(this.tableData, this.moduleId)
    },
    getViewUrl(row) {
      return `/view/${row.id}`
    },
    onExportPostman() {
      this.download('/doc/export/postman', { moduleId: this.moduleId }, err => {
        this.tipError('Export fail')
        console.log(err)
      })
    },
    onCopyPostmanUrl() {
      this.copyText(this.postmanUrl)
    }
  }
}
</script>
