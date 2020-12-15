<template>
  <div>
    <el-form ref="form" :model="form" class="text-form" label-width="100px">
      <el-form-item label="项目名称">
        {{ form.name }}
      </el-form-item>
      <el-form-item label="项目描述">
        {{ form.description }}
      </el-form-item>
      <el-form-item label="项目管理员">
        {{ form.admin }}
      </el-form-item>
      <el-form-item label="访问权限">
        {{ form.isPrivate === 1 ? '私有' : '公开' }}
      </el-form-item>
      <el-form-item label="创建人">
        {{ form.creatorName }}
      </el-form-item>
      <el-form-item label="创建时间">
        {{ form.gmtCreate }}
      </el-form-item>
      <el-form-item v-if="hasRole(`project:${projectId}`, [Role.admin])">
        <el-button type="primary" size="mini" @click="onProjectUpdate">修改项目</el-button>
        <el-button type="danger" size="mini" @click="onProjectDel">删除项目</el-button>
      </el-form-item>
    </el-form>
    <el-dialog
      title="修改项目"
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
        <el-form-item label="项目管理员" required>
          <user-select ref="userSelect" :loader="loadSpaceMember" multiple :value="projectFormData.adminIds" />
        </el-form-item>
        <el-form-item label="访问权限">
          <el-radio-group v-model="projectFormData.isPrivate">
            <el-radio class="el-icon-lock" :label="1">私有</el-radio>
            <el-radio :label="0">公开</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="projectDlgShow = false">取 消</el-button>
        <el-button type="primary" @click="onProjectUpdateSave">保 存</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import UserSelect from '@/components/UserSelect'

export default {
  name: 'ProjectInfo',
  components: { UserSelect },
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
          { required: true, message: '不能为空', trigger: 'blur' }
        ],
        spaceId: [
          { required: true, message: '不能为空', trigger: 'blur' }
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
    onProjectDel() {
      this.confirm('确认要删除该项目吗？', () => {
        this.post('/project/delete', { id: this.projectId }, () => {
          location.reload()
        })
      })
    },
    onProjectUpdate() {
      this.projectDlgShow = true
      Object.assign(this.projectFormData, this.form)
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
            this.tipSuccess('修改成功')
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
