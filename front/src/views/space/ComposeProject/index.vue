<template>
  <div class="app-container">
    <el-button
      v-if="projectList.length === 0 && hasRole(`space:${spaceId}`, [Role.dev, Role.admin])"
      type="primary"
      @click="onProjectAdd"
    >
      {{ $t('createProject') }}
    </el-button>
    <el-tabs
      v-show="projectList.length > 0"
      v-model="active"
      type="card"
      :before-leave="beforeLeave"
      @tab-click="onTabClick"
    >
      <el-tab-pane
        v-for="project in projectList"
        :key="project.id"
        :label="project.name"
        :name="project.name"
      >
        <span slot="label">
          {{ project.name }}
          <el-dropdown
            v-if="hasRole(`space:${spaceId}`, [Role.dev, Role.admin])"
            v-show="project.id === projectInfo.id"
            trigger="click"
            style="margin-left: 5px;"
            @command="handleCommand"
          >
            <span class="el-dropdown-link">
              <el-tooltip placement="top" content="更多操作" :open-delay="500">
                <a class="el-icon-setting el-icon--right"></a>
              </el-tooltip>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item icon="el-icon-edit" :command="onUpdate">{{ $t('update')}}</el-dropdown-item>
              <el-dropdown-item icon="el-icon-delete" class="danger" :command="onDelete">{{ $t('delete') }}</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </span>
      </el-tab-pane>
      <el-tab-pane v-if="hasRole(`space:${spaceId}`, [Role.dev, Role.admin])" name="_new_">
        <span slot="label" @click="onProjectAdd">
          <el-tooltip placement="top" :content="$t('createProject')">
            <i class="el-icon-circle-plus"></i>
          </el-tooltip>
        </span>
      </el-tab-pane>
    </el-tabs>
    <div v-show="projectList.length > 0" class="compose-content">
      <el-tabs active-name="doc" tab-position="left">
        <el-tab-pane :label="$t('apiList')" name="doc">
          <div class="compose-url">
            <span style="margin-right: 20px">
              状态：
              <el-tag v-if="projectInfo.status" type="success">{{ $t('enable') }}</el-tag>
              <el-tag v-else type="danger">{{ $t('disable') }}</el-tag>
            </span>
            <span v-show="visitUrl" style="margin-right: 20px">
              {{ $t('visitUrl') }}：<el-link :href="visitUrl" type="primary" :underline="false" target="_blank">{{ visitUrl }}</el-link>
            </span>
            <span v-show="visitPassword">{{ $t('pwdShow') }}：{{ visitPassword }}</span>
          </div>
          <el-dropdown v-if="hasRole(`space:${spaceId}`, [Role.dev, Role.admin])" trigger="click" @command="handleCommand">
            <el-button type="primary" size="mini">
              {{ $t('createDoc') }} <i class="el-icon-arrow-down el-icon--right"></i>
            </el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item icon="el-icon-document" :command="onDocAdd">{{ $t('createDoc') }}</el-dropdown-item>
              <el-dropdown-item icon="el-icon-folder" :command="onDocFolderAdd">{{ $t('createFolder') }}</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
          <div class="table-right">
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
            />
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
              prop="origin"
              :label="$t('origin')"
              width="250"
              show-overflow-tooltip
            />
            <u-table-column
              prop="creator"
              :label="$t('creator')"
              width="120"
              show-overflow-tooltip
            />
            <u-table-column
              prop="gmtCreate"
              :label="$t('createTime')"
              width="110"
            >
              <template slot-scope="scope">
                <time-tooltip :time="scope.row.gmtCreate" />
              </template>
            </u-table-column>
            <u-table-column
              v-if="hasRole(`space:${spaceId}`, [Role.dev, Role.admin])"
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
              v-if="hasRole(`space:${spaceId}`, [Role.dev, Role.admin])"
              :label="$t('operation')"
              width="150"
            >
              <template slot-scope="scope">
                <div class="icon-operation">
                  <div v-if="isFolder(scope.row)" style="display: inline-block">
                    <el-tooltip v-if="isFolder(scope.row)" placement="top" :content="$t('createDoc')" :open-delay="500">
                      <el-link type="primary" icon="el-icon-document-add" @click="onDocAdd(scope.row)" />
                    </el-tooltip>
                    <el-tooltip v-if="isFolder(scope.row)" placement="top" :content="$t('createFolder')" :open-delay="500">
                      <el-link type="primary" icon="el-icon-folder-add" @click="onDocFolderAdd(scope.row)" />
                    </el-tooltip>
                    <el-tooltip v-if="isFolder(scope.row)" placement="top" :content="$t('updateName')" :open-delay="500">
                      <el-link type="primary" icon="el-icon-edit" @click="onDocUpdate(scope.row)" />
                    </el-tooltip>
                  </div>
                  <el-popconfirm
                    :title="$t('deleteConfirm', scope.row.name)"
                    @confirm="onDocRemove(scope.row)"
                  >
                    <el-link slot="reference" type="danger" icon="el-icon-delete" :title="$t('delete')" />
                  </el-popconfirm>
                </div>
              </template>
            </u-table-column>
          </u-table>
        </el-tab-pane>
        <el-tab-pane :label="$t('setting')" name="setting">
          <compose-project-setting ref="composeProjectSettingRef" :project-id="projectInfo.id" />
        </el-tab-pane>
      </el-tabs>
    </div>
    <compose-project-create-dialog ref="projectCreateDlg" :success="onProjectAddSuccess" />
    <el-dialog
      :title="$t('selectDoc')"
      :visible.sync="selectDocShow"
      @close="() => getSelect().clearChecked()"
    >
      <doc-select
        ref="docSelect"
        show-checkbox
        :load-init="false"
        :show-url="true"
        :indent="16"
      />
      <div slot="footer" class="dialog-footer">
        <el-button @click="selectDocShow = false">{{ $t('dlgCancel') }}</el-button>
        <el-button type="primary" @click="onDocAddSave">{{ $t('dlgSave') }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import ComposeProjectCreateDialog from '@/components/ComposeProjectCreateDialog'
import ComposeProjectSetting from '@/components/ComposeProjectSetting'
import DocSelect from '@/components/DocSelect'
import HttpMethod from '@/components/HttpMethod'
import TimeTooltip from '@/components/TimeTooltip'
import PopoverUpdate from '@/components/PopoverUpdate'

export default {
  components: { ComposeProjectCreateDialog, ComposeProjectSetting, DocSelect, HttpMethod, TimeTooltip, PopoverUpdate },
  props: {
    spaceId: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      active: null,
      beforeLeave: function(activeName, oldActiveName) {
        return activeName !== '_new_'
      },
      projectList: [],
      projectInfo: {
        id: '',
        name: '',
        status: 1
      },
      parentDoc: null,
      selectDocShow: false,
      tableHeight: 0,
      tableSearch: '',
      tableData: [],
      triggerStatus: '1'
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
    },
    visitUrl() {
      if (!this.projectInfo) {
        return ''
      }
      return `${this.getBaseUrl()}/#/show/${this.projectInfo.id}`
    },
    visitPassword() {
      if (!this.projectInfo) {
        return ''
      }
      return this.projectInfo.password
    }
  },
  watch: {
    spaceId(val) {
      this.loadProject(val)
    }
  },
  mounted() {
    this.loadProject(this.spaceId)
  },
  created() {
    this.initHeight()
    window.addEventListener('resize', this.initHeight)
  },
  destroyed() {
    window.removeEventListener('resize', this.initHeight)
  },
  methods: {
    reload() {
      this.loadProject(this.spaceId)
    },
    reloadTable(callback) {
      this.loadTable(this.projectInfo.id, callback)
    },
    refreshTable() {
      this.reloadTable(() => {
        this.tipSuccess('刷新成功')
      })
    },
    loadTable(projectId, callback) {
      if (projectId) {
        this.triggerStatus = this.getAttr(this.getTriggerStatusKey()) || '1'
        this.get('/compose/doc/list', { projectId: projectId }, function(resp) {
          this.tableData = this.convertTree(resp.data)
          callback && callback()
        })
      }
    },
    loadProject(spaceId) {
      if (spaceId) {
        this.get('/space/project/compose/list', { spaceId: spaceId }, resp => {
          this.projectList = resp.data
          if (this.projectList.length > 0) {
            let projectList
            if (this.projectInfo.id) {
              projectList = this.projectList.filter(row => row.id === this.projectInfo.id)
            }
            if (!projectList || projectList.length === 0) {
              projectList = this.projectList
            }
            const project = projectList[0]
            this.active = project.name
            this.projectInfo = project
            this.loadTable(project.id)
          }
        })
      }
    },
    searchContent(searchText, row) {
      return (row.url && row.url.toLowerCase().indexOf(searchText) > -1) ||
        (row.name && row.name.toLowerCase().indexOf(searchText) > -1) ||
        (row.id && row.id.toLowerCase().indexOf(searchText) > -1)
    },
    onProjectAdd() {
      this.$refs.projectCreateDlg.show(this.spaceId)
    },
    onProjectAddSuccess() {
      this.loadProject(this.spaceId)
    },
    onTabClick(tab) {
      const label = tab.label
      for (const project of this.projectList) {
        if (label === project.name) {
          this.projectInfo = project
          this.reloadTable()
          break
        }
      }
    },
    onDocAdd(parentDoc) {
      this.parentDoc = parentDoc
      this.selectDocShow = true
      this.$nextTick(() => {
        this.getSelect().init()
      })
    },
    onDocFolderAdd(parent) {
      this.$prompt(this.$t('inputFolderMsg'), this.$t('newFolderTitle'), {
        confirmButtonText: this.$t('ok'),
        cancelButtonText: this.$t('cancel'),
        inputPattern: /^.{1,64}$/,
        inputErrorMessage: this.$t('notEmptyLengthLimit', 64)
      }).then(({ value }) => {
        const data = {
          name: value,
          projectId: this.projectInfo.id
        }
        if (parent) {
          data.parentId = parent.id
        }
        const children = parent ? parent.children : []
        this.pmsNextOrderIndex(children).then(order => {
          data.orderIndex = order
        })
        this.post('/compose/doc/folder/add', data, () => {
          this.tipSuccess(this.$t('createSuccess'))
          this.reload()
        })
      }).catch(() => {
      })
    },
    getSelect() {
      return this.$refs.docSelect
    },
    onDocAddSave() {
      const checkedNodes = this.getSelect().getCheckedNodes(true)
      const docList = checkedNodes
        .filter(row => row.docId && row.docId.length > 0)
        .map(row => { return { docId: row.docId, origin: row.origin } })
      if (docList.length === 0) {
        this.tipError($t('pleaseCheckDoc'))
        return
      }
      const data = {
        projectId: this.projectInfo.id,
        docList: docList
      }
      if (this.parentDoc) {
        data.parentId = this.parentDoc.id
      }
      const children = this.parentDoc ? this.parentDoc.children : []
      this.pmsNextOrderIndex(children).then(order => {
        docList.forEach(row => {
          row.orderIndex = order
          order = order + 10
        })
        this.post('/compose/doc/add', data, () => {
          this.selectDocShow = false
          this.tipSuccess($t('addSuccess'))
          this.reloadTable()
        })
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
      return `torna.composedoc.table.trigger.${this.projectInfo.id}`
    },
    onUpdate() {
      this.$refs.projectCreateDlg.updateShow(this.projectInfo)
    },
    onDelete() {
      this.confirm(this.$t('deleteConfirm', this.projectInfo.name), () => {
        const data = {
          id: this.projectInfo.id
        }
        this.post('/compose/project/delete', data, () => {
          this.tipSuccess(this.$t('deleteSuccess'))
          this.reload()
        })
      })
    },
    onSaveOrderIndex(id, orderIndex, callback) {
      callback()
      this.updateOrderIndex(id, orderIndex)
    },
    updateOrderIndex(id, orderIndex) {
      this.post('/compose/doc/orderindex/update', { id: id, orderIndex: orderIndex }, resp => {
        this.reloadTable()
      })
    },
    initHeight() {
      this.tableHeight = window.innerHeight - 240
    },
    isFolder(row) {
      return row.isFolder === 1
    },
    onDocUpdate(row) {
      if (this.isFolder(row)) {
        this.onFolderUpdate(row)
      }
    },
    onFolderUpdate(row) {
      this.$prompt(this.$t('inputFolderMsg'), this.$t('updateFolderTitle'), {
        confirmButtonText: this.$t('ok'),
        cancelButtonText: this.$t('cancel'),
        inputValue: row.name,
        inputPattern: /^.{1,64}$/,
        inputErrorMessage: this.$t('notEmptyLengthLimit', 64)
      }).then(({ value }) => {
        const data = {
          id: row.id,
          name: value
        }
        this.post('/compose/doc/folder/update', data, () => {
          this.tipSuccess(this.$t('updateSuccess'))
          this.reload()
        })
      }).catch(() => {
      })
    },
    onDocRemove(row) {
      this.post('/compose/doc/remove', { id: row.id }, () => {
        this.tipSuccess($t('deleteSuccess'))
        this.reloadTable()
      })
    }
  }
}
</script>
<style lang="scss" scoped>
.compose-content {
  .compose-url {
    margin-bottom: 20px;
    color: #606266;
    font-size: 14px;
  }
}
</style>
