<template>
  <div class="app-container">
    <h3>{{ $t('accountInfo') }}</h3>
    <el-tabs v-model="activeName" type="card">
      <el-tab-pane :label="$t('baseInfo')" name="1">
        <user-info :user-info="userInfo" />
      </el-tab-pane>
      <el-tab-pane v-if="!isThirdPartyUser()" :label="$t('updatePassword')" name="2">
        <update-password />
      </el-tab-pane>
      <el-tab-pane v-if="enableMfaAuth()" :label="$t('mfa.settingTitle')" name="3">
        <el-button type="primary" :loading="resetMFALoading" @click="resetMFA()">{{ $t('mfa.resetTitle') }}</el-button>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script>
import UserInfo from './UserInfo'
import UpdatePassword from './UpdatePassword'
import { enable_mfa_auth, is_third_party_user } from '@/utils/user'
import { removeToken } from '@/utils/auth'
export default {
  components: { UserInfo, UpdatePassword },
  data() {
    return {
      activeName: '1',
      userInfo: {},
      resetMFALoading: false
    }
  },
  created() {
    this.get('/user/get', {}, resp => {
      this.userInfo = resp.data
    })
  },
  methods: {
    isThirdPartyUser() {
      return is_third_party_user(this.userInfo)
    },
    enableMfaAuth() {
      return enable_mfa_auth(this.userInfo)
    },
    resetMFA() {
      this.confirm(
        this.$t('mfa.resetConfirm'),
        () => {
          this.resetMFALoading = true
          this.post('/user/mfa/reset', {}, (resp) => {
            if (resp && resp.code === '0') {
              this.resetMFALoading = false
              removeToken()
              this.goMFABind(this.userInfo.username)
            }
          }, () => {
            this.resetMFALoading = false
          })
        }
      )
    }
  }
}
</script>
