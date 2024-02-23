<template>
  <div class="app-container">
    <el-form :inline="true" :model="searchFormData" class="demo-form-inline" size="mini">
      <el-form-item label="UserId">
        <el-input v-model="searchFormData.id" :clearable="true" style="width: 250px;"/>
      </el-form-item>
      <el-form-item :label="$t('loginAccount')">
        <el-input v-model="searchFormData.username" :clearable="true" style="width: 250px;"/>
      </el-form-item>
      <el-form-item :label="$t('status')">
        <el-select v-model="searchFormData.status" :clearable="true">
          <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value">
            {{ item.label }}
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item :label="$t('isSuperAdmin')">
        <el-select v-model="searchFormData.isSuperAdmin" :clearable="true">
          <el-option v-for="item in isSuperAdminOptions" :key="item.value" :label="item.label" :value="item.value">
            {{ item.label }}
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="onSearch">{{ $t('search') }}</el-button>
      </el-form-item>
    </el-form>
    <div class="table-opt-btn">
      <el-button type="primary" size="mini" icon="el-icon-plus" @click="onAdd">{{ $t('addNewUser') }}</el-button>
    </div>
    <el-table
      :data="pageInfo.rows"
      border
      highlight-current-row
    >
      <el-table-column
        prop="id"
        label="UserId"
        width="150"
      />
      <el-table-column
        prop="username"
        :label="$t('loginAccount')"
      />
      <el-table-column
        prop="nickname"
        :label="$t('nickname')"
        width="180"
      >
        <template slot-scope="scope">
          {{ scope.row.nickname }}
          <el-tooltip v-show="scope.row.isSuperAdmin" :content="$t('superAdmin')" placement="top">
            <i class="el-icon-s-custom"></i>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column
        prop="email"
        :label="$t('email')"
      />
      <el-table-column
        prop="source"
        :label="$t('origin')"
        width="120"
      >
        <template slot-scope="scope">
          <span>{{ getSource(scope.row) }}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="status"
        :label="$t('status')"
        width="100"
      >
        <template slot-scope="scope">
          <span v-if="scope.row.status === 0" class="danger">{{ $t('disable') }}</span>
          <span v-if="scope.row.status === 1" class="success">{{ $t('normal') }}</span>
          <span v-if="scope.row.status === 2" class="warning">{{ $t('inactive') }}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="gmtCreate"
        :label="$t('regTime')"
        width="120"
      >
        <template slot-scope="scope">
          <time-tooltip :time="scope.row.gmtCreate"/>
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('operation')"
        width="260"
      >
        <template slot-scope="scope">
          <div v-if="!isSelf(scope.row.id)">
            <el-link :underline="false" type="primary" @click="onAllocateProject(scope.row)">
              {{ $t('AdminUser.allocateProject') }}
            </el-link>
            <el-link :underline="false" type="primary" @click="onUserUpdate(scope.row)">{{ $t('update') }}</el-link>
            <el-popconfirm
              v-if="scope.row.source === getEnums().SOURCE.REGISTER || scope.row.source === getEnums().SOURCE.BACKEND"
              :title="$t('resetPasswordConfirm', scope.row.nickname)"
              @confirm="onRestPwd(scope.row)"
            >
              <el-link slot="reference" :underline="false" type="primary">{{ $t('resetPassword') }}</el-link>
            </el-popconfirm>
            <el-popconfirm
              v-if="scope.row.status === 1"
              :title="$t('disableConfirm', scope.row.nickname)"
              @confirm="onUserDisable(scope.row)"
            >
              <el-link slot="reference" :underline="false" type="danger">{{ $t('disable') }}</el-link>
            </el-popconfirm>
            <el-popconfirm
              v-if="scope.row.status === 0"
              :title="$t('enableConfirm', scope.row.nickname)"
              @confirm="onUserEnable(scope.row)"
            >
              <el-link slot="reference" :underline="false" type="primary">{{ $t('enable') }}</el-link>
            </el-popconfirm>
            <el-popconfirm
              :title="$t('deleteConfirm', scope.row.nickname)"
              @confirm="onUserDelete(scope.row)"
            >
              <el-link slot="reference" :underline="false" type="danger">{{ $t('delete') }}</el-link>
            </el-popconfirm>
          </div>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      background
      style="margin-top: 5px"
      :current-page="searchFormData.pageIndex"
      :page-size="searchFormData.pageSize"
      :page-sizes="getPageSizeConfig()"
      :total="pageInfo.total"
      layout="total, sizes, prev, pager, next"
      @size-change="onSizeChange"
      @current-change="onPageIndexChange"
    />
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      :close-on-click-modal="false"
      @close="resetForm('dialogForm')"
    >
      <el-form
        ref="dialogForm"
        :rules="dialogFormRules"
        :model="dialogFormData"
        label-width="120px"
        size="mini"
      >
        <el-form-item
          prop="username"
          :label="$t('loginAccount')"
        >
          <el-input v-model="dialogFormData.username" :placeholder="$t('loginAccount')"/>
        </el-form-item>
        <el-form-item
          prop="nickname"
          :label="$t('nickname')"
        >
          <el-input v-model="dialogFormData.nickname" :placeholder="$t('suggestUseRealName')"/>
        </el-form-item>
        <el-form-item
          prop="isSuperAdmin"
          :label="$t('isSuperAdmin')"
        >
          <el-switch
            v-model="dialogFormData.isSuperAdmin"
            active-color="#13ce66"
            inactive-color="#ff4949"
            :active-value="1"
            :inactive-value="0"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">{{ $t('dlgCancel') }}</el-button>
        <el-button type="primary" @click="onDialogSave">{{ $t('dlgSave') }}</el-button>
      </div>
    </el-dialog>

    <el-dialog
      :title="$t('AdminUser.allocateProject')"
      :visible.sync="chooseProjectShow"
      :close-on-click-modal="false"
    >
      <div style="margin-bottom: 10px">
        <el-form class="text-form" label-width="120">
          <el-form-item :label="$t('nickname')">
            {{ currentUserNick }}
          </el-form-item>
          <el-form-item :label="$t('role')">
            <el-radio-group v-model="projectRole">
              <el-radio :label="Role.guest">{{ $t('visitor') }}</el-radio>
              <el-radio :label="Role.dev">{{ $t('developer') }}</el-radio>
              <el-radio :label="Role.admin">{{ $t('admin') }}</el-radio>
            </el-radio-group>
          </el-form-item>
          <project-select ref="projectSelect" :on-ok="onAllocateProjectOk"
                          :on-cancel="() => chooseProjectShow = false"/>
        </el-form>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import TimeTooltip from '@/components/TimeTooltip'
