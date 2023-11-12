<template>
  <el-dialog
    :title="$t('importSwaggerDoc')"
    :visible.sync="importJsonDlgShow"
    @close="onHide"
  >
    <el-tabs v-model="active" type="card">
      <el-tab-pane :label="$t('ImportSwaggerDialog.pluginImport')" name="first">
        <div style="max-height: 400px;overflow-y: auto">
          <div class="tip">{{ $t('ImportSwaggerDialog.pluginImportTip') }}</div>
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
      <el-tab-pane :label="$t('ImportSwaggerDialog.urlImport')" name="second">
        <el-form
          ref="importJsonForm"
          :model="importJsonFormData"
          :rules="importJsonRule"
          size="mini"
          label-width="100px"
        >
          <el-form-item label="URL" prop="url">
            <el-input
              v-model="importJsonFormData.url"
              :placeholder="$t('importSwaggerPlaceholder')"
              show-word-limit
              maxlength="100"
            />
          </el-form-item>
          <el-form-item :label="$t('basicAuth')">
            <el-col :span="12" style="padding-right: 10px;">
              <el-input v-model="importJsonFormData.authUsername" :placeholder="$t('optionalUsername')" style="width: 100%;" />
            </el-col>
            <el-col :span="12">
              <el-input v-model="importJsonFormData.authPassword" :placeholder="$t('optionalPassword')" style="width: 100%;" />
            </el-col>
          </el-form-item>
        </el-form>
      </el-tab-pane>
      <el-tab-pane :label="$t('ImportSwaggerDialog.swaggerContentImport')" name="third">
        <el-form
          ref="importContentForm"
          :model="importContentFormData"
          :rules="importContentFormRule"
          size="mini"
        >
          <el-form-item prop="content">
            <el-input v-model="importContentFormData.content" type="textarea" :rows="15" :placeholder="$t('ImportSwaggerDialog.swaggerContentImportTip')" />
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>
    <el-alert
      v-if="active !== 'first'"
      :title="$t('ImportSwaggerDialog.importTip')"
      type="info"
      show-icon
      :closable="false"
    />
    <div v-if="active !== 'first'" slot="footer" class="dialog-footer">
      <el-button @click="importJsonDlgShow = false">{{ $t('dlgCancel') }}</el-button>
      <el-button :loading="importJsonLoading" type="primary" @click="onImportSwaggerSave">{{ $t('dlgImport') }}</el-button>
    </div>
    <div v-else slot="footer" class="dialog-footer">
      <el-button type="primary" @click="importJsonDlgShow = false">{{ $t('ok') }}</el-button>
    </div>
  </el-dialog>
</template>
<script>
import { mavonEditor } from 'mavon-editor'

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
        url: '',
        content: '',
        authUsername: '',
        authPassword: '',
        refreshMinutes: 5,
        refreshStatus: 0
      },
      importContentFormData: {
        projectId: '',
        content: ''
      },
      importContentFormRule: {
        content: [
          { required: true, message: this.$t('notEmpty'), trigger: 'blur' }
        ]
      },
      importJsonRule: {
        url: [
          { required: true, message: this.$t('notEmpty'), trigger: 'blur' },
          { validator: (rule, value, callback) => {
            if (value && !/^http(s)?:\/\/.+$/i.test(value)) {
              callback(new Error(this.$t('errorUrl')))
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
      this.resetForm('importContentForm')
    },
    onImportSwagger() {
      this.importJsonLoading = false
      this.importJsonDlgShow = true
    },
    onImportSwaggerSave() {
      if (this.active === 'second') {
        this.$refs.importJsonForm.validate((valid) => {
          if (valid) {
            const loading = this.loadImporting()
            this.importJsonFormData.projectId = this.projectId
            this.formatHost(this.importJsonFormData)
            this.post('/module/import/swaggerV2', this.importJsonFormData, resp => {
              this.fireSuccess(loading, resp)
            }, () => { loading.close() })
          }
        })
      }
      if (this.active === 'third') {
        this.$refs.importContentForm.validate((valid) => {
          if (valid) {
            const loading = this.loadImporting()
            this.importContentFormData.projectId = this.projectId
            this.post('/module/import/swaggerV2', this.importContentFormData, resp => {
              this.fireSuccess(loading, resp)
            }, () => { loading.close() })
          }
        })
      }
    },
    loadImporting() {
      return this.$loading({
        lock: true,
        text: $t('ImportSwaggerDialog.importing'),
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      })
    },
    fireSuccess(loading, resp) {
      setTimeout(() => {
        this.importJsonDlgShow = false
        loading.close()
        this.tipSuccess(this.$t('importSuccess'))
        this.success && this.success(resp)
      }, 1500)
    }
  }
}
</script>
