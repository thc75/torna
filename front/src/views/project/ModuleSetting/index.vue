<template>
  <div>
    <div v-if="hasRole(`project:${projectId}`, [Role.admin])" style="float: right">
      <el-button type="danger" size="mini" @click="onModuleDelete">{{ $ts('deleteModule') }}</el-button>
    </div>
    <h3>
      {{ $ts('moduleName') }}：
      <span style="font-weight: normal">
        {{ moduleVO.name }}
        <popover-update
          v-if="hasRole(`project:${projectId}`, [Role.admin, Role.dev])"
          :value="moduleVO.name"
          :on-show="() => {return moduleVO.name}"
          :on-save="onSaveName"
        />
      </span>
    </h3>
    <h3>
      {{ $ts('debugEnv') }}
    </h3>

    <div v-if="hasRole(`project:${projectId}`, [Role.admin, Role.dev])" style="margin-bottom: 10px">
      <el-button type="primary" size="mini" @click="onEnvAdd">{{ $ts('addEnv') }}</el-button>
    </div>

    <div v-show="envs.length > 0">
      <el-tabs v-model="activeNameEnv" type="card" @tab-click="onEnvTabClick">
        <el-tab-pane v-for="env in envs" :key="env.id" :name="env.id" :label="env.name">
          <span slot="label">
            <el-tooltip placement="top" :content="$ts('public')" :open-delay="200">
              <i v-show="env.isPublic === 1" class="el-icon-view"></i>
            </el-tooltip>
            {{ env.name }}
            <el-dropdown
              v-if="hasRole(`project:${projectId}`, [Role.admin, Role.dev])"
              v-show="env.id === curEnv.id"
              trigger="click"
              style="margin-left: 5px;"
              @command="handleCommand"
            >
              <span class="el-dropdown-link">
                <el-tooltip placement="top" :content="$ts('moreOperation')" :open-delay="500">
                  <a class="el-icon-setting el-icon--right"></a>
                </el-tooltip>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item icon="el-icon-edit" :command="onEnvUpdate">{{ $ts('update') }}</el-dropdown-item>
                <el-dropdown-item icon="el-icon-delete" class="danger" :command="onEnvDelete">{{ $ts('delete') }}</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </span>
        </el-tab-pane>
      </el-tabs>
      <h4>{{ $ts('baseUrl') }}：<span>{{ curEnv.url }}</span></h4>
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
      <div v-if="moduleVO.type === 1">
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
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogDebugEnvVisible = false">{{ $ts('dlgCancel') }}</el-button>
        <el-button type="primary" @click="onDialogDebugEnvSave">{{ $ts('dlgSave') }}</el-button>
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

$addI18n({
  'addEnv': { 'zh': '添加环境', 'en': 'Add Environment' },
  'importEnv': { 'zh': '其它模块导入', 'en': 'Import from other modules' },
  'swaggerSetting': { 'zh': 'Swagger设置', 'en': 'Swagger Setting' }
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
        description: '',
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
      isNew: false
    }
  },
  methods: {
    reload(moduleId) {
      if (moduleId) {
        this.moduleId = moduleId
      }
      this.loadModuleInfo(this.moduleId)
      this.loadEnvs(this.moduleId)
      this.activeNameEnv = ''
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
    loadEnvs(moduleId) {
      this.get('/module/environment/list', { moduleId: moduleId }, resp => {
        this.envs = resp.data
        if (this.envs.length > 0) {
          let envId
          // 刚刚保存后
          if (this.isNew) {
            envId = this.envs[this.envs.length - 1].id
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
      this.dialogDebugEnvTitle = this.$ts('addEnv')
      this.dialogDebugEnvVisible = true
      this.dialogDebugEnvFormData.id = ''
    },
    onEnvUpdate() {
      this.dialogDebugEnvTitle = this.$ts('updateEnv')
      this.dialogDebugEnvVisible = true
      this.$nextTick(() => {
        Object.assign(this.dialogDebugEnvFormData, this.curEnv)
      })
    },
    onEnvDelete() {
      this.confirm(this.$ts('deleteConfirm', this.curEnv.name), () => {
        this.post('/module/environment/delete', this.curEnv, () => {
          this.tip(this.$ts('deleteSuccess'))
          this.curEnv.id = ''
          this.reload()
        })
      })
    },
    onDialogDebugEnvSave() {
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
    }
  }
}
</script>
