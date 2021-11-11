<template>
  <div>
    <div class="openapi-tip">
      <h3>{{ $ts('whatsOpenApi') }}</h3>
      <div>
        {{ $ts('whatsOpenApiText') }}
      </div>
      <h3>{{ $ts('useStep') }}</h3>
      <p>{{ $ts('useStep1') }}</p>
      <p>{{ $ts('useStep2') }}</p>
    </div>
    <el-form class="text-form token-form" label-width="120px">
      <el-form-item :label="$ts('requestUrl')">
        {{ `${getBaseUrl()}/api` }}
      </el-form-item>
      <el-form-item label="token">
        {{ token }}
      </el-form-item>
      <el-form-item>
        <el-popconfirm
          :title="$ts('refreshTokenConfirm')"
          @confirm="refreshToken"
        >
          <el-button slot="reference" type="text">{{ $ts('refreshToken') }}</el-button>
        </el-popconfirm>
      </el-form-item>
    </el-form>
    <el-link type="primary" :underline="false" href="https://smart-doc-group.github.io/#/zh-cn/torna/tornaIntegration" target="_blank">[推荐]{{ $ts('useSmartDoc') }}</el-link>
    <span class="split">|</span>
    <el-link type="primary" :underline="false" @click="openLink('/openapi')">{{ $ts('openApiLink') }}</el-link>
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
        this.tipSuccess(this.$ts('refreshSuccess'))
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
