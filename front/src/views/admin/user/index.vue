<template>
  <div class="app-container">
    <el-form :inline="true" :model="searchFormData" class="demo-form-inline" size="mini">
      <el-form-item
        label="登录账号"
      >
        <el-input v-model="searchFormData.username" :clearable="true" placeholder="登录账号" style="width: 250px;" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="loadTable">查询</el-button>
      </el-form-item>
    </el-form>
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
      />
      <el-table-column
        prop="gmtCreate"
        label="注册时间"
        width="200"
      />
      <el-table-column
        label="操作"
      >
        <template slot-scope="scope">
          <el-popconfirm
            v-if="!isSelf(scope.row.id)"
            :title="`确定要重置 ${scope.row.nickname} 密码？`"
            @onConfirm="onRestPwd(scope.row)"
          >
            <el-link slot="reference" :underline="false" type="primary">重置密码</el-link>
          </el-popconfirm>
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
  </div>
</template>

<script>
export default {
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
      dialogTitle: '',
      dialogFormData: {
        id: '',
        username: '',
        password: '',
        nickname: ''
      },
      dialogFormRules: {
      }
    }
  },
  created() {
    this.loadTable()
  },
  methods: {
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
    onTableDelete(row) {
      this.confirm(`确认要删除该记录吗？`, function(done) {
        const data = {
          id: row.id
        }
        this.post('/userInfo/del', data, () => {
          done()
          this.tip('删除成功')
          this.loadTable()
        })
      })
    },
    onDialogSave() {
      this.$refs.dialogForm.validate((valid) => {
        if (valid) {
          const uri = this.dialogFormData.id ? '/userInfo/update' : '/userInfo/add'
          this.post(uri, this.dialogFormData, () => {
            this.dialogVisible = false
            this.loadTable()
          })
        }
      })
    },
    onSizeChange(size) {
      this.searchFormData.pageSize = size
      this.loadTable()
    },
    onAdd() {
      this.dialogTitle = '新增'
      this.dialogVisible = true
      this.dialogFormData.id = 0
    },
    onPageIndexChange(pageIndex) {
      this.searchFormData.pageIndex = pageIndex
      this.loadTable()
    }
  }
}
</script>
