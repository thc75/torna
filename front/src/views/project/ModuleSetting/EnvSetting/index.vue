<template>
  <div>
    <div style="margin-bottom: 10px">
      <el-button type="primary" size="mini" @click="onEnvAdd">{{ $t('addEnv') }}</el-button>
    </div>

    <div v-show="envs.length > 0">
      <el-tabs v-model="activeNameEnv" type="card" @tab-click="onEnvTabClick">
        <el-tab-pane v-for="env in envs" :key="env.id" :name="env.id" :label="env.name" />
      </el-tabs>
      <!-- descriptions -->
      <el-form
        ref="updateEnvForm"
        :rules="dialogDebugEnvFormRules"
        :model="updateEnvFormData"
        class="update-env-form"
      >
        <el-descriptions :column="3" border>
          <template slot="extra">
            <el-button-group style="float: right">
              <el-tooltip placement="top" :content="$t('EnvSetting.copyCurrent')" :open-delay="1000">
                <el-button type="primary" size="mini" icon="el-icon-document-copy" @click="onEnvCopy" />
              </el-tooltip>
              <el-tooltip placement="top" :content="$t('EnvSetting.deleteCurrent')" :open-delay="1000">
                <el-button type="danger" size="mini" icon="el-icon-delete" @click="onEnvDelete" />
              </el-tooltip>
            </el-button-group>
          </template>
          <el-descriptions-item :label="$t('envName')" content-class-name="update-env-name">
            <el-form-item prop="name">
              <el-input v-model="updateEnvFormData.name" />
            </el-form-item>
          </el-descriptions-item>
          <el-descriptions-item :label="$t('baseUrl')">
            <el-form-item prop="url">
              <el-input v-model="updateEnvFormData.url" />
            </el-form-item>
          </el-descriptions-item>
          <el-descriptions-item :label="$t('isPublic')">
            <el-switch
              v-model="updateEnvFormData.isPublic"
              :active-text="$t('yes')"
              :inactive-text="$t('no')"
              :active-value="1"
              :inactive-value="0"
            />
          </el-descriptions-item>
        </el-descriptions>
      </el-form>
      <el-button type="primary" size="mini" style="margin: 10px 0;" @click="updateEnv">{{ $t('save') }}</el-button>
      <h4>{{ $t('EnvSetting.commonSetting') }}</h4>
      <el-tabs v-model="activeName" tab-position="left" @tab-click="onTabClick">
        <el-tab-pane :label="$t('commonHeader')" name="globalHeaders">
          <global-headers ref="globalHeaders" />
        </el-tab-pane>
        <el-tab-pane :label="$t('commonRequest')" name="globalParams">
          <global-params ref="globalParams" />
        </el-tab-pane>
        <el-tab-pane :label="$t('commonResponse')" name="globalReturns">
          <global-returns ref="globalReturns" />
        </el-tab-pane>
      </el-tabs>
    </div>
    <!--dialog-->
    <el-dialog
      :title="dialogDebugEnvTitle"
      :visible.sync="dialogDebugEnvVisible"
      :close-on-click-modal="false"
      @close="resetForm('dialogDebugEnvForm')"
    >
      <el-tabs v-model="activeNameAdd" type="card">
        <el-tab-pane name="add" :label="$t('EnvSetting.newEnv')">
          <el-form
            ref="dialogDebugEnvForm"
            :rules="dialogDebugEnvFormRules"
            :model="dialogDebugEnvFormData"
            label-position="top"
            size="mini"
          >
            <el-form-item
              prop="name"
              :label="$t('envName')"
            >
              <el-input v-model="dialogDebugEnvFormData.name" :placeholder="$t('envNamePlaceholder')" show-word-limit maxlength="50" />
            </el-form-item>
            <el-form-item
              prop="url"
              :label="$t('baseUrl')"
            >
              <el-input v-model="dialogDebugEnvFormData.url" :placeholder="$t('baseUrlPlaceholder')" show-word-limit maxlength="100" />
            </el-form-item>
            <el-form-item
              prop="extendId"
              :label="$t('isPublic')"
            >
              <el-radio-group v-model="dialogDebugEnvFormData.isPublic">
                <el-radio :label="1">{{ $t('yes') }}</el-radio>
                <el-radio :label="0">{{ $t('no') }}</el-radio>
                <span class="info-tip">{{ $t('debugEnvPublicTip') }}</span>
              </el-radio-group>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        <el-tab-pane name="EnvSetting.importEnv" :label="$t('EnvSetting.importEnv')">
          <el-tree
            ref="tree"
            :data="treeRows"
            :highlight-current="true"
            :expand-on-click-node="true"
            default-expand-all
            :empty-text="$t('noData')"
            node-key="id"
            class="filter-tree"
            :show-checkbox="true"
          >
            <span slot-scope="{ node, data }">
              <span>
                {{ data.label }}
              </span>
              <span v-if="data.isEnv" class="doc-select-url">
                {{ data.url }}
              </span>
            </span>
          </el-tree>
        </el-tab-pane>
      </el-tabs>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogDebugEnvVisible = false">{{ $t('dlgCancel') }}</el-button>
        <el-button type="primary" @click="onDialogDebugEnvSave">{{ $t('dlgSave') }}</el-button>
      </div>
    </el-dialog>
    <!--dialog-->
    <el-dialog
      :title="dialogCopyEnvTitle"
      :visible.sync="dialogCopyEnvVisible"
      :close-on-click-modal="false"
      @close="resetForm('dialogCopyEnvForm')"
    >
      <el-form
        ref="dialogCopyEnvForm"
        :rules="dialogDebugEnvFormRules"
        :model="dialogCopyEnvFormData"
        label-position="top"
        size="mini"
      >
        <el-form-item
          prop="name"
          :label="$t('envName')"
        >
          <el-input v-model="dialogCopyEnvFormData.name" :placeholder="$t('envNamePlaceholder')" show-word-limit maxlength="50" />
        </el-form-item>
        <el-form-item
          prop="url"
          :label="$t('baseUrl')"
        >
          <el-input v-model="dialogCopyEnvFormData.url" :placeholder="$t('baseUrlPlaceholder')" show-word-limit maxlength="100" />
        </el-form-item>
        <el-form-item
          prop="extendId"
          :label="$t('isPublic')"
        >
          <el-radio-group v-model="dialogCopyEnvFormData.isPublic">
            <el-radio :label="1">{{ $t('yes') }}</el-radio>
            <el-radio :label="0">{{ $t('no') }}</el-radio>
            <span class="info-tip">{{ $t('debugEnvPublicTip') }}</span>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogCopyEnvVisible = false">{{ $t('dlgCancel') }}</el-button>
        <el-button type="primary" @click="onDialogCopyEnvSave">{{ $t('dlgSave') }}</el-button>
      </div>
    </el-dialog>

  </div>
