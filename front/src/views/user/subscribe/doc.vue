<template>
  <div class="app-container">
    <h3>{{ $ts('mySubscribeApi') }}</h3>
    <el-table
      :data="pageInfo.rows"
      border
      highlight-current-row
      style="width: 600px"
    >
      <el-table-column
        prop="name"
        :label="$ts('apiName')"
      />
      <el-table-column
        :label="$ts('operation')"
        width="130px"
      >
        <template slot-scope="scope">
          <el-popconfirm
            :title="$ts('cancelSubscribeConfirm', scope.row.name)"
            @confirm="onCancel(scope.row)"
          >
            <el-link slot="reference" type="primary" size="mini">{{ $ts('cancelSubscribe') }}</el-link>
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
        this.tipSuccess($ts('operateSuccess'))
        this.loadTable()
      })
    },
    onSizeChange(size) {
      this.searchFormData.pageIndex = 1
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
