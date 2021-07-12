<template>
  <div class="app-container">
    <el-form :inline="true" :model="searchFormData" size="mini">
      <el-form-item :label="$ts('nickEmail')">
        <el-input v-model="searchFormData.username" :clearable="true" style="width: 250px;" />
      </el-form-item>
      <el-form-item :label="$ts('role')">
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
      {{ $ts('addMember') }}
    </el-button>
    <el-table
      :data="userData"
      border
      highlight-current-row
    >
      <el-table-column
        prop="username"
        :label="$ts('member')"
        width="400"
      >
        <template slot-scope="scope">
          {{ scope.row.nickname }}<span v-show="scope.row.email">{{ `(${scope.row.email})` }}</span>
          <el-tag v-if="isSelf(scope.row.id)">{{ $ts('me') }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="roleCode"
        :label="$ts('role')"
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
        :label="$ts('joinTime')"
        width="200"
      />
      <el-table-column
        v-if="hasRole(`project:${projectId}`, Role.admin)"
        :label="$ts('operation')"
        width="150"
      >
        <template slot-scope="scope">
          <el-popconfirm
            v-if="!isSelf(scope.row.id)"
            :title="$ts('removeConfirm', scope.row.nickname)"
            @confirm="onMemberRemove(scope.row)"
          >
            <el-link slot="reference" :disabled="isSelf(scope.row.id)" type="danger">{{ $ts('remove') }}</el-link>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <!-- dialog   -->
    <el-dialog
      v-if="hasRole(`project:${projectId}`, Role.admin)"
      :title="$ts('addUser')"
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
        <el-form-item :label="$ts('user')" required>
          <user-select-v2 ref="userSelect" multiple />
        </el-form-item>
        <el-form-item :label="$ts('role')" prop="roleCode">
          <el-select v-model="memberAddFormData.roleCode">
            <el-option v-for="item in getProjectRoleCodeConfig()" :key="item.code" :value="item.code" :label="item.label">
              {{ item.label }}
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="memberAddDlgShow = false">{{ $ts('dlgCancel') }}</el-button>
        <el-button type="primary" @click="onMemberAddSave">{{ $ts('dlgSave') }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import UserSelectV2 from '@/components/UserSelectV2'
export default {
  name: 'ProjectMember',
  components: {
    UserSelectV2
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
          { required: true, message: this.$ts('pleaseSelect'), trigger: ['blur', 'change'] }
        ]
      },
      spaceUsers: []
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
    loadSelectUser() {
      this.loadSpaceMember().then(data => {
        this.spaceUsers = data
      })
    },
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
        this.tipSuccess(this.$ts('updateSuccess'))
      })
    },
    onMemberRemove(row) {
      const data = {
        projectId: this.projectId,
        userId: row.id
      }
      this.post('/project/member/remove', data, resp => {
        this.tipSuccess(this.$ts('removeSuccess'))
        this.loadTable()
      })
    },
    onMemberAdd() {
      this.memberAddDlgShow = true
      this.$nextTick(() => {
        this.loadSpaceMember().then(data => {
          this.$refs.userSelect.setData(data)
        })
      })
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
          this.tipSuccess($ts('addSuccess'))
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

