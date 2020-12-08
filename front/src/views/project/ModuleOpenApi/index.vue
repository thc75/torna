<template>
  <div>
    <div class="openapi-tip">
      <h3>OpenAPI的作用</h3>
      <div>
        可通过接口调用方式操作文档，第三方App以此来更新文档内容。
      </div>
      <h3>使用步骤</h3>
      <p>1、找到超级管理员索要AppKey和Secret</p>
      <p>2、项目依赖SDK，使用SDK中的API调用接口</p>
    </div>
    <el-form class="text-form token-form" label-width="120px">
      <el-form-item label="请求路径">
        {{ `${getBaseUrl()}/api` }}
      </el-form-item>
      <el-form-item label="AppKey/Secret">
        超级管理员给到
      </el-form-item>
      <el-form-item label="token">
        {{ token }}
      </el-form-item>
      <el-form-item>
        <el-popconfirm
          title="确定要刷新token吗？老token将不可用"
          @onConfirm="refreshToken"
        >
          <el-button slot="reference" type="text">刷新token</el-button>
        </el-popconfirm>
      </el-form-item>
    </el-form>
    <router-link to="/openapi" target="_blank">
      <el-button type="text">OpenAPI接口文档</el-button>
    </router-link>
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
  props: {
    moduleId: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      token: '',
      menus: []
    }
  },
  watch: {
    moduleId(id) {
      this.loadToken(id)
    }
  },
  methods: {
    reload() {
      this.loadToken(this.moduleId)
    },
    refreshToken() {
      this.get('/module/token/refresh', { moduleId: this.moduleId }, function(resp) {
        this.tipSuccess('刷新成功')
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
