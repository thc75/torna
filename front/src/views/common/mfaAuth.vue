<template>
  <div class="app-container">
    <logo no-style :collapse="false" />
    <el-form
      ref="mfaAuthForm"
      :model="mfaAuthData"
      :rules="resetRules"
      class="center-form"
      @submit.native.prevent
    >
      <h3>{{ $t('mfa.authTitle') }}</h3>
      <el-form-item prop="totpCode">
        <el-input
          v-model="mfaAuthData.totpCode"
          type="text"
          :placeholder="$t('mfa.totpPlaceholder')"
          @input="handleTotpInput"
        />
      </el-form-item>
      <el-form-item>
        <el-button
          type="primary"
          :loading="verifyLoading"
          style="width: 100%"
          native-type="submit"
          @click="onVerify"
        >
          {{ $t('mfa.verifyButton') }}
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import { setToken } from '@/utils/auth'
import { Enums } from '@/utils/enums'
import Logo from '@/components/Logo'

export default {
  components: { Logo },
  data() {
    return {
      verifyLoading: false,
      mfaAuthData: {
        totpCode: ''
      },
      resetRules: {
        totpCode: [
          { required: true, message: this.$t('mfa.totpPlaceholder'), trigger: 'blur' }
        ]
      },
      redirect: undefined
    }
  },
  computed: {
    ...mapState('user', ['username'])
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
    }
  },
  methods: {
    handleEnter(event) {
      if (event.keyCode === 13) {
        event.preventDefault()
        this.onVerify()
      }
    },
    handleTotpInput(value) {
      this.mfaAuthData.totpCode = value.replace(/[^\d]/g, '').slice(0, 6)
    },
    // 验证
    onVerify() {
      this.$refs.mfaAuthForm.validate(valid => {
        if (valid) {
          this.verifyLoading = true
          this.post('/mfa/verify', {
            username: this.username,
            totpCode: this.mfaAuthData.totpCode
          }, function(resp) {
            this.verifyLoading = false
            if (resp.data && resp.data.checkStatus) {
              this.$store.commit('user/SET_USERNAME', null)
              const data = resp.data
              if (data.loginResult) {
                const loginData = data.loginResult
                setToken(loginData.token)
                if (loginData.status === Enums.USER_STATUS.SET_PASSWORD) {
                  this.goSetPassword()
                } else {
                  this.goRoute(this.redirect || '/dashboard')
                }
              }
            } else {
              if (!resp.msg) {
                this.$message.error(this.$t('mfa.authFailed'))
              }
            }
          }, (error) => {
            this.verifyLoading = false
            // 检查是否已经有错误消息显示
            if (!error.response || !error.response.data.msg) {
              this.$message.error(this.$t('mfa.authFailed'))
            }
          })
        }
      })
    }
  }
}
</script>
