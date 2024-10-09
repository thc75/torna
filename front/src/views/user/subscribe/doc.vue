<template>
  <div class="app-container">
    <h3>{{ $t('mySubscribeApi') }}</h3>
    <el-table
      :data="pageInfo.list"
      border
      highlight-current-row
      style="width: 600px"
    >
      <el-table-column
        prop="name"
        :label="$t('apiName')"
      />
      <el-table-column
        :label="$t('operation')"
        width="130px"
      >
        <template slot-scope="scope">
          <el-popconfirm
            :title="$t('cancelSubscribeConfirm', scope.row.name)"
            @confirm="onCancel(scope.row)"
          >
            <el-link slot="reference" type="primary" size="mini">{{ $t('cancelSubscribe') }}</el-link>
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
        list: [],
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
        this.tipSuccess($t('operateSuccess'))
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
