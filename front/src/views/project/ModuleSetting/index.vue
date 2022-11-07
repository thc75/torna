<template>
  <div>
    <div style="float: right">
      <el-button type="danger" size="mini" @click="onModuleDelete">{{ $ts('deleteModule') }}</el-button>
    </div>
    <h3>
      {{ $ts('appName') }}：
      <span style="font-weight: normal">
        {{ moduleVO.name }}
        <popover-update
          :value="moduleVO.name"
          :on-show="() => {return moduleVO.name}"
          :on-save="onSaveName"
        />
      </span>
    </h3>
    <h3>
      {{ $ts('debugEnv') }}
    </h3>

    <div style="margin-bottom: 10px">
      <el-button type="primary" size="mini" @click="onEnvAdd">{{ $ts('addEnv') }}</el-button>
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
        size="mini"
        class="update-env-form"
      >
        <el-descriptions :column="3" border>
          <template slot="extra">
            <el-button-group style="float: right">
              <el-tooltip placement="top" :content="$ts('copyCurrent')" :open-delay="1000">
                <el-button type="primary" size="mini" icon="el-icon-document-copy" @click="onEnvCopy" />
              </el-tooltip>
              <el-tooltip placement="top" :content="$ts('deleteCurrent')" :open-delay="1000">
                <el-button type="danger" size="mini" icon="el-icon-delete" @click="onEnvDelete" />
              </el-tooltip>
            </el-button-group>
          </template>
          <el-descriptions-item :label="$ts('envName')" content-class-name="update-env-name">
            <el-form-item prop="name">
              <el-input v-model="updateEnvFormData.name" />
            </el-form-item>
          </el-descriptions-item>
          <el-descriptions-item :label="$ts('baseUrl')">
            <el-form-item prop="url">
              <el-input v-model="updateEnvFormData.url" />
            </el-form-item>
          </el-descriptions-item>
          <el-descriptions-item :label="$ts('isPublic')">
            <el-switch
              v-model="updateEnvFormData.isPublic"
              :active-text="$ts('yes')"
              :inactive-text="$ts('no')"
              :active-value="1"
              :inactive-value="0"
            />
          </el-descriptions-item>
        </el-descriptions>
      </el-form>
      <el-button type="primary" size="mini" style="margin: 10px 0;" @click="updateEnv">{{ $ts('save') }}</el-button>
      <h4>{{ $ts('commonSetting') }}</h4>
      <el-tabs v-model="activeName" tab-position="left" @tab-click="onTabClick">
        <el-tab-pane :label="$ts('commonHeader')" name="globalHeaders">
          <global-headers ref="globalHeaders" />
        </el-tab-pane>
        <el-tab-pane :label="$ts('commonRequest')" name="globalParams">
          <global-params ref="globalParams" />
        </el-tab-pane>
        <el-tab-pane :label="$ts('commonResponse')" name="globalReturns">
          <global-returns ref="globalReturns" />
        </el-tab-pane>
      </el-tabs>
      <div v-show="moduleVO.type === 1">
        <h3>{{ $ts('swaggerSetting') }}</h3>
        <swagger-setting ref="swaggerSetting" />
      </div>
    </div>
    <!--dialog-->
    <el-dialog
      :title="dialogDebugEnvTitle"
      :visible.sync="dialogDebugEnvVisible"
      :close-on-click-modal="false"
      @close="resetForm('dialogDebugEnvForm')"
    >
      <el-tabs v-model="activeNameAdd" type="card">
        <el-tab-pane name="add" :label="$ts('newEnv')">
          <el-form
            ref="dialogDebugEnvForm"
            :rules="dialogDebugEnvFormRules"
            :model="dialogDebugEnvFormData"
            label-position="top"
            size="mini"
          >
            <el-form-item
              prop="name"
              :label="$ts('envName')"
            >
              <el-input v-model="dialogDebugEnvFormData.name" :placeholder="$ts('envNamePlaceholder')" show-word-limit maxlength="50" />
            </el-form-item>
            <el-form-item
              prop="url"
              :label="$ts('baseUrl')"
            >
              <el-input v-model="dialogDebugEnvFormData.url" :placeholder="$ts('baseUrlPlaceholder')" show-word-limit maxlength="100" />
            </el-form-item>
            <el-form-item
              prop="extendId"
              :label="$ts('isPublic')"
            >
              <el-radio-group v-model="dialogDebugEnvFormData.isPublic">
                <el-radio :label="1">{{ $ts('yes') }}</el-radio>
                <el-radio :label="0">{{ $ts('no') }}</el-radio>
                <span class="info-tip">{{ $ts('debugEnvPublicTip') }}</span>
              </el-radio-group>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        <el-tab-pane name="importEnv" :label="$ts('importEnv')">
          <el-tree
            ref="tree"
            :data="treeRows"
            :highlight-current="true"
            :expand-on-click-node="true"
            default-expand-all
            :empty-text="$ts('noData')"
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
        <el-button @click="dialogDebugEnvVisible = false">{{ $ts('dlgCancel') }}</el-button>
        <el-button type="primary" @click="onDialogDebugEnvSave">{{ $ts('dlgSave') }}</el-button>
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
          :label="$ts('envName')"
        >
          <el-input v-model="dialogCopyEnvFormData.name" :placeholder="$ts('envNamePlaceholder')" show-word-limit maxlength="50" />
        </el-form-item>
        <el-form-item
          prop="url"
          :label="$ts('baseUrl')"
        >
          <el-input v-model="dialogCopyEnvFormData.url" :placeholder="$ts('baseUrlPlaceholder')" show-word-limit maxlength="100" />
        </el-form-item>
        <el-form-item
          prop="extendId"
          :label="$ts('isPublic')"
        >
          <el-radio-group v-model="dialogCopyEnvFormData.isPublic">
            <el-radio :label="1">{{ $ts('yes') }}</el-radio>
            <el-radio :label="0">{{ $ts('no') }}</el-radio>
            <span class="info-tip">{{ $ts('debugEnvPublicTip') }}</span>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogCopyEnvVisible = false">{{ $ts('dlgCancel') }}</el-button>
        <el-button type="primary" @click="onDialogCopyEnvSave">{{ $ts('dlgSave') }}</el-button>
      </div>
    </el-dialog>

  </div>
