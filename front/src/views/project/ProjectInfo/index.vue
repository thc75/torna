<template>
  <div class="app-container">
    <el-form ref="form" :model="form" class="text-form" label-width="150px">
      <el-form-item :label="$ts('projectName')">
        {{ form.name }}
      </el-form-item>
      <el-form-item :label="$ts('projectDesc')">
        {{ form.description }}
      </el-form-item>
      <el-form-item :label="$ts('projectAdmin')">
        {{ form.admin }}
      </el-form-item>
      <el-form-item :label="$ts('visitPermission')">
        {{ form.isPrivate === 1 ? $ts('private') : $ts('public') }}
      </el-form-item>
      <el-form-item :label="$ts('creator')">
        {{ form.creatorName }}
      </el-form-item>
      <el-form-item :label="$ts('createTime')">
        {{ form.gmtCreate }}
      </el-form-item>
      <el-form-item v-if="hasRole(`project:${projectId}`, [Role.admin])">
        <el-button type="primary" size="mini" @click="onProjectUpdate">{{ $ts('updateProject') }}</el-button>
        <el-button type="danger" size="mini" @click="onProjectDel">{{ $ts('deleteProject') }}</el-button>
      </el-form-item>
    </el-form>
    <el-dialog
      :title="$ts('updateProject')"
      :close-on-click-modal="false"
      :visible.sync="projectDlgShow"
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
        <el-form-item :label="$ts('projectAdmin')" required>
          <user-select-v2 ref="userSelect" multiple :value="projectFormData.adminIds" />
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
        <el-button @click="projectDlgShow = false">{{ $ts('dlgCancel') }}</el-button>
        <el-button type="primary" @click="onProjectUpdateSave">{{ $ts('dlgSave') }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import UserSelectV2 from '@/components/UserSelectV2'
import QuestionPrivate from '@/components/QuestionPrivate'

export default {
  name: 'ProjectInfo',
  components: { UserSelectV2, QuestionPrivate },
  props: {
    projectId: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      form: {
        id: '',
        name: '',
        adminIds: [],
        admin: '',
        spaceId: '',
        description: '',
        isPrivate: 0,
        creator: '',
        gmtCreate: ''
      },
      projectDlgShow: false,
      projectFormData: {
        name: '',
        description: '',
        adminIds: [],
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
  watch: {
    projectId(projectId) {
      this.loadInfo(projectId)
    }
  },
  methods: {
    loadSelectUser() {
      this.loadSpaceMember().then(data => {
        this.spaceUsers = data
      })
    },
    onProjectDel() {
      this.confirm(this.$ts('deleteProjectConfirm'), () => {
        this.post('/project/delete', { id: this.projectId }, () => {
          this.tipSuccess(this.$ts('deleteSuccess'))
          this.goRoute(`/space/project/${this.form.spaceId}`)
        })
      })
    },
    onProjectUpdate() {
      this.projectDlgShow = true
      Object.assign(this.projectFormData, this.form)
      this.$nextTick(() => {
        this.loadSpaceMember().then(data => {
          this.$refs.userSelect.setData(data)
          this.$refs.userSelect.setValue(this.projectFormData.adminIds)
        })
      })
    },
    onProjectUpdateSave() {
      const promise = this.$refs.userSelect.validate()
      const promiseMain = this.$refs.projectForm.validate()
      Promise.all([promise, promiseMain]).then(validArr => {
        // 到这里来表示全部内容校验通过
        this.projectFormData.adminIds = this.$refs.userSelect.getValue()
        this.post('/project/update', this.projectFormData, resp => {
          this.projectDlgShow = false
          // 如果空间变了，需要刷新页面
          if (this.projectFormData.spaceId !== this.form.spaceId) {
            location.reload()
          } else {
            this.tipSuccess(this.$ts('updateSuccess'))
            this.loadInfo(this.projectId)
          }
        })
      }).catch((e) => {
      }) // 加上这个控制台不会报Uncaught (in promise)
    },
    loadInfo(projectId) {
      if (projectId) {
        this.get('/project/info', { projectId: projectId }, resp => {
          const data = resp.data
          data.admin = data.admins.map(userInfo => {
            return userInfo.nickname
          }).join(' / ')
          data.adminIds = data.admins.map(userInfo => {
            return userInfo.id
          })
          this.form = data
        })
      }
    }
  }
}
</script>