</template>
<script>
import GlobalHeaders from '@/components/ModuleSetting/GlobalHeaders'
import GlobalParams from '@/components/ModuleSetting/GlobalParams'
import GlobalReturns from '@/components/ModuleSetting/GlobalReturns'

export default {
  name: 'ModuleSetting',
  components: { GlobalHeaders, GlobalParams, GlobalReturns },
  props: {
    projectId: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      activeNameEnv: '',
      activeName: 'globalHeaders',
      activeNameAdd: 'add',
      envs: [],
      curEnv: {
        id: '',
        name: '',
        url: '',
        isPublic: 0
      },
      moduleId: '',
      dialogDebugEnvVisible: false,
      dialogDebugEnvTitle: '',
      dialogDebugEnvFormData: {
        id: '',
        moduleId: '',
        name: '',
        url: '',
        isPublic: 0
      },
      updateEnvFormData: {
        id: '',
        moduleId: '',
        name: '',
        url: '',
        isPublic: 0
      },
      dialogDebugEnvFormRules: {
        name: [
          { required: true, message: this.$t('notEmpty'), trigger: 'blur' }
        ],
        url: [
          { required: true, message: this.$t('notEmpty'), trigger: 'blur' }
        ]
      },
      dialogCopyEnvVisible: false,
      dialogCopyEnvTitle: '',
      dialogCopyEnvFormData: {
        id: '',
        fromEnvId: '',
        destModuleId: '',
        name: '',
        url: '',
        isPublic: 0
      },
      isNew: false,
      treeRows: []
    }
  },
  methods: {
    reload(moduleId) {
      if (moduleId) {
        this.moduleId = moduleId
        this.activeNameEnv = ''
      }
      this.loadEnvs(this.moduleId)
      this.activeName = 'globalHeaders'
    },
    onTabClick(tab) {
      this.$refs[tab.name].reload(this.curEnv.id)
    },
    onEnvTabClick(tab) {
      this.selectEnvTab(tab.name)
    },
    selectEnvTab(id) {
      this.activeNameEnv = id
      const result = this.envs.filter(row => row.id === id)
      if (result && result.length > 0) {
        this.curEnv = result[0]
        Object.assign(this.updateEnvFormData, this.curEnv)
        this.loadParams(this.curEnv.id)
      }
    },
    loadParams(envId) {
      const $ref = this.$refs[this.activeName]
      $ref && $ref.reload(envId)
    },
    loadEnvs(moduleId) {
      this.get('/module/environment/list', { moduleId: moduleId }, resp => {
        this.envs = resp.data
        if (this.envs.length > 0) {
          let envId
          // 刚刚保存后
          if (this.isNew) {
            envId = this.envs[this.envs.length - 1].id
            this.isNew = false
          } else {
            if (!this.activeNameEnv || this.activeNameEnv === '0') {
              envId = this.envs[0].id
            } else {
              envId = this.activeNameEnv
            }
          }
          this.selectEnvTab(envId)
        }
      })
    },
    onEnvAdd() {
      this.loadEnvTree()
      this.dialogDebugEnvTitle = this.$t('addEnv')
      this.dialogDebugEnvVisible = true
      this.dialogDebugEnvFormData = {
        id: '',
        moduleId: '',
        name: '',
        url: '',
        isPublic: 0
      }
    },
    onEnvDelete() {
      this.confirm(this.$t('deleteConfirm', this.curEnv.name), () => {
        this.post('/module/environment/delete', this.curEnv, () => {
          this.tip(this.$t('deleteSuccess'))
          this.activeNameEnv = ''
          this.reload()
        })
      })
    },
    onDialogDebugEnvSave() {
      if (this.activeNameAdd === 'add') {
        this.saveNewEnv()
      } else {
        this.importOtherEnvs()
      }
    },
    saveNewEnv() {
      this.$refs.dialogDebugEnvForm.validate((valid) => {
        if (valid) {
          this.isNew = this.dialogDebugEnvFormData.id.length === 0
          const uri = this.dialogDebugEnvFormData.id ? '/module/environment/update' : '/module/environment/add'
          this.dialogDebugEnvFormData.moduleId = this.moduleId
          this.post(uri, this.dialogDebugEnvFormData, () => {
            this.dialogDebugEnvVisible = false
            this.reload()
          })
        }
      })
    },
    updateEnv() {
      this.$refs.updateEnvForm.validate((valid) => {
        if (valid) {
          const uri = '/module/environment/update'
          this.post(uri, this.updateEnvFormData, () => {
            this.tipSuccess($t('updateSuccess'))
            this.reload()
          })
        }
      })
    },
    onEnvCopy() {
      this.dialogCopyEnvTitle = this.$t('EnvSetting.copyEnv', this.curEnv.name)
      this.dialogCopyEnvVisible = true
      this.dialogCopyEnvFormData = {
        id: '',
        fromEnvId: this.curEnv.id,
        destModuleId: this.moduleId,
        name: this.curEnv.name + ' copy',
        url: this.curEnv.url,
        description: this.curEnv.description,
        isPublic: this.curEnv.isPublic
      }
    },
    onDialogCopyEnvSave() {
      this.$refs.dialogCopyEnvForm.validate((valid) => {
        if (valid) {
          this.isNew = true
          const uri = '/module/environment/copy'
          this.dialogCopyEnvFormData.moduleId = this.moduleId
          this.post(uri, this.dialogCopyEnvFormData, () => {
            this.dialogCopyEnvVisible = false
            this.reload()
          })
        }
      })
    },
    loadEnvTree() {
      this.get('/module/environment/userenvs', {}, resp => {
        const rows = resp.data
        for (const row of rows) {
          if (!(row.isModule || row.isEnv)) {
            row.disabled = true
          }
        }
        this.treeRows = this.convertTree(rows)
      })
    },
    importOtherEnvs() {
      const nodes = this.$refs.tree.getCheckedNodes(true)
      if (nodes.length === 0) {
        this.tipError($t('EnvSetting.plzCheckEnv'))
        return
      }
      const envIds = nodes.map(row => row.envId)
      const data = {
        destModuleId: this.moduleId,
        envIds: envIds
      }
      this.post('/module/environment/import', data, resp => {
        this.tipSuccess($t('importSuccess'))
        this.dialogDebugEnvVisible = false
        this.reload()
      })
    }
  }
}
</script>
<style lang="scss">
th.is-bordered-label {width: 100px;}
.update-env-name { width: 250px; }
.update-env-form {
  .el-form-item { margin-bottom: 0; }
}
</style>
