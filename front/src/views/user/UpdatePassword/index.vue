<template>
  <div class="app-container">
    <el-form
      ref="updatePwdForm"
      :model="updatePwdData"
      :rules="updatePwdRules"
      label-width="150px"
      style="width: 500px;"
    >
      <el-form-item :label="$ts('oldPassword')" prop="oldPassword">
        <el-input
          v-model="updatePwdData.oldPassword"
          type="password"
        />
      </el-form-item>
      <el-form-item :label="$ts('newPassword')" prop="password">
        <el-input
          v-model="updatePwdData.password"
          type="password"
        />
      </el-form-item>
      <el-form-item :label="$ts('newPasswordConfirm')" prop="password2">
        <el-input
          v-model="updatePwdData.password2"
          type="password"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click.native.prevent="handleUpdate">{{ $ts('dlgUpdate') }}</el-button>
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
        callback(new Error($ts('notSamePassword')))
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
          { required: true, message: $ts('notEmpty'), trigger: 'blur' }
        ],
        password: [
          { required: true, message: $ts('notEmpty'), trigger: 'blur' }
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
            alert($ts('updatePasswordSuccess'))
            this.logout('/')
          })
        }
      })
    }
  }
}
</script>
