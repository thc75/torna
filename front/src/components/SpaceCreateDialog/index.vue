<template>
  <el-dialog
    title="创建空间"
    :close-on-click-modal="false"
    :visible.sync="visible"
    width="40%"
    @close="onHide"
  >
    <el-form
      ref="spaceForm"
      :model="spaceFormData"
      :rules="spaceRule"
      size="mini"
      label-width="100px"
    >
      <el-form-item label="空间名称" prop="name">
        <el-input
          v-model="spaceFormData.name"
          show-word-limit
          maxlength="50"
        />
      </el-form-item>
      <el-form-item v-if="isSuperAdmin()" label="空间管理员" required>
        <user-select ref="userSelect" multiple />
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取 消</el-button>
      <el-button type="primary" @click="onSpaceCreateSave">保 存</el-button>
    </div>
  </el-dialog>
</template>
<script>
import UserSelect from '@/components/UserSelect'
export default {
  name: 'SpaceCreateDialog',
  components: { UserSelect },
  props: {
    success: {
      type: Function,
      default: () => {}
    }
  },
  data() {
    return {
      visible: false,
      spaceFormData: {
        name: '',
        adminId: ''
      },
      spaceRule: {
        name: [
          { required: true, message: '不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    onSpaceCreateSave() {
      const promise = this.fireUserSelect('validate') || []
      const promiseMain = this.$refs.spaceForm.validate()
      const allPromise = promise ? [promise, promiseMain] : [promiseMain]
      Promise.all(allPromise).then(validArr => {
        // 到这里来表示全部内容校验通过
        this.spaceFormData.adminIds = this.fireUserSelect('getValue') || []
        this.post('/space/add', this.spaceFormData, resp => {
          this.visible = false
          this.tipSuccess('创建成功')
          this.success(resp.data)
        })
      }).catch((e) => {
        console.error(e)
      }) // 加上这个控制台不会报Uncaught (in promise)
    },
    show() {
      this.visible = true
    },
    fireUserSelect(method) {
      const userSelect = this.$refs.userSelect
      return userSelect && userSelect[method]()
    },
    onHide() {
      this.resetForm('spaceForm')
      this.fireUserSelect('resetForm')
    }
  }
}
</script>
