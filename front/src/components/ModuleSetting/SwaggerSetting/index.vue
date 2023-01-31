<template>
  <div>
    <div>
      <el-form
        ref="importJsonForm"
        :model="importJsonFormData"
        :rules="importJsonRule"
        label-width="100px"
        size="mini"
      >
        <el-form-item>
          <el-tooltip effect="dark" :content="$t('syncSwaggerDoc')" placement="top">
            <el-button
              type="primary"
              icon="el-icon-refresh"
              @click.stop="onRefreshSwagger"
            />
          </el-tooltip>
        </el-form-item>
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
        <el-form-item>
          <el-button type="primary" @click="onSwaggerConfigSave">{{ $t('save') }}</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>
<script>
export default {
  data() {
    return {
      setting: {
        allowMethod: ''
      },
      importJsonFormData: {
        id: '',
        url: '',
        content: '',
        authUsername: '',
        authPassword: ''
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
      moduleId: '',
      allMethods: ['GET', 'POST', 'PUT', 'PATCH', 'DELETE', 'OPTIONS', 'HEAD']
    }
  },
  methods: {
    reload(moduleId) {
      if (moduleId) {
        this.moduleId = moduleId
      }
      this.loadSetting(this.moduleId)
    },
    loadSetting(moduleId) {
      this.get('/module/setting/swaggerSetting/config/get', { moduleId: moduleId }, resp => {
        const data = resp.data
        if (data) {
          this.importJsonFormData = resp.data
        }
      })
    },
    onSwaggerConfigSave() {
      this.$refs.importJsonForm.validate((valid) => {
        if (valid) {
          this.formatHost(this.importJsonFormData)
          this.post('/module/setting/swaggerSetting/config/update', this.importJsonFormData, resp => {
            if (resp.code === '0') {
              this.confirm(this.$t('SwaggerSetting.syncConfirm'), () => {
                this.onRefreshSwagger()
              })
            }
          })
        }
      })
    },
    onRefreshSwagger() {
      const loading = this.$loading({
        lock: true,
        text: $t('SwaggerSetting.synchronizing'),
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      })
      this.get('/module/refresh/swaggerV2', { moduleId: this.moduleId }, () => {
        setTimeout(() => {
          loading.close()
          this.tipSuccess(this.$t('syncSuccess'))
        }, 1500)
      }, () => loading.close())
    },
    onSaveAllowMethods() {
      const data = {
        moduleId: this.moduleId,
        method: this.setting.allowMethod
      }
      this.post('/module/setting/swaggerSetting/allowMethod/set', data, () => {
        this.tipSuccess(this.$t('updateSuccess'))
      })
    }
  }
}
</script>
