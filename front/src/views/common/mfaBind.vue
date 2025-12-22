<template>
  <div class="app-container">
    <el-form
      ref="mfaBindForm"
      :model="mfaBindData"
      :rules="bindRules"
      class="center-form"
      @submit.native.prevent
    >
      <h3 class="form-title">{{ $t('mfa.bindTitle') }}</h3>

      <div class="username-section">
        <div class="step-title">{{ $t('mfa.loginUser') }}：<span class="username-text">{{ username }}</span></div>
      </div>

      <!-- 二维码区域 -->
      <div class="qrcode-section">
        <div class="step-title">{{ $t('mfa.step1Title') }}</div>
        <div v-loading="qrLoading" class="qrcode-container">
          <img
            v-if="qrCode"
            :src="qrCode"
            alt="MFA QR Code"
            class="qrcode-image"
          >
          <div v-else class="qrcode-placeholder">
            {{ qrLoading ? $t('mfa.qrCodeLoading') : $t('mfa.qrCodeError') }}
          </div>
        </div>
      </div>

      <!-- 验证码输入 -->
      <div class="verify-section">
        <div class="step-title">{{ $t('mfa.step2Title') }}</div>
        <el-form-item prop="totpCode">
          <el-input
            v-model="mfaBindData.totpCode"
            type="text"
            maxlength="6"
            :placeholder="$t('mfa.totpPlaceholder')"
            @input="handleTotpInput"
          />
        </el-form-item>
      </div>

      <!-- 操作按钮 -->
      <el-form-item class="action-buttons">
        <el-form-item>
          <el-button
            type="primary"
            :loading="bindLoading"
            style="width: 100%"
            native-type="submit"
            @click="onVerify"
          >
            {{ $t('mfa.submitButton') }}
          </el-button>
        </el-form-item>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import { setToken } from '@/utils/auth'
import { Enums } from '@/utils/enums'

export default {
  data() {
    return {
      qrCode: null,
      qrLoading: false,
      bindLoading: false,
      mfaBindData: {
        totpCode: ''
      },
      bindRules: {
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
  mounted() {
    console.log('username from user store:', this.username)
    this.getQRCode()
  },
  methods: {
    // 获取二维码
    getQRCode() {
      try {
        this.qrLoading = true
        this.post('/mfa/getQRCode', {
          username: this.username
        }, function(resp) {
          const data = resp.data
          if (data && data.qrCode) {
            this.qrCode = data.qrCode.startsWith('data:image') ? data.qrCode : `data:image/png;base64,${data.qrCode}`
          }
        })
      } catch (error) {
        this.$message.error(this.$t('mfa.bindFailed'))
        console.error('get qrcode error:', error)
      } finally {
        this.qrLoading = false
      }
    },

    // 输入过滤
    handleTotpInput(value) {
      this.mfaBindData.totpCode = value.replace(/[^\d]/g, '').slice(0, 6)
    },

    // 验证
    onVerify() {
      this.$refs.mfaBindForm.validate(valid => {
        if (valid) {
          this.bindLoading = true
          this.post('/mfa/verify', {
            username: this.username,
            totpCode: this.mfaBindData.totpCode
          }, function(resp) {
            this.bindLoading = false
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
              this.$message.success(this.$t('mfa.bindSuccess'))
            } else {
              if (!resp.msg) {
                this.$message.error(this.$t('mfa.bindFailed'))
              }
            }
          }, (error) => {
            this.bindLoading = false
            // 检查是否已经有错误消息显示
            if (!error.response || !error.response.data.msg) {
              this.$message.error(this.$t('mfa.bindFailed'))
            }
            this.goRoute('/login')
          })
        }
      })
    }
  }
}
</script>

<style scoped>
.form-title {
  text-align: center;
  margin-bottom: 30px;
}

.username-section,
.qrcode-section,
.verify-section {
  margin: 24px 0;
}

.username-text {
  font-weight: bold;
}

.step-title {
  font-size: 14px;
  color: #606266;
  margin-bottom: 12px;
}

.qrcode-container {
  margin: 0 auto;
  text-align: center;
  min-height: 240px;
}

.qrcode-image {
  width: 400px;
  height: 400px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
}

.qrcode-placeholder {
  width: 200px;
  height: 200px;
  line-height: 200px;
  margin: 0 auto;
  background: #f5f7fa;
  color: #909399;
  border-radius: 4px;
}
</style>
