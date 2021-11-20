<template>
  <div class="login-page">
    <logo no-style :collapse="false" />
    <el-form
      v-show="!submited"
      ref="regForm"
      :model="regForm"
      :rules="regRules"
      class="center-form"
      style="margin: 60px auto;"
      @submit.native.prevent
    >
      <h3>{{ $ts('signUp') }}</h3>
      <el-form-item prop="username">
        <el-input
          v-model="regForm.username"
          :placeholder="$ts('loginAccount')"
          prefix-icon="el-icon-message"
          maxlength="100"
          show-word-limit
        />
      </el-form-item>
      <el-form-item prop="nickname">
        <el-input
          v-model="regForm.nickname"
          :placeholder="$ts('nickname')"
          prefix-icon="el-icon-user"
          maxlength="100"
          show-word-limit
        />
      </el-form-item>
      <el-form-item prop="password">
        <el-input
          v-model="regForm.password"
          type="password"
          :placeholder="$ts('password')"
          prefix-icon="el-icon-lock"
        />
      </el-form-item>
      <el-form-item prop="password2">
        <el-input
          v-model="regForm.password2"
          type="password"
          :placeholder="$ts('passwordConfirm')"
          prefix-icon="el-icon-lock"
        />
      </el-form-item>
      <el-button type="primary" style="width: 100%;" native-type="submit" @click="handleReg">{{ $ts('signUp') }}</el-button>
      <div class="footer">
        <el-button type="text" @click="goLogin">{{ $ts('goLogin') }}</el-button>
      </div>
    </el-form>
  </div>
</template>
<script>
import md5 from 'js-md5'
import Logo from '@/components/Logo'

export default {
  name: 'Reg',
  components: { Logo },
  data() {
    const validatePassword2 = (rule, value, callback) => {
      if (value !== this.regForm.password) {
        callback(new Error('两次密码不一致'))
      } else {
        callback()
      }
    }
    return {
      serverConfig: {
        enableReg: false
      },
      query: {},
      submited: false,
      emailUrl: '',
      regForm: {
        username: '',
        password: '',
        password2: '',
        nickname: ''
      },
      regRules: {
        username: [
          { required: true, message: '请填写邮箱地址', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur'] }
        ],
        nickname: [
          { required: true, message: '请填写昵称', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
        ],
        password2: [{ required: true, trigger: 'blur', validator: validatePassword2 }]
      },
      loading: false,
      passwordType: 'password',
      password2Type: 'password',
      regTitle: '接入方注册'
    }
  },
  created() {
    this.pmsConfig().then(config => {
      Object.assign(this.serverConfig, config)
    })
  },
  methods: {
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
    showPwd2() {
      if (this.password2Type === 'password') {
        this.password2Type = ''
      } else {
        this.password2Type = 'password'
      }
      this.$nextTick(() => {
        this.$refs.password2.focus()
      })
    },
    goLogin: function() {
      this.goRoute('/login')
    },
    handleReg() {
      if (!this.serverConfig.enableReg) {
        alert('不允许注册')
        return false
      }
      this.$refs.regForm.validate(valid => {
        if (valid) {
          this.doSubmit()
        }
      })
    },
    onCaptchaSuccess(params) {
      this.doSubmit(data => {
        data.captcha = params
      })
    },
    doSubmit: function(callback) {
      const data = {}
      Object.assign(data, this.regForm)
      data.password = md5(data.password)
      callback && callback.call(this, data)
      this.post('/system/reg', data, function(resp) {
        // 验证邮箱
        if (data.needVerifyEmail) {
          this.submited = true
        } else {
          this.alert('注册成功', '提示', function() {
            this.goRoute(`/login`)
          })
        }
      })
    }
  }
}
</script>

<style lang="scss">
  /* 修复input 背景不协调 和光标变色 */
  /* Detail see https://github.com/PanJiaChen/vue-element-admin/pull/927 */

  $bg:#283443;
  $light_gray:#fff;
  $cursor: #000;

  @supports (-webkit-mask: none) and (not (cater-color: $cursor)) {
    .login-container .el-input input {
      color: $cursor;
    }
  }

  /* reset element-ui css */
  .login-container {
    .el-input {
      display: inline-block;
      height: 47px;
      width: 85%;

      input {
        background: transparent;
        border: 0px;
        -webkit-appearance: none;
        border-radius: 0px;
        padding: 12px 5px 12px 15px;
        color: #000;
        height: 47px;
        caret-color: $cursor;

        &:-webkit-autofill {
          box-shadow: 0 0 0px 1000px #fff inset !important;
          -webkit-text-fill-color: $cursor !important;
        }
      }
    }

    .el-form-item {
      border: 1px solid #d3dce6;
      border-radius: 5px;
      color: #454545;
    }
  }
</style>

<style lang="scss" scoped>
  $bg:#2d3a4b;
  $dark_gray:#889aa4;
  $light_gray:#eee;

  .login-container {
    min-height: 100%;
    width: 100%;
    overflow: hidden;

    .login-form {
      position: relative;
      width: 520px;
      max-width: 100%;
      padding: 60px 35px 0;
      margin: 0 auto;
      overflow: hidden;
    }

    .tips {
      font-size: 14px;
      color: #fff;
      margin-bottom: 10px;

      span {
        &:first-of-type {
          margin-right: 16px;
        }
      }
    }

    .svg-container {
      padding: 6px 5px 6px 15px;
      color: $dark_gray;
      vertical-align: middle;
      width: 30px;
      display: inline-block;
    }

    .title-container {
      position: relative;

      .title {
        font-size: 26px;
        margin: 0px auto 40px auto;
        text-align: center;
        font-weight: bold;
      }
    }

    .show-pwd {
      position: absolute;
      right: 10px;
      top: 7px;
      font-size: 16px;
      color: $dark_gray;
      cursor: pointer;
      user-select: none;
    }
  }
</style>
