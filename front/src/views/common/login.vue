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
        <h3 class="title">{{ $ts('userLogin') }}</h3>
      </div>
      <el-tabs v-show="showLoginTab" v-model="loginForm.source" type="card">
        <el-tab-pane :label="$ts('accountLogin')" name="register"></el-tab-pane>
        <el-tab-pane v-if="serverConfig.enableThirdPartyForm" :label="$ts('thirdpartyLogin')" name="form"></el-tab-pane>
        <el-tab-pane v-if="serverConfig.enableLdap" :label="$ts('ldapLogin')" name="ldap"></el-tab-pane>
      </el-tabs>
      <el-form-item prop="username">
        <el-input
          v-model="loginForm.username"
          :placeholder="$ts('loginAccount')"
          prefix-icon="el-icon-message"
          auto-complete="on"
        />
      </el-form-item>
      <el-form-item prop="password">
        <el-input
          v-model="loginForm.password"
          type="password"
          :placeholder="$ts('password')"
          prefix-icon="el-icon-lock"
          auto-complete="on"
        />
      </el-form-item>
      <el-button :loading="loading" type="primary" style="width: 100%;" native-type="submit" @click="handleLogin">{{ $ts('loginSubmit') }}</el-button>
      <div class="footer">
        <div v-if="serverConfig.enableReg">
          <el-link type="primary" :underline="false" @click="onReg">{{ $ts('signUp') }}</el-link>
          <span class="split">|</span>
          <el-link type="primary" :underline="false" @click="onForgetPwd">{{ $ts('forgetPwd') }}？</el-link>
        </div>
        <el-link
          v-if="serverConfig.enableThirdPartyOauth"
          type="primary"
          :underline="false"
          :href="serverConfig.oauthLoginUrl"
        >
          {{ serverConfig.oauthButtonText }}
        </el-link>
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
        callback(new Error($ts('plzInputLoginAccount')))
      } else {
        callback()
      }
    }
    const validatePassword = (rule, value, callback) => {
      if (value.length === 0) {
        callback(new Error($ts('plzInputPassword')))
      } else {
        callback()
      }
    }
    return {
      serverConfig: {
        enableReg: false,
        enableThirdPartyForm: false,
        enableThirdPartyOauth: false,
        enableLdap: false,
        oauthLoginUrl: '',
        oauthButtonText: ''
      },
      loginForm: {
        username: '',
        password: '',
        source: 'register'
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
  computed: {
    showLoginTab() {
      return this.serverConfig.enableThirdPartyForm || this.serverConfig.enableLdap
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
    this.pmsConfig().then(config => {
      Object.assign(this.serverConfig, config)
    })
  },
  methods: {
    onReg: function() {
      this.$router.push({ path: `/reg` })
    },
    onForgetPwd() {
      this.alert($ts('askSuperAdminRestPwd'), $ts('forgetPwd'))
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
      if (this.loginForm.source === this.getEnums().SOURCE.REGISTER) {
        pwd = md5(pwd)
      }
      const postData = {}
      Object.assign(postData, data)
      postData.password = pwd
      callback && callback.call(this, postData)
      this.loading = true
      this.post('/system/login', postData, function(resp) {
        this.loading = false
        const data = resp.data
        setToken(data.token)
        if (data.status === Enums.USER_STATUS.SET_PASSWORD) {
          this.goSetPassword()
        } else {
          this.goRoute(this.redirect || '/dashboard')
        }
      }, () => {
        this.loading = false
      })
    },
    useVerify() {
      this.$refs.verify.show()
    }
  }
}
</script>

