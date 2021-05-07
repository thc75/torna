<template>
  <div class="app-container">
    <el-form :inline="true" :model="searchFormData" class="demo-form-inline" size="mini">
      <el-form-item
        label="登录账号"
      >
        <el-input v-model="searchFormData.username" :clearable="true" placeholder="登录账号" style="width: 250px;" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="onSearch">查询</el-button>
      </el-form-item>
    </el-form>
    <div class="table-opt-btn">
      <el-button type="primary" size="mini" icon="el-icon-plus" @click="onAdd">创建新用户</el-button>
    </div>
    <el-table
      :data="pageInfo.rows"
      border
      highlight-current-row
    >
      <el-table-column
        prop="username"
        label="登录账号"
      />
      <el-table-column
        prop="nickname"
        label="昵称"
      >
        <template slot-scope="scope">
          {{ scope.row.nickname }}
          <el-tooltip v-show="scope.row.isSuperAdmin" content="超级管理员" placement="top">
            <i class="el-icon-s-custom"></i>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column
        prop="email"
        label="邮箱"
      />
      <el-table-column
        prop="source"
        label="来源"
        width="120"
      >
        <template slot-scope="scope">
          <span>{{ getSource(scope.row) }}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="status"
        label="状态"
        width="100"
      >
        <template slot-scope="scope">
          <span v-if="scope.row.status === 0" class="danger">禁用</span>
          <span v-if="scope.row.status === 1" class="success">正常</span>
          <span v-if="scope.row.status === 2" class="warning">未激活</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="gmtCreate"
        label="注册时间"
        width="120"
      >
        <template slot-scope="scope">
          <time-tooltip :time="scope.row.gmtCreate" />
        </template>
      </el-table-column>
      <el-table-column
        label="操作"
        width="150"
      >
        <template slot-scope="scope">
          <div v-if="!isSelf(scope.row.id)">
            <el-popconfirm
              v-if="scope.row.source === 'register'"
              :title="`确定要重置 ${scope.row.nickname} 密码？`"
              @confirm="onRestPwd(scope.row)"
            >
              <el-link slot="reference" :underline="false" type="primary">重置密码</el-link>
            </el-popconfirm>
            <el-popconfirm
              v-if="scope.row.status === 1"
              :title="`确定要禁用 ${scope.row.nickname} ？`"
              @confirm="onUserDisable(scope.row)"
            >
              <el-link slot="reference" :underline="false" type="danger">禁用</el-link>
            </el-popconfirm>
            <el-popconfirm
              v-if="scope.row.status === 0"
              :title="`确定要启用 ${scope.row.nickname} ？`"
              @confirm="onUserEnable(scope.row)"
            >
              <el-link slot="reference" :underline="false" type="primary">启用</el-link>
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
          label="登录账号"
        >
          <el-input v-model="dialogFormData.username" placeholder="建议使用邮箱" />
        </el-form-item>
        <el-form-item
          prop="nickname"
          label="昵称"
        >
          <el-input v-model="dialogFormData.nickname" placeholder="建议使用姓名或花名" />
        </el-form-item>
        <el-form-item
          prop="isSuperAdmin"
          label="是否超级管理员"
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
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="onDialogSave">保 存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import TimeTooltip from '@/components/TimeTooltip'

const SOURCE_MAP = {
  'register': '自主注册',
  'oauth': '第三方登录',
  'form': '第三方登录',
  'backend': '后台创建'
}
export default {
  components: { TimeTooltip },
  data() {
    return {
      searchFormData: {
        username: '',
        pageIndex: 1,
        pageSize: 10
      },
      pageInfo: {
        rows: [],
        total: 0
      },
      dialogVisible: false,
      dialogTitle: '新建用户',
      dialogFormData: {
        username: '',
        nickname: '',
        isSuperAdmin: 0
      },
      dialogFormRules: {
        username: [
          { required: true, message: '不能为空', trigger: 'blur' }
        ],
        nickname: [
          { required: true, message: '不能为空', trigger: 'blur' }
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
        this.alert(`新密码：${resp.data}`, '重置成功')
      })
    },
    onUserDisable(row) {
      this.post('/admin/user/disable', row, () => {
        this.tipSuccess('操作成功')
        this.loadTable()
      })
    },
    onUserEnable(row) {
      this.post('/admin/user/enable', row, () => {
        this.tipSuccess('操作成功')
        this.loadTable()
      })
    },
    onDialogSave() {
      this.$refs.dialogForm.validate((valid) => {
        if (valid) {
          const uri = '/admin/user/create'
          this.post(uri, this.dialogFormData, (resp) => {
            const data = resp.data
            this.alert(`登录账号：${data.username}\n密码：${data.password}`, '创建成功')
            this.dialogVisible = false
            this.loadTable()
          })
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
    },
    onPageIndexChange(pageIndex) {
      this.searchFormData.pageIndex = pageIndex
      this.loadTable()
    },
    getSource(row) {
      return SOURCE_MAP[row.source] || '未知'
    }
  }
}
</script>
