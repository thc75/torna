<template>
  <el-dialog
    :title="$ts('createProject')"
    :close-on-click-modal="false"
    :visible.sync="visible"
    @close="onHide"
  >
    <el-form
      ref="projectForm"
      :model="projectFormData"
      :rules="projectRule"
      size="mini"
      label-width="150px"
      style="width: 600px;"
    >
      <el-form-item :label="$ts('projectName')" prop="name">
        <el-input
          v-model="projectFormData.name"
          show-word-limit
          maxlength="50"
        />
      </el-form-item>
      <el-form-item :label="$ts('projectDesc')" prop="description">
        <el-input
          v-model="projectFormData.description"
          type="textarea"
          show-word-limit
          maxlength="100"
        />
      </el-form-item>
      <el-form-item :label="$ts('visitPermission')">
        <el-radio-group v-model="projectFormData.isPrivate">
          <el-radio class="el-icon-lock" :label="1">{{ $ts('private') }}</el-radio>
          <el-radio :label="0">{{ $ts('public') }}</el-radio>
        </el-radio-group>
        <question-private />
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="visible = false">{{ $ts('dlgCancel') }}</el-button>
      <el-button type="primary" @click="onProjectCreateSave">{{ $ts('dlgSave') }}</el-button>
    </div>
  </el-dialog>
</template>
<script>
import QuestionPrivate from '@/components/QuestionPrivate'
export default {
  name: 'ProjectCreateDialog',
  components: { QuestionPrivate },
  props: {
    success: {
      type: Function,
      default: () => {}
    }
  },
  data() {
    return {
      visible: false,
      projectFormData: {
        name: '',
        description: '',
        spaceId: '',
        isPrivate: 1
      },
      projectRule: {
        name: [
          { required: true, message: this.$ts('notEmpty'), trigger: 'blur' }
        ],
        spaceId: [
          { required: true, message: this.$ts('notEmpty'), trigger: 'blur' }
        ]
      },
      spaceData: []
    }
  },
  methods: {
    onProjectCreateSave() {
      const promiseMain = this.$refs.projectForm.validate()
      Promise.all([promiseMain]).then(validArr => {
        // 到这里来表示全部内容校验通过
        this.post('/project/add', this.projectFormData, resp => {
          this.visible = false
          this.tipSuccess(this.$ts('addSuccess'))
          this.initPerm()
          this.success(resp.data)
        })
      }).catch((e) => {
      }) // 加上这个控制台不会报Uncaught (in promise)
    },
    show(spaceId) {
      if (!spaceId) {
        this.tipError('show()必须传spaceId参数')
        return false
      }
      this.projectFormData.spaceId = spaceId
      this.visible = true
    },
    onHide() {
      this.resetForm('projectForm')
    }
  }
}
</script>
