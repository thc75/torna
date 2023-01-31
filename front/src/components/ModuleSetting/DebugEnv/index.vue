<template>
  <div>
    <el-button
      type="primary"
      size="mini"
      style="margin-bottom: 10px"
      @click="onDebugEnvAdd"
    >
      {{ $t('addEnv') }}
    </el-button>
    <el-table
      :data="debugEnv"
      border
      highlight-current-row
    >
      <el-table-column :label="$t('envName')" prop="name" width="300px" />
      <el-table-column :label="$t('baseUrl')" prop="url" />
      <el-table-column :label="$t('isPublic')" prop="isPublic" width="100px">
        <template slot-scope="scope">
          {{ scope.row.isPublic === 1 ? $t('yes') : $t('no') }}
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('operation')"
        width="150"
      >
        <template slot-scope="scope">
          <el-link type="primary" size="mini" @click="onDebugEnvUpdate(scope.row)">{{ $t('update') }}</el-link>
          <el-popconfirm
            :title="$t('deleteConfirm', scope.row.name)"
            @confirm="onDebugEnvDelete(scope.row)"
          >
            <el-link slot="reference" type="danger" size="mini">{{ $t('delete') }}</el-link>
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
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogDebugEnvVisible = false">{{ $t('dlgCancel') }}</el-button>
        <el-button type="primary" @click="onDialogDebugEnvSave">{{ $t('dlgSave') }}</el-button>
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
        name: '',
        url: '',
        description: '',
        isPublic: 0
      },
      dialogDebugEnvFormRules: {
        name: [
          { required: true, message: this.$t('notEmpty'), trigger: 'blur' }
        ],
        url: [
          { required: true, message: this.$t('notEmpty'), trigger: 'blur' }
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
      this.get('/module/environment/list', { moduleId: moduleId }, resp => {
        this.debugEnv = resp.data
      })
    },
    onDebugEnvAdd() {
      this.dialogDebugEnvTitle = this.$t('addEnv')
      this.dialogDebugEnvVisible = true
      this.dialogDebugEnvFormData.id = ''
    },
    onDebugEnvUpdate(row) {
      this.dialogDebugEnvTitle = this.$t('updateEnv')
      this.dialogDebugEnvVisible = true
      this.$nextTick(() => {
        Object.assign(this.dialogDebugEnvFormData, row)
      })
    },
    onDebugEnvDelete(row) {
      this.post('/module/environment/delete', row, () => {
        this.tip(this.$t('deleteSuccess'))
        this.reload()
      })
    },
    onDialogDebugEnvSave() {
      this.$refs.dialogDebugEnvForm.validate((valid) => {
        if (valid) {
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