import ProjectSelect from '@/components/ProjectSelect'

export default {
  components: {TimeTooltip, ProjectSelect},
  data() {
    return {
      statusOptions: [
        {value: 0, label: $t('disable')},
        {value: 1, label: $t('normal')},
        {value: 2, label: $t('inactive')}
      ],
      isSuperAdminOptions: [
        {value: 0, label: $t('no')},
        {value: 1, label: $t('yes')}
      ],
      searchFormData: {
        id: '',
        username: '',
        status: '',
        isSuperAdmin: '',
        pageIndex: 1,
        pageSize: 10
      },
      pageInfo: {
        rows: [],
        total: 0
      },
      dialogVisible: false,
      dialogTitle: $t('addNewUser'),
      dialogFormData: {
        username: '',
        nickname: '',
        isSuperAdmin: 0
      },
      dialogFormRules: {
        username: [
          {required: true, message: $t('notEmpty'), trigger: 'blur'}
        ],
        nickname: [
          {required: true, message: $t('notEmpty'), trigger: 'blur'}
        ]
      },
      currentUserId: 0,
      chooseProjectShow: false,
      projectRole: 'dev',
      currentUserNick: ''
    }
  },
  created() {
    this.loadTable()
  },
  methods: {
    onSearch() {
      this.searchFormData.pageIndex = 1
      this.loadTable()
    },
    loadTable() {
      this.post('/admin/user/page', this.searchFormData, resp => {
        this.pageInfo = resp.data
      })
    },
    onRestPwd(row) {
      this.post('/admin/user/password/reset', {id: row.id}, resp => {
        this.alert($t('resetPasswordSuccess', resp.data), $t('resetSuccess'))
      })
    },
    onUserDisable(row) {
      this.post('/admin/user/disable', row, () => {
        this.tipSuccess($t('operateSuccess'))
        this.loadTable()
      })
    },
    onUserEnable(row) {
      this.post('/admin/user/enable', row, () => {
        this.tipSuccess($t('operateSuccess'))
        this.loadTable()
      })
    },
    onUserDelete(row) {
      this.post('/admin/user/delete', row, () => {
        this.tipSuccess($t('operateSuccess'))
        this.loadTable()
      })
    },
    onUserUpdate(row) {
      this.dialogTitle = $t('update')
      Object.assign(this.dialogFormData, row)
      this.dialogVisible = true
    },
    onAllocateProject(row) {
      this.currentUserId = row.id
      this.currentUserNick = row.nickname
      this.get('/admin/user/getUserProjectIds', {userId: row.id}, resp => {
        const projectIds = resp.data
        this.chooseProjectShow = true
        this.$nextTick(() => {
          this.$refs.projectSelect.reload(projectIds)
        })
      })
    },
    onDialogSave() {
      this.$refs.dialogForm.validate((valid) => {
        if (valid) {
          const isUpdate = this.dialogFormData.id && this.dialogFormData.id.length > 0
          if (isUpdate) {
            const uri = '/admin/user/update'
            this.post(uri, this.dialogFormData, () => {
              this.tipSuccess($t('operateSuccess'))
              this.dialogVisible = false
              this.loadTable()
            })
          } else {
            const uri = '/admin/user/create'
            this.post(uri, this.dialogFormData, (resp) => {
              const data = resp.data
              this.alert($t('addUserSuccess', data.username, data.password), $t('createSuccess'))
              this.dialogVisible = false
              this.loadTable()
            })
          }
        }
      })
    },
    onSizeChange(size) {
      this.searchFormData.pageIndex = 1
      this.searchFormData.pageSize = size
      this.loadTable()
    },
    onAdd() {
      this.dialogVisible = true
      this.dialogTitle = $t('addNewUser')
      this.dialogFormData = {
        username: '',
        nickname: '',
        isSuperAdmin: 0
      }
    },
    onPageIndexChange(pageIndex) {
      this.searchFormData.pageIndex = pageIndex
      this.loadTable()
    },
    getSource(row) {
      if (!this.sourceMap) {
        this.sourceMap = this.getSourceMap()
      }
      return this.sourceMap[row.source] || row.source
    },
    getSourceMap() {
      return {
        'register': $t('selfReg'),
        'oauth': $t('thirdPartyLogin'),
        'form': $t('thirdPartyLogin'),
        'backend': $t('backendAdd'),
        'ldap': 'LDAP'
      }
    },
    onAllocateProjectOk(projectIds) {
      const data = {
        userId: this.currentUserId,
        role: this.projectRole,
        projectIds: projectIds
      }
      this.post('/admin/user/allocateProject', data, resp => {
        this.tipSuccess($t('operateSuccess'))
        this.chooseProjectShow = false
      })
    }
  }
}
</script>
