<template>
  <div class="app-container">
    <h3>{{ $ts('accountInfo') }}</h3>
    <el-tabs v-model="activeName" type="card">
      <el-tab-pane :label="$ts('baseInfo')" name="1">
        <user-info :user-info="userInfo" />
      </el-tab-pane>
      <el-tab-pane v-if="!isThirdPartyUser()" :label="$ts('updatePassword')" name="2">
        <update-password />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script>
import UserInfo from './UserInfo'
import UpdatePassword from './UpdatePassword'
import { is_third_party_user } from '@/utils/user'
export default {
  components: { UserInfo, UpdatePassword },
  data() {
    return {
      activeName: '1',
      userInfo: {}
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
    }
  }
}
</script>
