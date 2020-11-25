<template>
  <div>
    <el-form ref="form" :model="form" class="text-form" label-width="100px">
      <el-form-item label="项目名称">
        {{ form.name }}
      </el-form-item>
      <el-form-item label="项目描述">
        {{ form.description }}
      </el-form-item>
      <el-form-item label="项目组长">
        {{ form.leader }}
      </el-form-item>
      <el-form-item label="访问权限">
        {{ form.isPrivate === 1 ? '私有' : '公开' }}
      </el-form-item>
      <el-form-item label="创建人">
        {{ form.creator }}
      </el-form-item>
      <el-form-item label="创建时间">
        {{ form.gmtCreate }}
      </el-form-item>
      <el-form-item>
        <!-- role_pos -->
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
        <!-- role_pos -->
        <el-form-item label="项目组长" required>
          <user-select ref="userSelect" :loader="loadSpaceMember" multiple :value="projectFormData.leaderIds" />
        </el-form-item>
        <!-- role_pos -->
        <el-form-item label="所属空间" prop="spaceId">
          <el-select
            v-model="projectFormData.spaceId"
            placeholder="请选择"
            style="width: 100%"
          >
            <el-option
              v-for="item in spaceData"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            >
              {{ item.name }}
            </el-option>
          </el-select>
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
        leaderIds: [],
        leader: '',
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
        spaceId: '',
        leaderIds: [],
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
  mounted() {
    this.loadSpaceData((data, spaceId) => {
      this.spaceData = data
    })
  },
  methods: {
    onProjectDel() {
      this.confirm('确认要删除该项目吗？', () => {
        this.post('/project/delete', { id: this.projectId }, () => {
          this.tipSuccess('删除成功')
          this.goHome()
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
        this.projectFormData.leaderIds = this.$refs.userSelect.getValue()
        this.post('/project/update', this.projectFormData, resp => {
          this.projectDlgShow = false
          // 如果空间变了，需要刷新页面
          if (this.projectFormData.spaceId !== this.form.spaceId) {
            location.reload()
          } else {
            this.tipSuccess('修改成功')
            this.fireEvent('projectChange', new Date().getTime())
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
          data.leader = data.leaders.map(userInfo => {
            return userInfo.realname
          }).join(' / ')
          data.leaderIds = data.leaders.map(userInfo => {
            return userInfo.id
          })
          this.form = data
        })
      }
    }
  }
}
</script>
