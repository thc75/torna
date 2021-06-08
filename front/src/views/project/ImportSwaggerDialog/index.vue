<template>
  <el-dialog
    :title="$ts('importSwaggerDoc')"
    :visible.sync="importJsonDlgShow"
    @close="onHide"
  >
    <el-tabs v-model="active" type="card">
      <el-tab-pane label="插件导入[推荐]" name="first">
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
      </el-tab-pane>
      <el-tab-pane label="URL导入" name="second">
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
      </el-tab-pane>
    </el-tabs>
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
