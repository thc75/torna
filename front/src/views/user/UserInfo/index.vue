<template>
  <div class="app-container">
    <el-form
      ref="userForm"
      :model="userData"
      :rules="rules"
      label-width="130px"
      style="width: 500px;"
    >
      <el-form-item :label="$t('loginAccount')">
        {{ userData.username }}
      </el-form-item>
      <el-form-item :label="$t('dingdingAccount')">
        <span v-if="!userData.dingdingUserId">
          <el-popover
            placement="right"
            v-model="bindDingDingShow"
          >
            <el-alert :title="$t('UserInfo.bindDingDingTip')" :closable="false" />
            <ding-ding-login :on-login-success="bindDingDingSuccess" />
            <div style="text-align: right; margin: 0">
              <el-button size="mini" type="text" @click="bindDingDingShow = false">取消</el-button>
            </div>
            <el-button slot="reference" type="primary" size="mini">{{ $t('bindAccount') }}</el-button>
          </el-popover>
        </span>
        <span v-else>
          {{ userData.dingdingNick }}
        </span>
      </el-form-item>
      <el-form-item :label="$t('weComMobile')" prop="weComMobile">
        <el-input v-model="userData.weComMobile" show-word-limit maxlength="11" />
      </el-form-item>
      <el-form-item :label="$t('nickname')" prop="nickname">
        <el-input v-model="userData.nickname" show-word-limit maxlength="50" />
      </el-form-item>
      <el-form-item :label="$t('email')" prop="email">
        <el-input v-model="userData.email" show-word-limit maxlength="100" />
      </el-form-item>
      <el-form-item :label="$t('regTime')">
        {{ userData.gmtCreate }}
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSave">{{ $t('dlgSave') }}</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
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
        email: '',
        weComMobile: ''
      },
      rules: {
        nickname: [
          { required: true, message: $t('notEmpty'), trigger: 'blur' }
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
            this.tipSuccess($t('saveSuccess'))
          })
        }
      })
    }
  }
}
</script>
