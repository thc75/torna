<template>
  <div>
    <h2>
      {{ settings.moduleVO.name }}
      <popover-update
        v-if="hasRole(`project:${projectId}`, [Role.admin, Role.dev])"
        :value="settings.moduleVO.name"
        :on-show="() => {return settings.moduleVO.name}"
        :on-save="onSaveName"
      />
    </h2>
    <h3>全局Headers</h3>
    <el-button
      v-if="hasRole(`project:${projectId}`, [Role.dev, Role.admin])"
      type="primary"
      size="mini"
      style="margin-bottom: 10px"
      @click="onHeaderAdd"
    >
      添加Header
    </el-button>
    <el-table
      :data="settings.globalHeaders"
      border
      :header-cell-style="cellStyleSmall()"
      :cell-style="cellStyleSmall()"
    >
      <el-table-column label="Name" prop="configKey" width="300px" />
      <el-table-column label="Value" prop="configValue" />
      <el-table-column label="描述" prop="description" />
      <el-table-column
        v-if="hasRole(`project:${projectId}`, [Role.dev, Role.admin])"
        label="操作"
        width="150"
      >
        <template slot-scope="scope">
          <el-link type="primary" size="mini" @click="onHeaderUpdate(scope.row)">修改</el-link>
          <el-popconfirm
            :title="`确定要删除 ${scope.row.configKey} 吗？`"
            @onConfirm="onHeaderDelete(scope.row)"
          >
            <el-link slot="reference" type="danger" size="mini">删除</el-link>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <h3>调试环境 <span class="doc-id">请求基本路径</span></h3>
    <el-button
      v-if="hasRole(`project:${projectId}`, [Role.dev, Role.admin])"
      type="primary"
      size="mini"
      style="margin-bottom: 10px"
      @click="onDebugHostAdd"
    >
      添加环境
    </el-button>
    <el-form ref="debugHostRef" :model="settings" size="mini">
      <el-form-item>
        <el-tabs
          v-model="settings.debugEnv"
          type="card"
          :closable="hasRole(`project:${projectId}`, [Role.admin, Role.dev])"
          @tab-remove="onDebugHostDelete"
        >
          <el-tab-pane
            v-for="item in settings.debugEnvs"
            :key="item.configKey"
            :label="item.configKey"
            :name="item.configKey"
          >
            <el-input
              v-model="item.configValue"
              placeholder="如：http://10.0.10.11:8080"
              :disabled="!hasRole(`project:${projectId}`, [Role.admin, Role.dev])"
            />
          </el-tab-pane>
        </el-tabs>
      </el-form-item>
      <el-form-item v-if="hasRole(`project:${projectId}`, [Role.dev, Role.admin])">
        <el-button type="primary" @click="onSaveDebugHost">保存</el-button>
      </el-form-item>
    </el-form>
    <div v-if="settings.moduleVO.type === 1">
      <h3>Swagger多个Method重复，只显示</h3>
      <el-form ref="allowMethodsRef" :model="settings" size="mini">
        <el-form-item prop="allowMethods">
          <el-select v-if="hasRole(`project:${projectId}`, [Role.dev, Role.admin])" v-model="settings.allowMethod" @change="onSaveAllowMethods">
            <el-option v-for="method in allMethods" :key="method" :label="method" :value="method">
              {{ method }}
            </el-option>
          </el-select>
          <span v-else>
            {{ settings.allowMethod }}
          </span>
        </el-form-item>
      </el-form>
    </div>
    <div v-if="hasRole(`project:${projectId}`, [Role.admin])">
      <el-divider />
      <el-button type="danger" size="mini" @click="onModuleDelete">删除模块</el-button>
    </div>
    <!--dialog-->
    <el-dialog
      :title="dialogHeaderTitle"
      :visible.sync="dialogHeaderVisible"
      :close-on-click-modal="false"
      @close="resetForm('dialogHeaderForm')"
    >
      <el-form
        ref="dialogHeaderForm"
        :rules="dialogHeaderFormRules"
        :model="dialogHeaderFormData"
        label-width="120px"
        size="mini"
      >
        <el-form-item
          prop="configKey"
          label="Name"
        >
          <el-input v-model="dialogHeaderFormData.configKey" placeholder="name" show-word-limit maxlength="50" />
        </el-form-item>
        <el-form-item
          prop="configValue"
          label="Value"
        >
          <el-input v-model="dialogHeaderFormData.configValue" placeholder="value" show-word-limit maxlength="200" />
        </el-form-item>
        <el-form-item
          prop="description"
          label="描述"
        >
          <el-input v-model="dialogHeaderFormData.description" type="textarea" placeholder="描述" show-word-limit maxlength="200" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogHeaderVisible = false">取 消</el-button>
        <el-button type="primary" @click="onDialogHeaderSave">保 存</el-button>
      </div>
    </el-dialog>
    <el-dialog
      :title="dialogDebugHostTitle"
      :visible.sync="dialogDebugHostVisible"
      :close-on-click-modal="false"
      @close="resetForm('dialogDebugHostForm')"
    >
      <el-form
        ref="dialogDebugHostForm"
        :rules="dialogDebugHostFormRules"
        :model="dialogDebugHostFormData"
        label-width="120px"
        size="mini"
      >
        <el-form-item
          prop="configKey"
          label="环境名称"
        >
          <el-input v-model="dialogDebugHostFormData.configKey" placeholder="环境名称，如：测试环境" show-word-limit maxlength="50" />
        </el-form-item>
        <el-form-item
          prop="configValue"
          label="调试路径"
        >
          <el-input v-model="dialogDebugHostFormData.configValue" placeholder="如：http://10.0.10.11:8080" show-word-limit maxlength="200" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogDebugHostVisible = false">取 消</el-button>
        <el-button type="primary" @click="onDialogDebugHostSave">保 存</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import PopoverUpdate from '@/components/PopoverUpdate'
