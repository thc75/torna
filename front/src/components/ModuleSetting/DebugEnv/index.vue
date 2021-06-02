<template>
  <div>
    <el-button
      type="primary"
      size="mini"
      style="margin-bottom: 10px"
      @click="onDebugEnvAdd"
    >
      {{ $ts('addEnv') }}
    </el-button>
    <el-table
      :data="debugEnv"
      border
      highlight-current-row
    >
      <el-table-column :label="$ts('envName')" prop="configKey" width="300px" />
      <el-table-column :label="$ts('baseUrl')" prop="configValue" />
      <el-table-column :label="$ts('isPublic')" prop="extendId" width="100px">
        <template slot-scope="scope">
          {{ scope.row.extendId === 1 ? $ts('yes') : $ts('no') }}
        </template>
      </el-table-column>
      <el-table-column
        :label="$ts('operation')"
        width="150"
      >
        <template slot-scope="scope">
          <el-link type="primary" size="mini" @click="onDebugEnvUpdate(scope.row)">{{ $ts('update') }}</el-link>
          <el-popconfirm
            :title="$ts('deleteConfirm', scope.row.configKey)"
            @confirm="onDebugEnvDelete(scope.row)"
          >
            <el-link slot="reference" type="danger" size="mini">{{ $ts('delete') }}</el-link>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
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
          prop="configKey"
          :label="$ts('envName')"
        >
          <el-input v-model="dialogDebugEnvFormData.configKey" :placeholder="$ts('envNamePlaceholder')" show-word-limit maxlength="50" />
        </el-form-item>
        <el-form-item
          prop="configValue"
          :label="$ts('baseUrl')"
        >
          <el-input v-model="dialogDebugEnvFormData.configValue" :placeholder="$ts('baseUrlPlaceholder')" show-word-limit maxlength="100" />
        </el-form-item>
        <el-form-item
          prop="extendId"
          :label="$ts('isPublic')"
        >
          <el-radio-group v-model="dialogDebugEnvFormData.extendId">
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
export default {
  data() {
    return {
      debugEnv: [],
      moduleId: '',
      dialogDebugEnvVisible: false,
      dialogDebugEnvTitle: '',
      dialogDebugEnvFormData: {
        id: '',
        moduleId: '',
        configKey: '',
        configValue: '',
        description: '',
        extendId: 0
      },
      dialogDebugEnvFormRules: {
        configKey: [
          { required: true, message: this.$ts('notEmpty'), trigger: 'blur' }
        ],
        configValue: [
          { required: true, message: this.$ts('notEmpty'), trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    reload(moduleId) {
      if (moduleId) {
        this.moduleId = moduleId
      }
      this.loadDebugEnvs(this.moduleId)
    },
    loadDebugEnvs(moduleId) {
      this.get('/module/setting/debugEnv/list', { moduleId: moduleId }, resp => {
        this.debugEnv = resp.data
      })
    },
    onDebugEnvAdd() {
      this.dialogDebugEnvTitle = this.$ts('addEnv')
      this.dialogDebugEnvVisible = true
      this.dialogDebugEnvFormData.id = ''
    },
    onDebugEnvUpdate(row) {
      this.dialogDebugEnvTitle = this.$ts('updateEnv')
      this.dialogDebugEnvVisible = true
      this.$nextTick(() => {
        Object.assign(this.dialogDebugEnvFormData, row)
      })
    },
    onDebugEnvDelete(row) {
      this.post('/module/setting/debugEnv/delete', row, () => {
        this.tip(this.$ts('deleteSuccess'))
        this.reload()
      })
    },
    onDialogDebugEnvSave() {
      this.$refs.dialogDebugEnvForm.validate((valid) => {
        if (valid) {
          const uri = this.dialogDebugEnvFormData.id ? '/module/setting/debugEnv/update' : '/module/setting/debugEnv/add'
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
