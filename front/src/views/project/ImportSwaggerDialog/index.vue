<template>
  <el-dialog
    :title="$ts('importSwaggerDoc')"
    :visible.sync="importJsonDlgShow"
    @close="onHide"
  >
    <h3>使用插件导入</h3>
    <div style="max-height: 400px;overflow-y: auto">
      <mavon-editor
        v-model="content"
        :boxShadow="false"
        :subfield="false"
        defaultOpen="preview"
        :editable="false"
        :toolbarsFlag="false"
      />
    </div>
    <div v-if="active === 'second'" slot="footer" class="dialog-footer">
      <el-button @click="importJsonDlgShow = false">{{ $ts('dlgCancel') }}</el-button>
      <el-button :loading="importJsonLoading" type="primary" @click="onImportSwaggerSave">{{ $ts('dlgImport') }}</el-button>
    </div>
    <div v-else slot="footer" class="dialog-footer">
      <el-button type="primary" @click="importJsonDlgShow = false">{{ $ts('ok') }}</el-button>
    </div>
  </el-dialog>
</template>
<script>
import { mavonEditor } from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'
export default {
  name: 'ImportSwaggerDialog',
  components: { mavonEditor },
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
      },
      active: 'first',
      content: ''
    }
  },
  mounted() {
    this.getFile(`static/help/swagger-plugin.md?q=${new Date().getTime()}`, (content) => {
      this.content = content
    })
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
