<template>
  <el-dialog
    :title="$t('createSpace')"
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
      label-position="top"
      @submit.native.prevent="onSpaceCreateSave"
    >
      <el-form-item :label="$t('spaceName')" prop="name">
        <el-input
          v-model="spaceFormData.name"
          show-word-limit
          maxlength="50"
        />
      </el-form-item>
      <el-form-item v-if="isSuperAdmin()" :label="$t('spaceAdmin')" required>
        <user-select ref="userSelect" multiple />
      </el-form-item>
      <el-form-item :label="$t('isComposeSpace')">
        <el-switch
          v-model="spaceFormData.isCompose"
          active-text=""
          inactive-text=""
          :active-value="1"
          :inactive-value="0"
        />
        <span class="info-tip">{{ $t('composeSpaceTip') }}</span>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="visible = false">{{ $t('dlgCancel') }}</el-button>
      <el-button type="primary" native-type="submit" @click="onSpaceCreateSave">{{ $t('dlgSave') }}</el-button>
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
        adminId: '',
        isCompose: 0
      },
      spaceRule: {
        name: [
          { required: true, message: this.$t('notEmpty'), trigger: 'blur' }
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
          this.tipSuccess(this.$t('createSuccess'))
          this.initPerm()
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
