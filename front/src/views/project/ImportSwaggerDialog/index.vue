<template>
  <el-dialog
    title="导入Swagger文档"
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
      <el-form-item label="URL" prop="url">
        <el-input
          v-model="importJsonFormData.importUrl"
          placeholder="输入URL，如：http://xxx:8080/swagger/doc.json"
          show-word-limit
          maxlength="100"
        />
      </el-form-item>
      <el-form-item label="Basic认证">
        <el-col :span="12" style="padding-right: 10px;">
          <el-input v-model="importJsonFormData.basicAuthUsername" placeholder="选填，username" style="width: 100%;" />
        </el-col>
        <el-col :span="12">
          <el-input v-model="importJsonFormData.basicAuthPassword" placeholder="选填，password" style="width: 100%;" />
        </el-col>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="importJsonDlgShow = false">取 消</el-button>
      <el-button :loading="importJsonLoading" type="primary"  @click="onImportSwaggerSave">导 入</el-button>
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
          { required: true, message: '不能为空', trigger: 'blur' },
          { validator: (rule, value, callback) => {
            if (value && !/^http(s)?:\/\/.+$/i.test(value)) {
              callback(new Error('URL格式不正确'))
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
            this.importJsonLoading = false
            this.importJsonDlgShow = false
            this.tipSuccess('导入成功')
            this.success && this.success()
          }, () => {
            this.importJsonLoading = false
          })
        }
      })
    }
  }
}
</script>
