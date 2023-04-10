<template>
  <div>
    <div class="openapi-tip">
      <h3>{{ $t('whatsOpenApi') }}</h3>
      <div>
        {{ $t('whatsOpenApiText') }}
      </div>
      <h3>{{ $t('useStep') }}</h3>
      <p>{{ $t('useStep1') }}</p>
      <p>{{ $t('useStep2') }}</p>
    </div>
    <el-form class="text-form token-form" label-width="120px">
      <el-form-item :label="$t('requestUrl')">
        {{ `${getBaseUrl()}/api` }}
      </el-form-item>
      <el-form-item label="token">
        {{ token }}<br>
        <el-popconfirm
          :title="$t('refreshTokenConfirm')"
          @confirm="refreshToken"
        >
          <el-button slot="reference" type="text">{{ $t('refreshToken') }}</el-button>
        </el-popconfirm>
      </el-form-item>
<!--      <el-form-item :label="$t('OpenApi.printPushContent')">-->
<!--        <el-switch-->
<!--          v-model="printPushContent"-->
<!--          :active-text="$t('yes')"-->
<!--          :inactive-text="$t('no')"-->
<!--          :active-value="true"-->
<!--          :inactive-value="false"-->
<!--          @change="() => onChange(getEnums().ModuleConfig.TORNA_PUSH_PRINT_CONTENT, printPushContent)"-->
<!--        />-->
<!--        <span class="info-tip">{{ $t('OpenApi.printPushContentTip') }}</span>-->
<!--      </el-form-item>-->
      <el-form-item :label="$t('OpenApi.contentOverride')">
        <el-switch
          v-model="contentOverride"
          :active-text="$t('yes')"
          :inactive-text="$t('no')"
          :active-value="true"
          :inactive-value="false"
          @change="() => onChange(getEnums().ModuleConfig.TORNA_PUSH_OVERRIDE, contentOverride)"
        />
        <span class="info-tip">{{ $t('OpenApi.contentOverrideTip') }}</span>
      </el-form-item>
    </el-form>
    <el-link type="primary" :underline="false" href="https://torna.cn/dev/smart-doc.html" target="_blank">[{{ $t('recommend') }}]{{ $t('useSmartDoc') }}</el-link>
    <span class="split">|</span>
    <el-link type="primary" :underline="false" @click="openLink('/openapi')">{{ $t('openApiLink') }}</el-link>
  </div>
</template>
<style>
.token-form {
  border: 1px dotted #ccc;
  margin-bottom: 10px;
}
.openapi-tip {
  font-size: 14px;
  color: #303133;
  margin: 20px 0;
}
</style>
<script>
export default {
  name: 'ModuleOpenApi',
  data() {
    return {
      moduleId: '',
      token: '',
      menus: [],
      printPushContent: false,
      contentOverride: false
    }
  },
  methods: {
    reload(moduleId) {
      if (moduleId) {
        this.moduleId = moduleId
      }
      this.loadToken(this.moduleId)
      this.loadConfig(this.moduleId)
    },
    refreshToken() {
      this.get('/module/token/refresh', { moduleId: this.moduleId }, function(resp) {
        this.tipSuccess(this.$t('refreshSuccess'))
        this.token = resp.data
      })
    },
    loadToken(moduleId) {
      if (moduleId) {
        this.get('/module/token/get', { moduleId: moduleId }, function(resp) {
          this.token = resp.data
        })
      }
    },
    loadConfig(moduleId) {
      this.contentOverride = false
      const keyPushOverride = this.getEnums().ModuleConfig.TORNA_PUSH_OVERRIDE
      const keyPushPrintContent = this.getEnums().ModuleConfig.TORNA_PUSH_PRINT_CONTENT
      const keys = [
        keyPushOverride,
        keyPushPrintContent
      ]
      this.get('/module/setting/common/list', { moduleId: moduleId, keys: keys.join(',') }, resp => {
        const list = resp.data
        for (const config of list) {
          if (config.configKey === keyPushOverride) {
            this.contentOverride = config.configValue === 'true'
          } else if (config.configKey === keyPushPrintContent) {
            this.printPushContent = config.configValue === 'true'
          }
        }
      })
    },
    onChange(key, value) {
      this.post('/module/setting/common/set', {
        moduleId: this.moduleId,
        configKey: key,
        configValue: value
      }, resp => {
        this.tipSuccess($t('updateSuccess'))
      })
    }
  }
}
</script>
