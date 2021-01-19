<template>
  <div>
    <h3>我的消息</h3>
    <div class="table-opt-btn">
      <el-button
        v-show="hasUnreadMessage()"
        type="primary"
        size="mini"
        icon="el-icon-circle-check"
        @click="setReadAll"
      >
        全部标记已读
      </el-button>
    </div>
    <el-table
      :data="pageInfo.rows"
      border
      highlight-current-row
    >
      <el-table-column
        prop="message"
        label="内容"
        show-overflow-tooltip
      />
      <el-table-column
        prop="isRead"
        label="状态"
        width="80px"
      >
        <template slot-scope="scope">
          <el-tag :type="scope.row.isRead === 0 ? 'info' : 'success'">
            {{ scope.row.isRead === 0 ? '未读' : '已读' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="gmtCreate"
        label="推送时间"
        width="200px"
      />
      <el-table-column
        label="操作"
        width="200px"
      >
        <template slot-scope="scope">
          <router-link :to="`/view/doc/${scope.row.sourceId}`">
            <el-button type="text" size="mini" @click="setRead(scope.row)">查看文档</el-button>
          </router-link>
          <el-button v-show="scope.row.isRead === 0" type="text" size="mini" @click="setRead(scope.row)">标记已读</el-button>
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
      this.post('/user/message/page', this.searchFormData, resp => {
        this.pageInfo = resp.data
      })
    },
    setRead(item) {
      item.isRead = true
      this.post('/user/message/setRead', { id: item.id }, () => {
      })
    },
    setReadAll() {
      this.post('/user/message/setReadAll', { }, () => {
        this.loadTable()
      })
    },
    hasUnreadMessage() {
      return this.pageInfo.rows.filter(row => row.isRead === 0).length > 0
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
