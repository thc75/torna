<template>
  <div class="app-container">
    <div class="open-sphere-log">XX</div>
    <el-form
      ref="resetPwdForm"
      :model="resetPwdData"
      :rules="resetRules"
      class="center-form"
    >
      <h3>重置密码</h3>
      <el-form-item>
        <el-input
          v-model="resetPwdData.email"
          placeholder="邮箱"
          prefix-icon="el-icon-ext-email"
          :disabled="true"
        />
      </el-form-item>
      <el-form-item prop="password">
        <el-input
          v-model="resetPwdData.password"
          type="password"
          placeholder="新密码"
          prefix-icon="el-icon-lock"
        />
      </el-form-item>
      <el-form-item prop="password2">
        <el-input
          v-model="resetPwdData.password2"
          type="password"
          placeholder="确认新密码"
          prefix-icon="el-icon-lock"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" style="width: 100%" @click.native.prevent="onReset">确定</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>

import md5 from 'js-md5'

export default {
  data() {
    const validatePassword2 = (rule, value, callback) => {
      if (value !== this.resetPwdData.password) {
        callback(new Error('两次密码不一致'))
      } else {
        callback()
      }
    }
    return {
      resetPwdData: {
        email: '',
        password: '',
        password2: ''
      },
      resetRules: {
        password: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
        ],
        password2: [{ required: true, trigger: 'blur', validator: validatePassword2 }]
      }
    }
  },
  created() {
    const query = this.$route.query
    Object.assign(this.resetPwdData, query)
  },
  methods: {
    onReset() {
      this.$refs.resetPwdForm.validate(valid => {
        if (valid) {
          const data = {}
          Object.assign(data, this.resetPwdData)
          data.password = md5(data.password)
          this.get('nologin.user.password.reset', data, function(resp) {
            alert('密码重置成功')
            this.logout()
          }, (resp) => this.tipError(resp.msg))
        }
      })
    }
  }
}
</script>
