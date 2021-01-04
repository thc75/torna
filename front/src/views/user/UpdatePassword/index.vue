<template>
  <div class="app-container">
    <el-form
      ref="updatePwdForm"
      :model="updatePwdData"
      :rules="updatePwdRules"
      label-width="120px"
      style="width: 500px;"
    >
      <el-form-item label="旧密码" prop="oldPassword">
        <el-input
          v-model="updatePwdData.oldPassword"
          type="password"
          placeholder="旧密码"
        />
      </el-form-item>
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
      </el-form-item>
    </el-form>
    </div>
</template>
<script>

import md5 from 'js-md5'

export default {
  name: 'UpdatePassword',
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
        oldPassword: '',
        password: '',
        password2: ''
      },
      updatePwdRules: {
        oldPassword: [
          { required: true, message: '请输入旧密码', trigger: 'blur' }
        ],
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
          data.oldPassword = md5(data.oldPassword)
          data.password = md5(data.password)
          this.post('/user/password/update', data, function(resp) {
            alert('修改成功，请重新登录')
            this.logout('/')
          })
        }
      })
    }
  }
}
</script>
