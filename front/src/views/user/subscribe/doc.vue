<template>
  <div>
    <h3>我关注的接口</h3>
    <el-table
      :data="pageInfo.rows"
      border
      highlight-current-row
      style="width: 600px"
    >
      <el-table-column
        prop="name"
        label="接口名称"
      />
      <el-table-column
        label="操作"
        width="120px"
      >
        <template slot-scope="scope">
          <el-popconfirm
            :title="`确定要取消关注 ${scope.row.name} 吗？`"
            @onConfirm="onCancel(scope.row)"
          >
            <el-link slot="reference" type="primary" size="mini">取消关注</el-link>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      background
      style="margin-top: 5px"
      :current-page="searchFormData.pageIndex"
      :page-size="searchFormData.pageSize"
      :page-sizes="[5, 10, 20, 40]"
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
        pageIndex: 1,
        pageSize: 20
      },
      pageInfo: {
        rows: [],
        total: 0
      }
    }
  },
  created() {
    this.loadTable()
  },
  methods: {
    loadTable() {
      this.get('/user/subscribe/doc/page', this.searchFormData, resp => {
        this.pageInfo = resp.data
      })
    },
    onCancel(row) {
      const data = {
        sourceId: row.id
      }
      this.post('/user/subscribe/doc/cancelSubscribe', data, () => {
        this.tipSuccess('操作成功')
        this.loadTable()
      })
    },
    onSizeChange(size) {
      this.searchFormData.pageSize = size
      this.loadTable()
    },
    onPageIndexChange(pageIndex) {
      this.searchFormData.pageIndex = pageIndex
      this.loadTable()
    }
  }
}
</script>
