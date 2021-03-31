<template>
  <div class="login-page">
    <logo no-style :collapse="false" />
    <el-form
      ref="loginForm"
      :model="loginForm"
      :rules="loginRules"
      class="center-form"
      auto-complete="on"
      @submit.native.prevent
    >
      <div class="title-container">
        <h3 class="title">用户登录</h3>
      </div>
      <el-form-item prop="username">
        <el-input
          v-model="loginForm.username"
          placeholder="登录邮箱"
          prefix-icon="el-icon-message"
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
        <el-link v-if="serverConfig.enableReg" type="primary" :underline="false" @click="onReg">注册新账号</el-link>
        <span v-if="serverConfig.enableReg" class="split">|</span>
        <el-link type="primary" :underline="false" @click="onForgetPwd">忘记密码？</el-link>
      </div>
    </el-form>
  </div>
</template>
<script>
import md5 from 'js-md5'
import { Enums } from '@/utils/enums'
import { setToken, removeToken } from '@/utils/auth'
import Logo from '@/components/Logo'

export default {
  name: 'Login',
  components: { Logo },
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
      serverConfig: {
        enableReg: false
      },
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
    this.getServerConfig(config => {
      Object.assign(this.serverConfig, config)
    })
  },
  methods: {
    onReg: function() {
      this.$router.push({ path: `/reg` })
    },
    onForgetPwd() {
      this.alert('询问超级管理员重置密码', '忘记密码')
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
      this.post('/system/login', postData, function(resp) {
        const data = resp.data
        setToken(data.token)
        if (data.status === Enums.USER_STATUS.SET_PASSWORD) {
          this.goSetPassword()
        } else {
          this.goRoute(this.redirect || '/dashboard')
        }
      })
    },
    useVerify() {
      this.$refs.verify.show()
    }
  }
}
</script>

