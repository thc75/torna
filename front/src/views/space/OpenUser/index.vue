<template>
  <div class="app-container">
    <el-form :inline="true" :model="searchFormData" class="demo-form-inline" size="mini">
      <el-form-item
        label="appKey"
      >
        <el-input v-model="searchFormData.appKey" clearable placeholder="appKey" style="width: 250px;"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="reload">查询</el-button>
      </el-form-item>
    </el-form>
    <el-button type="primary" size="mini" icon="el-icon-plus" style="margin-bottom: 10px;" @click="onAdd">新建账号</el-button>
    <el-table
      :data="pageInfo.rows"
      border
      highlight-current-row
    >
      <el-table-column
        prop="appKey"
        label="appKey"
      />
      <el-table-column
        prop="secret"
        label="secret"
      />
      <el-table-column
        prop="applicant"
        label="申请人"
        width="150px"
      />
      <el-table-column
        prop="status"
        label="状态"
        width="100px"
      >
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === 1" type="success">启用</el-tag>
          <el-tag v-if="scope.row.status === 0" type="danger">禁用</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="gmtCreate"
        label="添加时间"
        width="160px"
      />
      <el-table-column
        label="操作"
        width="140px"
      >
        <template slot-scope="scope">
          <el-popconfirm
            :title="`确定要启用此账号吗？`"
            @onConfirm="onEnable(scope.row)"
          >
            <el-link v-show="scope.row.status === 0" slot="reference" type="success">启用</el-link>
          </el-popconfirm>
          <el-popconfirm
            :title="`确定要禁用此账号吗？`"
            @onConfirm="onDisable(scope.row)"
          >
            <el-link v-show="scope.row.status === 1" slot="reference" type="danger">禁用</el-link>
          </el-popconfirm>
          <el-popconfirm
            :title="`确定要重置secret吗？`"
            @onConfirm="onRestSecret(scope.row)"
          >
            <el-link slot="reference" type="primary">重置secret</el-link>
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
  name: 'OpenUser',
  props: {
    spaceId: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      searchFormData: {
        appKey: '',
        spaceId: '',
        pageIndex: 1,
        pageSize: 20
      },
      pageInfo: {
        rows: [],
        total: 0
      }
    }
  },
  watch: {
    spaceId(spaceId) {
      this.loadTable(spaceId)
    }
  },
  methods: {
    reload() {
      this.loadTable(this.spaceId)
    },
    loadTable(spaceId) {
      this.searchFormData.spaceId = spaceId
      this.post('/openuser/page', this.searchFormData, resp => {
        this.pageInfo = resp.data
      })
    },
    onEnable(row) {
      this.post('/openuser/enable', {
        id: row.id
      }, resp => {
        this.tipSuccess('操作成功')
        this.reload()
      })
    },
    onDisable(row) {
      this.post('/openuser/disable', {
        id: row.id
      }, resp => {
        this.tipSuccess('操作成功')
        this.reload()
      })
    },
    onRestSecret(row) {
      this.post('/openuser/secret/reset', {
        id: row.id
      }, resp => {
        this.tipSuccess('操作成功')
        this.reload()
      })
    },
    onAdd() {
      this.$prompt('申请人', '新建账号', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /^.{1,64}$/,
        inputErrorMessage: '不能为空且长度在64以内'
      }).then(({ value }) => {
        const data = {
          spaceId: this.spaceId,
          applicant: value
        }
        this.post('/openuser/add', data, resp => {
          this.tipSuccess('创建成功')
          this.reload()
        })
      }).catch(() => {
      })
    },
    onSizeChange(size) {
      this.searchFormData.pageSize = size
      this.reload()
    },
    onPageIndexChange(pageIndex) {
      this.searchFormData.pageIndex = pageIndex
      this.reload()
    }
  }
}
</script>