</template>
<script>
import PopoverUpdate from '@/components/PopoverUpdate'
import GlobalHeaders from '@/components/ModuleSetting/GlobalHeaders'
import GlobalParams from '@/components/ModuleSetting/GlobalParams'
import GlobalReturns from '@/components/ModuleSetting/GlobalReturns'
import SwaggerSetting from '@/components/ModuleSetting/SwaggerSetting'

// 添加国际化
$addI18n({
  'addEnv': { 'zh': '添加环境', 'en': 'Add Environment' },
  'newEnv': { 'zh': '新环境', 'en': 'New Environment' },
  'importEnv': { 'zh': '从其它模块导入', 'en': 'Import from other modules' },
  'swaggerSetting': { 'zh': 'Swagger设置', 'en': 'Swagger Setting' },
  'copyCurrent': { 'zh': '复制环境', 'en': 'Duplicate' },
  'updateCurrent': { 'zh': '修改环境', 'en': 'Edit environment' },
  'deleteCurrent': { 'zh': '删除环境', 'en': 'Delete environment' },
  'copyEnv': { 'zh': '{0} 拷贝', 'en': '{0} Copy' },
  'commonSetting': { 'zh': '公共设置', 'en': 'Common Settings' },
  'plzCheckEnv': { 'zh': '请勾选环境', 'en': 'Please select environment' }
})

export default {
  name: 'ModuleSetting',
  components: { PopoverUpdate, GlobalHeaders, GlobalParams, GlobalReturns, SwaggerSetting },
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
      moduleVO: {
        id: '',
        name: '',
        type: 0
      },
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
          { required: true, message: this.$ts('notEmpty'), trigger: 'blur' }
        ],
        url: [
          { required: true, message: this.$ts('notEmpty'), trigger: 'blur' }
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
      this.loadModuleInfo(this.moduleId)
      this.loadSwaggerConfig(this.moduleId)
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
    loadModuleInfo(moduleId) {
      this.get('/module/info', { moduleId: moduleId }, resp => {
        this.moduleVO = resp.data
      })
    },
    loadSwaggerConfig(moduleId) {
      const $ref = this.$refs.swaggerSetting
      $ref && $ref.reload(moduleId)
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
    onSaveName(value, done) {
      const param = {
        id: this.moduleVO.id,
        name: value
      }
      this.post('/module/name/update', param, () => {
        this.tipSuccess(this.$ts('updateSuccess'))
        this.moduleVO.name = value
        done()
      })
    },
    onModuleDelete() {
      this.confirm(this.$ts('deleteModuleConfirm'), () => {
        this.post('/module/delete', { id: this.moduleId }, () => {
          alert(this.$ts('deleteSuccess'))
          location.reload()
        })
      })
    },
    onEnvAdd() {
      this.loadEnvTree()
      this.dialogDebugEnvTitle = this.$ts('addEnv')
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
      this.confirm(this.$ts('deleteConfirm', this.curEnv.name), () => {
        this.post('/module/environment/delete', this.curEnv, () => {
          this.tip(this.$ts('deleteSuccess'))
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
            this.tipSuccess($ts('updateSuccess'))
            this.reload()
          })
        }
      })
    },
    onEnvCopy() {
      this.dialogCopyEnvTitle = this.$ts('copyEnv', this.curEnv.name)
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
        this.tipError($ts('plzCheckEnv'))
        return
      }
      const envIds = nodes.map(row => row.envId)
      const data = {
        destModuleId: this.moduleId,
        envIds: envIds
      }
      this.post('/module/environment/import', data, resp => {
        this.tipSuccess($ts('importSuccess'))
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
