<template>
  <div class="app-container">
    <el-form
      :model="userInfo"
      size="mini"
      label-width="120px"
      style="width: 500px;"
    >
      <el-form-item label="登录账号">
        {{ userInfo.username }}
      </el-form-item>
      <el-form-item label="昵称">
        {{ userInfo.nickname }}
        <el-tooltip content="修改昵称" placement="top">
          <popover-update
            title="昵称"
            :on-show="() => {return userInfo.nickname}"
            :on-save="onSaveNickname"
          />
        </el-tooltip>
      </el-form-item>
      <el-form-item label="注册时间">
        {{ userInfo.gmtCreate }}
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
import PopoverUpdate from '@/components/PopoverUpdate'
export default {
  name: 'UserInfo',
  components: { PopoverUpdate },
  data() {
    return {
      userInfo: {
        id: '',
        username: '',
        nickname: '',
        gmtCreate: ''
      }
    }
  },
  created() {
    this.loadUserInfo()
  },
  methods: {
    loadUserInfo() {
      this.get('/user/get', {}, resp => {
        this.userInfo = resp.data
      })
    },
    onSaveNickname(value, done) {
      const data = {
        nickname: value
      }
      this.post('/user/nickname/update', data, () => {
        this.tipSuccess('修改成功')
        done()
        this.loadUserInfo()
      })
    }
  }
}
</script>
