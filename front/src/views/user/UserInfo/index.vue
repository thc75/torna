<template>
  <div class="app-container">
    <el-form
      ref="userForm"
      :model="userInfo"
      :rules="rules"
      label-width="120px"
      style="width: 500px;"
    >
      <el-form-item :label="$t('loginAccount')">
        {{ userInfo.username }}
      </el-form-item>
      <el-form-item :label="$t('nickname')" prop="nickname">
        <el-input v-model="userInfo.nickname" show-word-limit maxlength="50" />
      </el-form-item>
      <el-form-item :label="$t('email')" prop="email">
        <el-input v-model="userInfo.email" show-word-limit maxlength="100" />
      </el-form-item>
      <el-form-item :label="$t('regTime')">
        {{ userInfo.gmtCreate }}
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSave">{{ $t('dlgSave') }}</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
export default {
  name: 'UserInfo',
  props: {
    userInfo: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      rules: {
        nickname: [
          { required: true, message: $t('notEmpty'), trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    // this.loadUserInfo()
  },
  methods: {
    loadUserInfo() {
      this.get('/user/get', {}, resp => {
        this.userInfo = resp.data
      })
    },
    onSave() {
      this.$refs.userForm.validate(valid => {
        if (valid) {
          this.post('/user/update', this.userInfo, () => {
            this.tipSuccess($t('saveSuccess'))
          })
        }
      })
    }
  }
}
</script>
