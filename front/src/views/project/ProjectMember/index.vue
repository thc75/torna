<template>
  <div class="app-container">
    <el-form :inline="true" :model="searchFormData" size="mini">
      <el-form-item label="登录账号">
        <el-input v-model="searchFormData.username" :clearable="true" placeholder="登录账号" style="width: 250px;" />
      </el-form-item>
      <el-form-item label="角色">
        <el-select v-model="searchFormData.roleCode" clearable>
          <el-option v-for="item in getProjectRoleCodeConfig()" :key="item.code" :value="item.code" :label="item.label">
            {{ item.label }}
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="loadTable">查询</el-button>
      </el-form-item>
    </el-form>
    <el-button
      v-if="hasRole(`project:${projectId}`, Role.admin)"
      type="primary"
      size="mini"
      icon="el-icon-plus"
      style="margin-bottom: 10px;"
      @click="onMemberAdd"
    >
      添加成员
    </el-button>
    <el-table
      :data="userData"
      border
      highlight-current-row
    >
      <el-table-column
        prop="username"
        label="成员"
        width="400"
      >
        <template slot-scope="scope">
          {{ `${scope.row.nickname}(${scope.row.username})` }}
          <el-tag v-if="isSelf(scope.row.id)">我</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="roleCode"
        label="角色"
        width="250"
      >
        <template slot-scope="scope">
          <el-select
            v-if="hasRole(`project:${projectId}`, Role.admin)"
            v-model="scope.row.roleCode"
            size="mini"
            :disabled="isSelf(scope.row.id)"
            @change="onRoleChange(scope.row)"
          >
            <el-option
              v-for="item in getProjectRoleCodeConfig()"
              :key="item.code"
              :value="item.code"
              :label="item.label"
            >
              {{ item.label }}
            </el-option>
          </el-select>
          <span v-else>
            {{ getProjectRoleName(scope.row.roleCode) }}
          </span>
        </template>
      </el-table-column>
      <el-table-column
        prop="gmtCreate"
        label="加入时间"
        width="200"
      />
      <el-table-column
        v-if="hasRole(`project:${projectId}`, Role.admin)"
        label="操作"
        width="150"
      >
        <template slot-scope="scope">
          <el-popconfirm
            v-if="!isSelf(scope.row.id)"
            :title="`确定要移除 ${scope.row.nickname} 吗？`"
            @confirm="onMemberRemove(scope.row)"
          >
            <el-link slot="reference" :disabled="isSelf(scope.row.id)" type="danger">移除</el-link>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
<!--    -->
    <el-dialog
      v-if="hasRole(`project:${projectId}`, Role.admin)"
      title="添加用户"
      :close-on-click-modal="false"
      :visible.sync="memberAddDlgShow"
      @close="onHide"
    >
      <el-form
        ref="memberAddForm"
        :model="memberAddFormData"
        :rules="memberAddRules"
        size="mini"
        style="width: 600px;"
        label-width="150px"
      >
        <el-form-item label="用户" required>
          <user-select ref="userSelect" :loader="loadSpaceMember" multiple />
        </el-form-item>
        <el-form-item label="角色" prop="roleCode">
          <el-select v-model="memberAddFormData.roleCode">
            <el-option v-for="item in getProjectRoleCodeConfig()" :key="item.code" :value="item.code" :label="item.label">
              {{ item.label }}
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="memberAddDlgShow = false">取 消</el-button>
        <el-button type="primary" @click="onMemberAddSave">保 存</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import UserSelect from '@/components/UserSelect'
export default {
  name: 'ProjectMember',
  components: {
    UserSelect
  },
  props: {
    projectId: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      searchFormData: {
        username: '',
        roleCode: '',
        projectId: ''
      },
      userData: [],
      memberAddDlgShow: false,
      memberAddFormData: {
        roleCode: ''
      },
      memberAddRules: {
        roleCode: [
          { required: true, message: '请选择', trigger: ['blur', 'change'] }
        ]
      }
    }
  },
  watch: {
    projectId(projectId) {
      if (projectId) {
        this.searchFormData.projectId = projectId
        this.loadTable()
      }
    }
  },
  methods: {
    loadTable() {
      this.get('/project/member/list', this.searchFormData, resp => {
        this.userData = resp.data
      })
    },
    onRoleChange(row) {
      const data = {
        projectId: this.projectId,
        userId: row.id,
        roleCode: row.roleCode
      }
      this.post('/project/member/update', data, () => {
        this.tipSuccess('修改成功')
      })
    },
    onMemberRemove(row) {
      const data = {
        projectId: this.projectId,
        userId: row.id
      }
      this.post('/project/member/remove', data, resp => {
        this.tipSuccess('移除成功')
        this.loadTable()
      })
    },
    onMemberAdd() {
      this.memberAddDlgShow = true
    },
    onMemberAddSave() {
      const promise = this.$refs.userSelect.validate()
      const promiseMain = this.$refs.memberAddForm.validate()
      // memberAddFormData
      Promise.all([promise, promiseMain]).then(validArr => {
        // 到这里来表示全部内容校验通过
        const userIds = this.$refs.userSelect.getValue()
        Object.assign(this.memberAddFormData, {
          projectId: this.projectId,
          userIds: userIds
        })
        this.post('/project/member/add', this.memberAddFormData, resp => {
          this.tipSuccess('添加成功')
          this.memberAddDlgShow = false
          this.loadTable()
        })
      }).catch((e) => {
      }) // 加上这个控制台不会报Uncaught (in promise)
    },
    onHide() {
      this.resetForm('memberAddForm')
      this.$refs.userSelect.resetForm()
    }
  }
}
</script>

