<template>
  <el-dialog
    :title="$ts('composeProject')"
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
      <el-form-item :label="$ts('visitStyle')">
        <el-radio-group v-model="projectFormData.type">
          <el-radio :label="getEnums().COMPOSE_PROJECT_TYPE.PUBLIC">{{ $ts('public') }}</el-radio>
          <el-radio :label="getEnums().COMPOSE_PROJECT_TYPE.ENCRYPT">{{ $ts('encryption') }}</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item :label="$ts('status')">
        <el-radio-group v-model="projectFormData.status">
          <el-radio :label="getEnums().STATUS.ENABLE">{{ $ts('enable') }}</el-radio>
          <el-radio :label="getEnums().STATUS.DISABLE">{{ $ts('disable') }}</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="visible = false">{{ $ts('dlgCancel') }}</el-button>
      <el-button type="primary" @click="onProjectCreateSave">{{ $ts('dlgSave') }}</el-button>
    </div>
  </el-dialog>
</template>
<script>
export default {
  name: 'ComposeProjectCreateDialog',
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
        type: 1,
        status: 1
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
      this.$refs.projectForm.validate(valid => {
        if (valid) {
          const uri = this.projectFormData.id ? '/compose/project/update' : '/compose/project/add'
          this.post(uri, this.projectFormData, resp => {
            this.visible = false
            this.tipSuccess(this.$ts('addSuccess'))
            this.initPerm()
            this.success(resp.data)
          })
        }
      })
    },
    show(spaceId) {
      if (!spaceId) {
        this.tipError('show()必须传spaceId参数')
        return false
      }
      this.projectFormData = {
        name: '',
        description: '',
        spaceId: spaceId,
        type: 1,
        status: 1
      }
      this.visible = true
    },
    updateShow(data) {
      Object.assign(this.projectFormData, data)
      this.visible = true
    },
    onHide() {
      this.resetForm('projectForm')
    }
  }
}
</script>
