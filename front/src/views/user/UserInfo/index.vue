<template>
  <div class="app-container">
    <el-form
      ref="userForm"
      :model="userData"
      :rules="rules"
      label-width="120px"
      style="width: 500px;"
    >
      <el-form-item :label="$ts('loginAccount')">
        {{ userData.username }}
      </el-form-item>
      <el-form-item :label="$ts('dingdingAccount')">
        <span v-if="!userData.dingdingUserId">
          <el-popover
            placement="right"
            v-model="bindDingDingShow"
          >
            <el-alert :title="$ts('bindDingDingTip')" :closable="false" />
            <ding-ding-login :on-login-success="bindDingDingSuccess" />
            <div style="text-align: right; margin: 0">
              <el-button size="mini" type="text" @click="bindDingDingShow = false">取消</el-button>
            </div>
            <el-button slot="reference" type="primary" size="mini">{{ $ts('bindAccount') }}</el-button>
          </el-popover>
        </span>
        <span v-else>
          {{ userData.dingdingNick }}
        </span>
      </el-form-item>
      <el-form-item :label="$ts('nickname')" prop="nickname">
        <el-input v-model="userData.nickname" show-word-limit maxlength="50" />
      </el-form-item>
      <el-form-item :label="$ts('email')" prop="email">
        <el-input v-model="userData.email" show-word-limit maxlength="100" />
      </el-form-item>
      <el-form-item :label="$ts('regTime')">
        {{ userData.gmtCreate }}
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSave">{{ $ts('dlgSave') }}</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
$addI18n({
  'bindDingDingTip': { 'zh': '使用钉钉App扫一扫进行账号绑定', 'en': 'Use DingDing app scan the QR to bind account' }
})
import DingDingLogin from '@/components/DingDingLogin'
export default {
  name: 'UserInfo',
  components: { DingDingLogin },
  props: {
    userInfo: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      bindDingDingShow: false,
      userData: {
        username: '',
        dingdingUserId: '',
        nickname: '',
        email: ''
      },
      rules: {
        nickname: [
          { required: true, message: $ts('notEmpty'), trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    userInfo(val) {
      Object.assign(this.userData, val)
    }
  },
  created() {
    // this.loadUserInfo()
  },
  methods: {
    bindDingDingSuccess(data) {
      const errorMessage = data.errorMessage
      if (errorMessage) {
        this.tipError(`绑定失败:${errorMessage}`)
      } else {
        this.tipSuccess('绑定成功')
        this.bindDingDingShow = false
        this.loadUserInfo()
      }
    },
    loadUserInfo() {
      this.get('/user/get', {}, resp => {
        this.userData = resp.data
      })
    },
    onSave() {
      this.$refs.userForm.validate(valid => {
        if (valid) {
          this.post('/user/update', this.userData, () => {
            this.tipSuccess($ts('saveSuccess'))
          })
        }
      })
    }
  }
}
</script>
