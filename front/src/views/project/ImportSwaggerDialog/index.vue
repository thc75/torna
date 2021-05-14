<template>
  <el-dialog
    :title="$ts('importSwaggerDoc')"
    :visible.sync="importJsonDlgShow"
    @close="onHide"
  >
    <el-form
      ref="importJsonForm"
      :model="importJsonFormData"
      :rules="importJsonRule"
      size="mini"
      label-width="100px"
    >
      <el-form-item label="URL" prop="importUrl">
        <el-input
          v-model="importJsonFormData.importUrl"
          :placeholder="$ts('importSwaggerPlaceholder')"
          show-word-limit
          maxlength="100"
        />
      </el-form-item>
      <el-form-item :label="$ts('basicAuth')">
        <el-col :span="12" style="padding-right: 10px;">
          <el-input v-model="importJsonFormData.basicAuthUsername" :placeholder="$ts('optionalUsername')" style="width: 100%;" />
        </el-col>
        <el-col :span="12">
          <el-input v-model="importJsonFormData.basicAuthPassword" :placeholder="$ts('optionalPassword')" style="width: 100%;" />
        </el-col>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="importJsonDlgShow = false">{{ $ts('dlgCancel') }}</el-button>
      <el-button :loading="importJsonLoading" type="primary"  @click="onImportSwaggerSave">{{ $ts('dlgImport') }}</el-button>
    </div>
  </el-dialog>
</template>
<script>
export default {
  name: 'ImportSwaggerDialog',
  props: {
    projectId: {
      type: String,
      default: ''
    },
    success: {
      type: Function,
      default: () => {}
    }
  },
  data() {
    return {
      importJsonDlgShow: false,
      importJsonLoading: false,
      importJsonFormData: {
        projectId: '',
        importUrl: '',
        basicAuthUsername: '',
        basicAuthPassword: ''
      },
      importJsonRule: {
        importUrl: [
          { required: true, message: this.$ts('notEmpty'), trigger: 'blur' },
          { validator: (rule, value, callback) => {
            if (value && !/^http(s)?:\/\/.+$/i.test(value)) {
              callback(new Error(this.$ts('errorUrl')))
            } else {
              callback()
            }
          }, trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    show() {
      this.onImportSwagger()
    },
    onHide() {
      this.resetForm('importJsonForm')
    },
    onImportSwagger() {
      this.importJsonLoading = false
      this.importJsonDlgShow = true
    },
    onImportSwaggerSave() {
      this.$refs.importJsonForm.validate((valid) => {
        if (valid) {
          this.importJsonLoading = true
          this.importJsonFormData.projectId = this.projectId
          this.formatHost(this.importJsonFormData)
          this.post('/module/import/swagger', this.importJsonFormData, resp => {
            if (resp.code !== '0') {
              this.tipError(resp.msg)
            } else {
              this.importJsonLoading = false
              this.importJsonDlgShow = false
              this.tipSuccess(this.$ts('importSuccess'))
              this.success && this.success(resp)
            }
          }, () => {
            this.importJsonLoading = false
          })
        }
      })
    }
  }
}
</script>
