<template>
  <div class="app-container">
    <el-form
      ref="loginForm"
      :model="loginForm"
      :rules="loginRules"
      class="center-form"
      auto-complete="on"
      @submit.native.prevent
    >
      <div class="title-container">
        <h3 class="title">开放平台登录</h3>
      </div>
      <el-form-item prop="username">
        <el-input
          v-model="loginForm.username"
          placeholder="登录邮箱"
          prefix-icon="el-icon-user"
          auto-complete="on"
        />
      </el-form-item>
      <el-form-item prop="password">
        <el-input
          v-model="loginForm.password"
          type="password"
          placeholder="登录密码"
          prefix-icon="el-icon-lock"
          auto-complete="on"
        />
      </el-form-item>
      <el-button :loading="loading" type="primary" style="width: 100%;" native-type="submit" @click="handleLogin">登 录</el-button>
      <div class="footer">
        <el-link type="primary" :underline="false" @click="onReg">注册新账号</el-link>
      </div>
    </el-form>
  </div>
</template>
<script>
import md5 from 'js-md5'
import { setToken, removeToken } from '@/utils/auth'

export default {
  name: 'Login',
  data() {
    const validateUsername = (rule, value, callback) => {
      if (value.length === 0) {
        callback(new Error('请输入登录邮箱'))
      } else {
        callback()
      }
    }
    const validatePassword = (rule, value, callback) => {
      if (value.length === 0) {
        callback(new Error('请输入密码'))
      } else {
        callback()
      }
    }
    return {
      loginForm: {
        username: '',
        password: ''
      },
      loginRules: {
        username: [{ required: true, trigger: 'blur', validator: validateUsername }],
        password: [{ required: true, trigger: 'blur', validator: validatePassword }]
      },
      loading: false,
      passwordType: 'password',
      redirect: undefined
    }
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
    }
  },
  created() {
    removeToken()
  },
  methods: {
    onReg: function() {
      this.$router.push({ path: `/isvReg` })
    },
    showPwd() {
      if (this.passwordType === 'password') {
        this.passwordType = ''
      } else {
        this.passwordType = 'password'
      }
      this.$nextTick(() => {
        this.$refs.password.focus()
      })
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.doSubmit()
        }
      })
    },
    onCaptchaSuccess(params) {
      this.doSubmit(function(data) {
        data.captcha = params
      })
    },
    doSubmit: function(callback) {
      const data = this.loginForm
      let pwd = data.password
      pwd = md5(pwd)
      const postData = {
        username: data.username,
        password: pwd
      }
      callback && callback.call(this, postData)
      this.post('/portal/common/login', postData, function(resp) {
        const data = resp.data
        this.setUserType('2')
        setToken(data.token)
        this.goRoute(this.redirect || '/dashboard')
      })
    },
    useVerify() {
      this.$refs.verify.show()
    }
  }
}
</script>