export default {
  name: 'ModuleSetting',
  components: { PopoverUpdate },
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
      settings: {
        debugEnv: '',
        moduleVO: {
          id: '',
          name: '',
          type: 0
        },
        debugEnvs: [],
        globalHeaders: [],
        allowMethod: 'POST',
        baseUrl: ''
      },
      allMethods: ['GET', 'POST', 'PUT', 'PATCH', 'DELETE', 'OPTIONS', 'HEAD'],
      dialogHeaderVisible: false,
      dialogHeaderTitle: '',
      dialogHeaderFormData: {
        id: '',
        moduleId: '',
        configKey: '',
        configValue: '',
        description: ''
      },
      dialogHeaderFormRules: {
        configKey: [
          { required: true, message: '不能为空', trigger: 'blur' },
          { validator: (rule, value, callback) => {
            if (value && !/^[a-zA-Z0-9\-_]+$/.test(value)) {
              callback(new Error('格式错误，支持大小写英文、数字、-、下划线'))
            } else {
              callback()
            }
          }, trigger: 'blur' }
        ], configValue: [
          { required: true, message: '不能为空', trigger: 'blur' }
        ]
      },
      // debugEnv
      dialogDebugHostVisible: false,
      dialogDebugHostTitle: '',
      dialogDebugHostFormData: {
        moduleId: '',
        configKey: '',
        configValue: ''
      },
      dialogDebugHostFormRules: {
        configKey: [
          { required: true, message: '不能为空', trigger: 'blur' }
        ],
        configValue: [
          { required: true, message: '不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    moduleId(id) {
      this.loadSettings(id)
    }
  },
  methods: {
    reload() {
      this.loadSettings(this.moduleId)
    },
    loadHeaders() {
      this.get('/module/setting/globalHeader/list', { moduleId: this.moduleId }, resp => {
        this.settings.globalHeaders = resp.data
      })
    },
    loadDebugHosts(init) {
      this.get('/module/setting/debugEnv/list', { moduleId: this.moduleId }, resp => {
        this.settings.debugEnvs = resp.data
        if (init) {
          this.initDebugHost()
        }
      })
    },
    loadSettings(moduleId) {
      if (moduleId) {
        this.get('/module/setting/get', { moduleId: moduleId }, function(resp) {
          this.settings = resp.data
          this.initDebugHost()
        })
      }
    },
    initDebugHost() {
      if (this.settings.debugEnvs.length > 0) {
        this.settings.debugEnv = this.settings.debugEnvs[0].configKey
      }
    },
    onSaveName(value, done) {
      const param = {
        id: this.settings.moduleVO.id,
        name: value
      }
      this.post('/module/name/update', param, () => {
        this.tipSuccess('修改成功')
        this.settings.moduleVO.name = value
        done()
      })
    },
    onHeaderAdd() {
      this.dialogHeaderTitle = '新增Header'
      this.dialogHeaderVisible = true
      this.dialogHeaderFormData.id = ''
    },
    onDebugHostAdd() {
      this.dialogDebugHostTitle = '新增调试环境'
      this.dialogDebugHostVisible = true
      this.dialogDebugHostFormData.id = ''
    },
    onDialogDebugHostSave() {
      this.$refs.dialogDebugHostForm.validate((valid) => {
        if (valid) {
          const uri = '/module/setting/debugEnv/set'
          this.dialogDebugHostFormData.moduleId = this.moduleId
          this.post(uri, this.dialogDebugHostFormData, () => {
            this.tipSuccess('添加成功')
            this.settings.debugEnv = this.dialogDebugHostFormData.configKey
            this.dialogDebugHostVisible = false
            this.loadDebugHosts()
          })
        }
      })
    },
    onHeaderUpdate(row) {
      this.dialogHeaderTitle = '修改Header'
      this.dialogHeaderVisible = true
      this.$nextTick(() => {
        Object.assign(this.dialogHeaderFormData, row)
      })
    },
    onHeaderDelete(row) {
      this.post('/module/setting/globalHeader/delete', row, () => {
        this.tip('删除成功')
        this.loadHeaders()
      })
    },
    onDialogHeaderSave() {
      this.$refs.dialogHeaderForm.validate((valid) => {
        if (valid) {
          const uri = this.dialogHeaderFormData.id ? '/module/setting/globalHeader/update' : '/module/setting/globalHeader/add'
          this.dialogHeaderFormData.moduleId = this.moduleId
          this.post(uri, this.dialogHeaderFormData, () => {
            this.dialogHeaderVisible = false
            this.loadHeaders()
          })
        }
      })
    },
    onSaveAllowMethods() {
      const data = {
        moduleId: this.moduleId,
        method: this.settings.allowMethod
      }
      this.post('/module/setting/allowMethod/set', data, () => {
        this.tipSuccess('修改成功')
      })
    },
    onSaveDebugHost() {
      this.$refs.debugHostRef.validate((valid) => {
        if (valid) {
          const data = {
            debugEnvs: this.settings.debugEnvs
          }
          this.post('/module/setting/debugEnv/save', data, () => {
            this.tipSuccess('保存成功')
          })
        }
      })
    },
    onDebugHostDelete(name) {
      this.confirm(`确认要删除 ${name} 吗？`, () => {
        this.settings.debugEnvs.forEach(row => {
          if (row.configKey === name) {
            this.post('/module/setting/debugEnv/delete', row, () => {
              this.tipSuccess('删除成功')
              this.loadDebugHosts(true)
            })
          }
        })
      })
    },
    onModuleDelete() {
      this.confirm('确认要删除该模块吗？', () => {
        this.post('/module/delete', { id: this.moduleId }, () => {
          alert('删除成功')
          location.reload()
        })
      })
    }
  }
}
</script>
