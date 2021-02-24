<template>
  <div class="login-page">
    <div class="pwd-info">
      <el-form
        ref="updatePwdForm"
        :model="updatePwdData"
        :rules="updatePwdRules"
        label-width="120px"
        class="center-form"
        style="width: 500px;"
      >
        <el-alert title="首次登录需要修改初始密码" effect="dark" :closable="false" style="margin-bottom: 20px" />
        <el-form-item label="新密码" prop="password">
          <el-input
            v-model="updatePwdData.password"
            type="password"
            placeholder="新密码"
          />
        </el-form-item>
        <el-form-item label="确认新密码" prop="password2">
          <el-input
            v-model="updatePwdData.password2"
            type="password"
            placeholder="确认新密码"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click.native.prevent="handleUpdate">修 改</el-button>
          <el-button type="text" @click.native.prevent="goLogin('/')">返回登录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>
<script>
import md5 from 'js-md5'

export default {
  data() {
    const validatePassword2 = (rule, value, callback) => {
      if (value !== this.updatePwdData.password) {
        callback(new Error('两次密码不一致'))
      } else {
        callback()
      }
    }
    return {
      updatePwdData: {
        password: '',
        password2: ''
      },
      updatePwdRules: {
        password: [
          { required: true, message: '请输入新密码', trigger: 'blur' }
        ],
        password2: [{ required: true, trigger: 'blur', validator: validatePassword2 }]
      }
    }
  },
  methods: {
    handleUpdate() {
      this.$refs.updatePwdForm.validate(valid => {
        if (valid) {
          const data = {}
          Object.assign(data, this.updatePwdData)
          data.password = md5(data.password)
          this.post('/system/password/updateByFirstLogin', data, function(resp) {
            this.goRoute('/')
          })
        }
      })
    }
  }
}
</script>
<style lang="scss" scoped>
.pwd-info table th {text-align: right;padding: 5px 10px;}
</style>
