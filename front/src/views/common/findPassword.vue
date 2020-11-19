<template>
  <div class="app-container">
    <div class="open-sphere-log"><a href="http://opensphere.cn">XX</a></div>
    <el-form
      v-show="!submited"
      ref="findPasswordForm"
      :model="findPasswordFormData"
      :rules="findPasswordRules"
      class="center-form"
      @submit.native.prevent
    >
      <h3 class="title">密码找回</h3>
      <el-form-item prop="username">
        <el-input
          v-model="findPasswordFormData.username"
          placeholder="输入邮箱账号"
          prefix-icon="el-icon-ext-email"
        />
      </el-form-item>
      <el-button type="primary" style="width: 100%;" @click="onResetPassword">重置密码</el-button>
      <div class="footer">
        <el-button type="text" @click="() => goRoute('/login')">前往登录页</el-button>
      </div>
    </el-form>
    <el-form
      v-show="submited"
      class="center-form"
      @submit.native.prevent
    >
      <h3 class="title">密码找回</h3>
      <el-alert
        :closable="false"
        class="el-alert-tip"
      >
        <div slot="title">
          我们向邮箱 {{ encodeEmail(findPasswordFormData.username) }} 发送了一封含有重置密码链接的邮件。请登录邮箱查看，如长时间没有收到邮件，请检查你的垃圾邮件文件夹。
        </div>
      </el-alert>
      <el-button type="success" style="width: 100%;" @click="goEmailSite">前往登录邮箱</el-button>
      <div class="footer">
        <el-button type="text" @click="() => goRoute('login')">前往登录页</el-button>
      </div>
    </el-form>
  </div>
</template>
<script>
import { processEmail } from '@/utils/email'

export default {
  name: 'FindPassword',
  data() {
    return {
      findPasswordFormData: {
        username: ''
      },
      findPasswordRules: {
        username: [
          { required: true, message: '请输入登录邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ]
      },
      submited: false,
      loading: false
    }
  },
  created() {
  },
  methods: {
    onResetPassword() {
      this.$refs.findPasswordForm.validate(valid => {
        if (valid) {
          this.doSubmit()
        }
      })
    },
    goEmailSite: function() {
      const email = this.findPasswordFormData.username
      processEmail(email, (url) => {
        if (url) {
          location = url
        }
      })
    },
    onCaptchaSuccess(params) {
      this.doSubmit(function(data) {
        data.captcha = params
      })
    },
    doSubmit: function(callback) {
      const data = Object.assign({}, this.findPasswordFormData)
      callback && callback.call(this, data)
      this.get('nologin.password.find', data, resp => {
        this.submited = true
      })
    },
    useVerify() {
      this.$refs.verify.show()
    }
  }
}
</script>

