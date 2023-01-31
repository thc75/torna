<template>
  <div>
    <div class="table-opt-btn">
      <el-button type="primary" size="mini" @click="onAdd">{{ $t('newShare') }}</el-button>
    </div>
    <el-table
      :data="pageInfo.rows"
      border
      highlight-current-row
    >
      <el-table-column :label="$t('shareUrl')" show-overflow-tooltip>
        <template slot-scope="scope">
          <el-link type="primary" :href="buildUrl(scope.row)" target="_blank">{{ buildUrl(scope.row) }}</el-link>
          <span v-if="scope.row.type === getEnums().SHARE_TYPE.ENCRYPT">
            &nbsp;&nbsp;{{ $t('pwdShow') }}：{{ scope.row.password }}
          </span>
          <span v-if="scope.row.remark.length > 0" class="info-tip">
            {{ $t('remarkShow') }}：{{ scope.row.remark }}
          </span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('shareDoc')" width="100">
        <template slot-scope="scope">
          <span v-if="scope.row.isAll">{{ $t('allDocs') }}</span>
          <el-button v-else type="text" @click="viewDoc(scope.row)">{{ $t('look') }}</el-button>
        </template>
      </el-table-column>
      <el-table-column :label="$t('shareStyle')" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.type === getEnums().SHARE_TYPE.PUBLIC">{{ $t('public') }}</el-tag>
          <el-tag v-if="scope.row.type === getEnums().SHARE_TYPE.ENCRYPT" type="warning">{{ $t('encryption') }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('creator')" prop="creatorName" width="120" />
      <el-table-column :label="$t('status')" width="80">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === getEnums().STATUS.ENABLE" type="success">{{ $t('enable') }}</el-tag>
          <el-tag v-if="scope.row.status === getEnums().STATUS.DISABLE" type="danger">{{ $t('disable') }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('EnvSetting.allowDebug')" width="80">
        <template slot-scope="scope">
          <span v-if="scope.row.isShowDebug === getEnums().STATUS.ENABLE">{{ $t('yes') }}</span>
          <span v-else>{{ $t('no') }}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="gmtCreate"
        :label="$t('createTime')"
        width="110"
      >
        <template slot-scope="scope">
          <span :title="scope.row.gmtCreate">{{ scope.row.gmtCreate.split(' ')[0] }}</span>
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('operation')"
        width="200"
      >
        <template slot-scope="scope">
          <el-link type="primary" :underline="false" @click="onTableUpdate(scope.row)">{{ $t('update') }}</el-link>
          <el-link v-if="scope.row.status === getEnums().STATUS.ENABLE" type="warning" :underline="false" @click="onTableDisable(scope.row)">
            {{ $t('disable') }}
          </el-link>
          <el-link v-else type="success" :underline="false" @click="onTableEnable(scope.row)">{{ $t('enable') }}</el-link>
          <el-popconfirm
            :title="$t('deleteRowConfirm')"
            @confirm="onTableDelete(scope.row)"
          >
            <el-link slot="reference" type="danger">{{ $t('delete') }}</el-link>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      background
      style="margin-top: 5px"
      :current-page="searchFormData.pageIndex"
      :page-size="searchFormData.pageSize"
      :page-sizes="[5, 10, 20, 40]"
      :total="pageInfo.total"
      layout="total, sizes, prev, pager, next"
      @size-change="onSizeChange"
      @current-change="onPageIndexChange"
    />
    <!--dialog-->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      :close-on-click-modal="false"
      @close="() => {
        resetForm('dialogForm')
        $refs.docTreeRef.setCheckedKeys([])
        clearDialogForm()
      }"
    >
      <el-form
        ref="dialogForm"
        :rules="dialogFormRules"
        :model="dialogFormData"
        label-width="120px"
        size="mini"
      >
        <el-form-item :label="$t('remark')">
          <el-input v-model="dialogFormData.remark" :placeholder="$t('optional')" show-word-limit maxlength="50" />
        </el-form-item>
        <el-form-item :label="$t('shareStyle')">
          <el-radio-group v-model="dialogFormData.type">
            <el-radio :label="1">{{ $t('public') }}</el-radio>
            <el-radio :label="2">{{ $t('encryption') }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="$t('selectDoc')">
          <el-radio-group v-model="dialogFormData.isAll">
            <el-radio :label="0">{{ $t('partDocs') }}</el-radio>
            <el-radio :label="1">{{ $t('allDocs') }}<span class="normal-text">{{ $t('wholeModule') }}</span></el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="$t('EnvSetting.allowDebug')">
          <el-switch
            v-model="dialogFormData.isShowDebug"
            active-color="#13ce66"
            :active-value="1"
            :inactive-value="0"
          />
        </el-form-item>
        <el-form-item v-show="dialogFormData.isShowDebug" :label="$t('debugEnv')">
          <el-checkbox v-model="checkAll" :indeterminate="isIndeterminate">{{ $t('selectAll') }}</el-checkbox>
          <el-checkbox-group v-model="dialogFormData.moduleEnvironmentIdList">
            <el-checkbox v-for="(env, index) in moduleEnvironmentList" :key="index" :label="env.id">{{ env.name }}</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item v-show="dialogFormData.isAll === 0">
          <doc-tree ref="docTreeRef" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">{{ $t('dlgCancel') }}</el-button>
        <el-button type="primary" @click="onDialogSave">{{ $t('dlgSave') }}</el-button>
      </div>
    </el-dialog>
    <el-dialog
      :title="$t('shareDoc')"
      :visible.sync="dialogViewVisible"
      @close="() => { $refs.docTreeViewRef.clear() }"
    >
      <doc-tree ref="docTreeViewRef" view-mode />
    </el-dialog>
  </div>
</template>
<script>
import DocTree from '@/components/DocTree'

export default {
  components: { DocTree },
  props: {

  },
  data() {
    return {
      moduleId: '',
      data: [],
      searchFormData: {
        pageIndex: 1,
        pageSize: 10,
        moduleId: ''
      },
      pageInfo: {
        rows: [],
        total: 0
      },
      dialogVisible: false,
      dialogViewVisible: false,
      dialogTitle: '',
      dialogFormData: {
        id: '',
        type: 1,
        moduleId: '',
        isAll: 0,
        remark: '',
        isShowDebug: 1,
        moduleEnvironmentIdList: []
      },
      autoAppend: 1,
      moduleEnvironmentList: [],
      dialogFormRules: {
      }
    }
  },
  computed: {
    isIndeterminate() {
      const checkedCount = this.dialogFormData.moduleEnvironmentIdList.length
      return checkedCount > 0 && checkedCount < this.moduleEnvironmentList.length
    },
    checkAll: {
      get() {
        return this.dialogFormData.moduleEnvironmentIdList.length === this.moduleEnvironmentList.length
      },
      set(val) {
        this.$set(this.dialogFormData, 'moduleEnvironmentIdList', val ? this.moduleEnvironmentList.map(item => item.id) : [])
      }
    }
  },
  methods: {
    reload(moduleId) {
      if (moduleId) {
        this.moduleId = moduleId
      }
      this.loadTable(this.moduleId)
      this.loadDebugEnvs(this.moduleId)
    },
    loadTable(moduleId) {
      if (moduleId) {
        this.searchFormData.moduleId = moduleId
        this.post('/doc/share/page', this.searchFormData, resp => {
          this.pageInfo = resp.data
        })
      }
    },
    onAdd() {
      this.dialogTitle = this.$t('newShare')
      this.dialogVisible = true
      this.dialogFormData = {
        id: '',
        type: 1,
        isShowDebug: 0,
        moduleId: '',
        isAll: 0,
        remark: '',
        moduleEnvironmentList: [],
        moduleEnvironmentIdList: []
      }
      this.$nextTick(() => {
        this.reloadDocTree()
      })
    },
    loadDebugEnvs(moduleId, shareConfigId) {
      this.get('/module/environment/list', { moduleId: moduleId }, resp => {
        this.moduleEnvironmentList = resp.data
      })
      if (!shareConfigId) {
        return
      }
      this.get('/doc/share/listEnvironment', { id: shareConfigId }, resp => {
        this.$set(this.dialogFormData, 'moduleEnvironmentIdList', resp.data.map(item => item.moduleEnvironmentId))
      })
    },

    clearDialogForm() {
      if (!this.dialogFormData) {
        return
      }
      this.dialogFormData = this.$data.dialogFormData
    },
    onTableUpdate(row) {
      this.dialogTitle = this.$t('updateShare')
      this.dialogVisible = true
      this.$nextTick(() => {
        Object.assign(this.dialogFormData, row)

        if (row.isShowDebug) {
          this.loadDebugEnvs(row.moduleId, row.id)
        }

        if (!row.isAll) {
          this.get('/doc/share/listShareDocIds', { id: row.id }, resp => {
            const contentList = resp.data
            // 所有的文档id
            const idList = contentList.map(row => row.docId)
            this.reloadDocTree(treeData => {
              for (const data of treeData) {
                for (const content of contentList) {
                  if (data.id === content.docId) {
                    data.isShareFolder = content.isShareFolder === 1
                    break
                  }
                }
              }
            }, tree => {
              tree.setCheckedKeys(idList)
            })
          })
        } else {
          this.reloadDocTree()
        }
      })
    },
    onTableEnable(row) {
      this.post('/doc/share/enable', row, resp => {
        this.tipSuccess(this.$t('operateSuccess'))
        this.reload()
      })
    },
    onTableDisable(row) {
      this.post('/doc/share/disable', row, resp => {
        this.tipSuccess(this.$t('operateSuccess'))
        this.reload()
      })
    },
    onTableDelete(row) {
      const data = {
        id: row.id
      }
      this.post('/doc/share/del', data, () => {
        this.tipSuccess(this.$t('operateSuccess'))
        this.reload()
      })
    },
    onDialogSave() {
      const data = this.dialogFormData
      data.moduleId = this.moduleId
      const checkedNodes = this.$refs.docTreeRef.getCheckedNodes()
      if (!data.isAll && checkedNodes.length === 0) {
        this.tipError(this.$t('pleaseCheckDoc'))
        return
      }
      const content = []
      const treeNode = this.convertTree(checkedNodes)
      const append = (node, isShareFolder) => {
        if (isShareFolder === undefined) {
          isShareFolder = 0
        }
        // 如果是分享整个文件夹，只需要保存文件夹id
        if (isShareFolder) {
          content.push({
            docId: node.id,
            parentId: node.parentId,
            isShareFolder: isShareFolder
          })
        } else {
          content.push({
            docId: node.id,
            parentId: node.parentId,
            isShareFolder: isShareFolder
          })
          // 保存子文件
          // 如果没有设置追加，需要添加所有文档id
          const children = node.children || []
          if (children.length > 0) {
            for (const child of children) {
              append(child, isShareFolder)
            }
          }
        }
      }
      for (const node of treeNode) {
        append(node, node.isShareFolder)
      }
      data.content = content
      data.isAllSelectedDebug = this.checkAll
      const uri = this.dialogFormData.id ? '/doc/share/update' : '/doc/share/add'
      if (this.checkAll) {
        this.dialogFormData.moduleEnvironmentIdList = []
      }
      this.post(uri, this.dialogFormData, () => {
        this.dialogVisible = false
        this.reload()
      })
    },
    onSizeChange(size) {
      this.searchFormData.pageIndex = 1
      this.searchFormData.pageSize = size
      this.reload()
    },
    onPageIndexChange(pageIndex) {
      this.searchFormData.pageIndex = pageIndex
      this.reload()
    },
    reloadDocTree(beforeFun, afterFun) {
      this.$refs.docTreeRef.load(this.moduleId, beforeFun, afterFun)
    },
    reloadDocTreeView(beforeFun, afterFun) {
      this.$refs.docTreeViewRef.load(this.moduleId, beforeFun, afterFun)
    },
    buildUrl(row) {
      return `${this.getBaseUrl()}/#/share/${row.id}`
    },
    viewDoc(row) {
      this.dialogViewVisible = true
      this.$nextTick(() => {
        this.get('/doc/share/listShareDocIds', { id: row.id }, resp => {
          const contentList = resp.data
          const idList = contentList.map(row => row.docId)
          this.reloadDocTreeView(treeData => {
            for (const data of treeData) {
              for (const content of contentList) {
                if (data.id === content.docId) {
                  data.isShareFolder = content.isShareFolder === 1
                  break
                }
              }
            }
          }, tree => {
            tree.setCheckedKeys(idList)
            this.$refs.docTreeViewRef.disable()
          })
        })
      })
    }
  }
}
</script>
