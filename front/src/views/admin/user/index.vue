<template>
  <div class="app-container">
    <el-form :inline="true" :model="searchFormData" class="demo-form-inline" size="mini">
      <el-form-item label="UserId">
        <el-input v-model="searchFormData.id" :clearable="true" style="width: 250px;" />
      </el-form-item>
      <el-form-item :label="$ts('loginAccount')">
        <el-input v-model="searchFormData.username" :clearable="true" style="width: 250px;" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="onSearch">{{ $ts('search') }}</el-button>
      </el-form-item>
    </el-form>
    <div class="table-opt-btn">
      <el-button type="primary" size="mini" icon="el-icon-plus" @click="onAdd">{{ $ts('addNewUser') }}</el-button>
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
        :label="$ts('loginAccount')"
      />
      <el-table-column
        prop="nickname"
        :label="$ts('nickname')"
        width="180"
      >
        <template slot-scope="scope">
          {{ scope.row.nickname }}
          <el-tooltip v-show="scope.row.isSuperAdmin" :content="$ts('superAdmin')" placement="top">
            <i class="el-icon-s-custom"></i>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column
        prop="email"
        :label="$ts('email')"
      />
      <el-table-column
        prop="source"
        :label="$ts('origin')"
        width="120"
      >
        <template slot-scope="scope">
          <span>{{ getSource(scope.row) }}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="status"
        :label="$ts('status')"
        width="100"
      >
        <template slot-scope="scope">
          <span v-if="scope.row.status === 0" class="danger">{{ $ts('disable') }}</span>
          <span v-if="scope.row.status === 1" class="success">{{ $ts('normal') }}</span>
          <span v-if="scope.row.status === 2" class="warning">{{ $ts('inactive') }}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="gmtCreate"
        :label="$ts('regTime')"
        width="120"
      >
        <template slot-scope="scope">
          <time-tooltip :time="scope.row.gmtCreate" />
        </template>
      </el-table-column>
      <el-table-column
        :label="$ts('operation')"
        width="210"
      >
        <template slot-scope="scope">
          <div v-if="!isSelf(scope.row.id)">
            <el-link :underline="false" type="primary" @click="onUserUpdate(scope.row)">{{ $ts('update') }}</el-link>
            <el-popconfirm
              v-if="scope.row.source === getEnums().SOURCE.REGISTER || scope.row.source === getEnums().SOURCE.BACKEND"
              :title="$ts('resetPasswordConfirm', scope.row.nickname)"
              @confirm="onRestPwd(scope.row)"
            >
              <el-link slot="reference" :underline="false" type="primary">{{ $ts('resetPassword') }}</el-link>
            </el-popconfirm>
            <el-popconfirm
              v-if="scope.row.status === 1"
              :title="$ts('disableConfirm', scope.row.nickname)"
              @confirm="onUserDisable(scope.row)"
            >
              <el-link slot="reference" :underline="false" type="danger">{{ $ts('disable') }}</el-link>
            </el-popconfirm>
            <el-popconfirm
              v-if="scope.row.status === 0"
              :title="$ts('enableConfirm', scope.row.nickname)"
              @confirm="onUserEnable(scope.row)"
            >
              <el-link slot="reference" :underline="false" type="primary">{{ $ts('enable') }}</el-link>
            </el-popconfirm>
            <el-popconfirm
              :title="$ts('deleteConfirm', scope.row.nickname)"
              @confirm="onUserDelete(scope.row)"
            >
              <el-link slot="reference" :underline="false" type="danger">{{ $ts('delete') }}</el-link>
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
          :label="$ts('loginAccount')"
        >
          <el-input v-model="dialogFormData.username" :placeholder="$ts('suggestUseEmail')" />
        </el-form-item>
        <el-form-item
          prop="nickname"
          :label="$ts('nickname')"
        >
          <el-input v-model="dialogFormData.nickname" :placeholder="$ts('suggestUseRealName')" />
        </el-form-item>
        <el-form-item
          prop="isSuperAdmin"
          :label="$ts('isSuperAdmin')"
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
        <el-button @click="dialogVisible = false">{{ $ts('dlgCancel') }}</el-button>
        <el-button type="primary" @click="onDialogSave">{{ $ts('dlgSave') }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import TimeTooltip from '@/components/TimeTooltip'

export default {
  components: { TimeTooltip },
  data() {
    return {
      searchFormData: {
        id: '',
        username: '',
        pageIndex: 1,
        pageSize: 10
      },
      pageInfo: {
        rows: [],
        total: 0
      },
      dialogVisible: false,
      dialogTitle: $ts('addNewUser'),
      dialogFormData: {
        username: '',
        nickname: '',
        isSuperAdmin: 0
      },
      dialogFormRules: {
        username: [
          { required: true, message: $ts('notEmpty'), trigger: 'blur' }
        ],
        nickname: [
          { required: true, message: $ts('notEmpty'), trigger: 'blur' }
        ]
      }
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
      this.post('/admin/user/password/reset', { id: row.id }, resp => {
        this.alert($ts('resetPasswordSuccess', resp.data), $ts('resetSuccess'))
      })
    },
    onUserDisable(row) {
      this.post('/admin/user/disable', row, () => {
        this.tipSuccess($ts('operateSuccess'))
        this.loadTable()
      })
    },
    onUserEnable(row) {
      this.post('/admin/user/enable', row, () => {
        this.tipSuccess($ts('operateSuccess'))
        this.loadTable()
      })
    },
    onUserDelete(row) {
      this.post('/admin/user/delete', row, () => {
        this.tipSuccess($ts('operateSuccess'))
        this.loadTable()
      })
    },
    onUserUpdate(row) {
      this.dialogTitle = $ts('update')
      Object.assign(this.dialogFormData, row)
      this.dialogVisible = true
    },
    onDialogSave() {
      this.$refs.dialogForm.validate((valid) => {
        if (valid) {
          const isUpdate = this.dialogFormData.id && this.dialogFormData.id.length > 0
          if (isUpdate) {
            const uri = '/admin/user/update'
            this.post(uri, this.dialogFormData, () => {
              this.tipSuccess($ts('operateSuccess'))
              this.dialogVisible = false
              this.loadTable()
            })
          } else {
            const uri = '/admin/user/create'
            this.post(uri, this.dialogFormData, (resp) => {
              const data = resp.data
              this.alert($ts('addUserSuccess', data.username, data.password), $ts('createSuccess'))
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
      this.dialogTitle = $ts('addNewUser')
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
        'register': $ts('selfReg'),
        'oauth': $ts('thirdPartyLogin'),
        'form': $ts('thirdPartyLogin'),
        'backend': $ts('backendAdd'),
        'ldap': 'LDAP'
      }
    }
  }
}
</script>
