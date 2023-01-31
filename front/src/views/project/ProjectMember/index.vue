<template>
  <div class="app-container">
    <el-form :inline="true" :model="searchFormData" size="mini">
      <el-form-item :label="$t('nickEmail')">
        <el-input v-model="searchFormData.username" :clearable="true" style="width: 250px;" />
      </el-form-item>
      <el-form-item :label="$t('role')">
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
      {{ $t('addMember') }}
    </el-button>
    <el-table
      :data="userData"
      border
      highlight-current-row
    >
      <el-table-column
        prop="username"
        :label="$t('member')"
        width="400"
      >
        <template slot-scope="scope">
          {{ scope.row.nickname }}<span v-show="scope.row.email">{{ `(${scope.row.email})` }}</span>
          <el-tag v-if="isSelf(scope.row.id)">{{ $t('me') }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="roleCode"
        :label="$t('role')"
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
        :label="$t('joinTime')"
        width="200"
      />
      <el-table-column
        v-if="hasRole(`project:${projectId}`, Role.admin)"
        :label="$t('operation')"
        width="150"
      >
        <template slot-scope="scope">
          <el-popconfirm
            v-if="!isSelf(scope.row.id)"
            :title="$t('removeConfirm', scope.row.nickname)"
            @confirm="onMemberRemove(scope.row)"
          >
            <el-link slot="reference" :disabled="isSelf(scope.row.id)" type="danger">{{ $t('remove') }}</el-link>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <!-- dialog   -->
    <el-dialog
      v-if="hasRole(`project:${projectId}`, Role.admin)"
      :title="$t('addUser')"
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
        <el-form-item :label="$t('user')" required>
          <user-select-v2 ref="userSelect" multiple />
        </el-form-item>
        <el-form-item :label="$t('role')" prop="roleCode">
          <el-select v-model="memberAddFormData.roleCode">
            <el-option v-for="item in getProjectRoleCodeConfig()" :key="item.code" :value="item.code" :label="item.label">
              {{ item.label }}
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="memberAddDlgShow = false">{{ $t('dlgCancel') }}</el-button>
        <el-button type="primary" @click="onMemberAddSave">{{ $t('dlgSave') }}</el-button>
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
          { required: true, message: this.$t('pleaseSelect'), trigger: ['blur', 'change'] }
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
        this.tipSuccess(this.$t('updateSuccess'))
      })
    },
    onMemberRemove(row) {
      const data = {
        projectId: this.projectId,
        userId: row.id
      }
      this.post('/project/member/remove', data, resp => {
        this.tipSuccess(this.$t('removeSuccess'))
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
          this.tipSuccess($t('addSuccess'))
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

