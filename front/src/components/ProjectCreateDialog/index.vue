<template>
  <el-dialog
    title="创建项目"
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
      <el-form-item label="项目名称" prop="name">
        <el-input
          v-model="projectFormData.name"
          show-word-limit
          maxlength="50"
        />
      </el-form-item>
      <el-form-item label="项目描述" prop="description">
        <el-input
          v-model="projectFormData.description"
          type="textarea"
          show-word-limit
          maxlength="100"
        />
      </el-form-item>
      <el-form-item label="访问权限">
        <el-radio-group v-model="projectFormData.isPrivate">
          <el-radio class="el-icon-lock" :label="1">私有</el-radio>
          <el-radio :label="0">公开</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取 消</el-button>
      <el-button type="primary" @click="onProjectCreateSave">保 存</el-button>
    </div>
  </el-dialog>
</template>
<script>
export default {
  name: 'ProjectCreateDialog',
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
          { required: true, message: '不能为空', trigger: 'blur' }
        ],
        spaceId: [
          { required: true, message: '不能为空', trigger: 'blur' }
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
          this.fireEvent('projectChange', new Date().getTime())
          this.tipSuccess('添加成功')
          this.initPerm()
        })
      }).catch((e) => {
      }) // 加上这个控制台不会报Uncaught (in promise)
    },
    show(spaceId) {
      this.projectFormData.spaceId = spaceId
      this.visible = true
    },
    onHide() {
      this.resetForm('projectForm')
    }
  }
}
</script>
