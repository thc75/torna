<template>
  <el-dialog
    :title="$ts('importSwaggerDoc')"
    :visible.sync="importJsonDlgShow"
    @close="onHide"
  >
    <el-tabs v-model="active" type="card">
      <el-tab-pane :label="$ts('pluginImport')" name="first">
        <div style="max-height: 400px;overflow-y: auto">
          <div class="tip">{{ $ts('pluginImportTip') }}</div>
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
      <el-tab-pane :label="$ts('urlImport')" name="second">
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
              :placeholder="$ts('importSwaggerPlaceholder')"
              show-word-limit
              maxlength="100"
            />
          </el-form-item>
          <el-form-item :label="$ts('basicAuth')">
            <el-col :span="12" style="padding-right: 10px;">
              <el-input v-model="importJsonFormData.authUsername" :placeholder="$ts('optionalUsername')" style="width: 100%;" />
            </el-col>
            <el-col :span="12">
              <el-input v-model="importJsonFormData.authPassword" :placeholder="$ts('optionalPassword')" style="width: 100%;" />
            </el-col>
          </el-form-item>
        </el-form>
      </el-tab-pane>
      <el-tab-pane :label="$ts('swaggerContentImport')" name="third">
        <el-form
          ref="importContentForm"
          :model="importContentFormData"
          :rules="importContentFormRule"
          size="mini"
        >
          <el-form-item prop="content">
            <el-input v-model="importContentFormData.content" type="textarea" :rows="15" :placeholder="$ts('swaggerContentImportTip')" />
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>
    <el-alert
      v-if="active !== 'first'"
      :title="$ts('importTip')"
      type="info"
      show-icon
      :closable="false"
    />
    <div v-if="active !== 'first'" slot="footer" class="dialog-footer">
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

$addI18n({
  'pluginImport': { 'zh': '插件导入', 'en': 'Plugin Import' },
  'pluginImportTip': { 'zh': '如果是Java项目且用Maven管理可使用此方式，优点：无需启动项目即可推送文档',
    'en': 'If your project written in Java and managed by Maven, you can use this method. Advantages: You can push documents without starting the project' },
  'urlImport': { 'zh': 'URL导入', 'en': 'URL Import' },
  'importing': { 'zh': '导入中...', 'en': 'Importing' },
  'swaggerContentImport': { 'zh': ' JSON/YAML导入', 'en': 'JSON/YAML Import' },
  'swaggerContentImportTip': { 'zh': '输入swagger文档内容，支持json或yaml格式', 'en': 'Input swagger doc content,support json or yaml' },
  'timeToRefresh': { 'zh': '定时刷新', 'en': 'Time to refresh' },
  'refreshInterval': { 'zh': '刷新间隔', 'en': 'Refresh Interval' },
  'importTip': { 'zh': '提示：服务端异步处理导入过程，保存后可能无法看到文档，请过2~3秒再刷新查看',
    'en': 'Note: The import process is asynchronous on the server. After the file is saved successfully, you cannot see the file immediately. Refresh after 3 to 5 seconds' }
})
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
          { required: true, message: this.$ts('notEmpty'), trigger: 'blur' }
        ]
      },
      importJsonRule: {
        url: [
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
        text: $ts('importing'),
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      })
    },
    fireSuccess(loading, resp) {
      setTimeout(() => {
        this.importJsonDlgShow = false
        loading.close()
        this.tipSuccess(this.$ts('importSuccess'))
        this.success && this.success(resp)
      }, 1500)
    }
  }
}
</script>
