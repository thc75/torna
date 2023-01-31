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
        {{ token }}
      </el-form-item>
      <el-form-item>
        <el-popconfirm
          :title="$t('refreshTokenConfirm')"
          @confirm="refreshToken"
        >
          <el-button slot="reference" type="text">{{ $t('refreshToken') }}</el-button>
        </el-popconfirm>
      </el-form-item>
    </el-form>
    <el-link type="primary" :underline="false" href="https://smart-doc-group.github.io/#/zh-cn/torna/tornaIntegration" target="_blank">[{{ $t('recommend') }}]{{ $t('useSmartDoc') }}</el-link>
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
      menus: []
    }
  },
  methods: {
    reload(moduleId) {
      if (moduleId) {
        this.moduleId = moduleId
      }
      this.loadToken(this.moduleId)
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
    }
  }
}
</script>
