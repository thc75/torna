<template>
  <div>
    <div class="table-opt-btn">
      <el-button type="primary" size="mini" @click="onAdd">{{ $t('newShare') }}</el-button>
    </div>
    <el-table
      :data="pageInfo.list"
      border
      highlight-current-row
    >
      <el-table-column :label="$t('shareName')" prop="shareName" width="200"/>
      <el-table-column :label="$t('shareUrl')"  show-overflow-tooltip  >
        <template slot-scope="scope">
          <div class="share-url-wrapper">
            <el-link type="primary" :href="buildUrl(scope.row)" target="_blank">{{ buildUrl(scope.row) }}</el-link>
            <el-button
              type="text"
              class="copy-btn"
              @click.stop="copy(buildUrl(scope.row))"
            >
              <i class="el-icon-document-copy"></i>
            </el-button>
          </div>
        </template>
      </el-table-column>
      <el-table-column :label="$t('pwdShow')" prop="password" width="120">
        <template slot-scope="scope">
          <div class="password-wrapper">
            <span>{{ scope.row.password }}</span>
            <el-button
              type="text"
              class="copy-btn"
              @click.stop="copy(scope.row.password)"
            >
              <i class="el-icon-document-copy"></i>
            </el-button>
          </div>
        </template>
      </el-table-column>      <el-table-column :label="$t('shareDoc')" width="100">
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
      <el-table-column :label="$t('expirationTime')" width="100">
        <template slot-scope="scope">
          <span>{{ scope.row.expirationTime ? scope.row.expirationTime : $t('permanentValidity') }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('creator')" prop="creatorName" width="120"/>
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
      <el-table-column :label="$t('remark')" prop="remark" width="150"/>
      <el-table-column
        :label="$t('operation')"
        width="200"
      >
        <template slot-scope="scope">
          <el-link type="primary" :underline="false" @click="onTableUpdate(scope.row)">{{ $t('update') }}</el-link>
          <el-link v-if="scope.row.status === getEnums().STATUS.ENABLE" type="warning" :underline="false"
                   @click="onTableDisable(scope.row)">
            {{ $t('disable') }}
          </el-link>
          <el-link v-else type="success" :underline="false" @click="onTableEnable(scope.row)">{{
              $t('enable')
            }}
          </el-link>
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
        <el-form-item :label="$t('shareName')">
          <el-input v-model="dialogFormData.shareName" :placeholder="$t('optional')" show-word-limit maxlength="50"/>
        </el-form-item>
        <el-form-item :label="$t('shareStyle')">
          <el-radio-group v-model="dialogFormData.type">
            <el-radio :label="1">{{ $t('public') }}</el-radio>
            <el-radio :label="2">{{ $t('encryption') }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="dialogFormData.type === 2" :label="$t('pwdShow')" prop="password">
          <el-input v-model="dialogFormData.password" :type="passwordType" maxlength="32">
            <el-button slot="append" @click="generateRandomPassword">{{ $t('randomPassword') }}</el-button>
            <i slot="suffix" :class="['el-input__icon', passwordType === 'password' ? 'el-icon-lock' : 'el-icon-unlock']" style="cursor: pointer" @click="togglePasswordVisibility"></i>
          </el-input>
        </el-form-item>
        <el-form-item :label="$t('expirationTime')">
          <template slot="label">
            <span>{{ $t('expirationTime') }}</span>
            <el-tooltip placement="top" :content="$t('permanentValidityTip')">
              <i class="el-icon-question"/>
            </el-tooltip>
          </template>
          <el-date-picker
            v-model="dialogFormData.expirationTime"
            :picker-options="pickerOptions"
            :placeholder="$t('expirationTimePicker')"
            value-format="yyyy-MM-dd"
            format="yyyy-MM-dd"/>
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
            <el-checkbox v-for="(env, index) in moduleEnvironmentList" :key="index" :label="env.id">{{
                env.name
              }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item v-show="dialogFormData.isAll === 0">
          <div class="checked-nodes">
            <span>选中的菜单：</span>
            <el-tag v-for="tag in selectParentNodes" :key="tag.id" closable @close="handleClose(tag)"> {{ tag.name }} </el-tag>
          </div>
          <doc-tree
            ref="docTreeRef"
            message="sharePage"
            @checkedNodes="handleTreeCheckedNodes"
            @setNodesChecked="setNodesChecked" />
        </el-form-item>
        <el-form-item :label="$t('remark')">
          <el-input v-model="dialogFormData.remark" :placeholder="$t('optional')" show-word-limit maxlength="50"/>
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
      <doc-tree ref="docTreeViewRef" view-mode/>
    </el-dialog>
  </div>
</template>
<script>
import DocTree from '@/components/DocTree'

export default {
  components: {DocTree},
  props: {},
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
        list: [],
        total: 0
      },
      dialogVisible: false,
      dialogViewVisible: false,
      dialogTitle: '',
      dialogFormData: {
        id: '',
        type: 1,
        expirationTime: '',
        moduleId: '',
        isAll: 0,
        remark: '',
        isShowDebug: 1,
        moduleEnvironmentIdList: [] ,
        password: '',
        shareName: '',
      },
      autoAppend: 1,
      moduleEnvironmentList: [],
      passwordType: 'password',
      dialogFormRules: {
        password: [
          { required: true, message: this.$t('passwordNotEmpty'), trigger: 'blur', validator: (rule, value, callback) => {
              console.log(value)
              if (this.dialogFormData.type === 2 && !value) {
                callback(new Error(this.$t('passwordNotEmpty')))
              } else {
                callback()
              }
            }}
        ]
      },
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() < Date.now() - 8.64e7; // Disable dates before today
        }
      },
      selectNodes: [],
      selectParentNodes: []
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
    handleClose(tag) {
      const nodes = this.selectParentNodes.filter(item => item.id !== tag.id)
      this.selectParentNodes = [...nodes]
      this.$refs.docTreeRef.setCheckedKeys([...nodes.map(item => item.id)])
    },
    handleTreeCheckedNodes(rows, unIds = []) {
      const oldRows = this.selectNodes
      const ids = oldRows.map(item => item.id)
      const selectRows = rows.filter(item => {
        if (!ids.includes(item.id)) {
          return item
        }
      })
      this.selectNodes = [...oldRows, ...selectRows].filter(item => !unIds.includes(item.id))
      this.selectParentNodes = this.selectNodes.filter(item => !item.children.length)
    },
    setNodesChecked() {
      const rows = this.selectParentNodes.map(item => item.id)
      this.$refs.docTreeRef.setCheckedKeys([...rows])
    },
    togglePasswordVisibility() {
      this.passwordType = this.passwordType === 'password' ? 'text' : 'password';
    },
    generateRandomPassword() {
      const chars = 'ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz0123456789';
      let password = '';
      for (let i = 0; i < 8; i++) {
        password += chars.charAt(Math.floor(Math.random() * chars.length));
      }
      this.dialogFormData.password = password;
      this.$nextTick(() => {
        this.$refs.dialogForm.validateField('password');
      });
      this.$message({
        message: this.$t('pwdShow') + ': ' + password,
        type: 'success',
        duration: 3000
      });
    },
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
      this.selectNodes = []
      this.selectParentNodes = []
      this.dialogTitle = this.$t('newShare')
      this.dialogVisible = true
      this.dialogFormData = {
        id: '',
        type: 1,
        expirationTime: '',
        isShowDebug: 0,
        moduleId: '',
        isAll: 0,
        remark: '',
        password: '',
        moduleEnvironmentList: [],
        moduleEnvironmentIdList: []
      }
      this.$nextTick(() => {
        this.reloadDocTree()
      })
    },
    loadDebugEnvs(moduleId, shareConfigId) {
      this.get('/module/environment/list', {moduleId: moduleId}, resp => {
        this.moduleEnvironmentList = resp.data
      })
      if (!shareConfigId) {
        return
      }
      this.get('/doc/share/listEnvironment', {id: shareConfigId}, resp => {
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
      this.selectNodes = []
      this.selectParentNodes = []
      this.dialogTitle = this.$t('updateShare')
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.docTreeRef.initFilterText()
        Object.assign(this.dialogFormData, row)

        if (row.isShowDebug) {
          this.loadDebugEnvs(row.moduleId, row.id)
        }

        if (!row.isAll) {
          this.get('/doc/share/listShareDocIds', {id: row.id}, resp => {
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
              this.$nextTick(() => {
                this.handleTreeCheckedNodes(this.$refs.docTreeRef.getCheckedNodes(), [])
              })
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
       const checkedNodes = [...this.selectNodes]
      // const checkedNodes = this.$refs.docTreeRef.getCheckedNodes()
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
              append(child, child.isShareFolder)
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
      if (this.dialogFormData.type === 2 && !this.dialogFormData.password) {
        this.tipError(this.$t('passwordNotEmpty'))
        return
      }
      if (this.checkAll) {
        this.dialogFormData.moduleEnvironmentIdList = []
      }
      this.post(uri, this.dialogFormData, () => {
        this.dialogVisible = false
        this.selectNodes = []
        this.selectParentNodes = []
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
        this.get('/doc/share/listShareDocIds', {id: row.id}, resp => {
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
    },
    copy(text) {
      this.copyText(text)
    }
  }
}
</script>

<style scoped>
.share-url-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
  position: relative;
}

.copy-btn {
  padding: 2px 5px;
  opacity: 0;
  transition: opacity 0.3s;
  position: absolute;
  right: 0;
}

.share-url-wrapper:hover .copy-btn {
  opacity: 1;
}

.password-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
  position: relative;
}

.copy-btn {
  padding: 2px 5px;
  opacity: 0;
  transition: opacity 0.3s;
  position: absolute;
  right: 0;
}

.password-wrapper:hover .copy-btn {
  opacity: 1;
}

.checked-nodes {
  margin-bottom: 15px;
}
</style>
