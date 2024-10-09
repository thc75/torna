<template>
  <div class="app-container">
    <h3>{{ $t('myMessage') }}</h3>
    <div class="table-opt-btn">
      <el-button
        v-show="hasUnreadMessage()"
        type="primary"
        size="mini"
        icon="el-icon-circle-check"
        @click="setReadAll"
      >
        {{ $t('userMsgReadAll') }}
      </el-button>
      <el-button
        type="danger"
        size="mini"
        icon="el-icon-delete"
        @click="deletePushMessage"
      >
        {{ $t('clearPushMsg') }}
      </el-button>
    </div>
    <el-table
      :data="pageInfo.list"
      border
      highlight-current-row
    >
      <el-table-column
        prop="message"
        :label="$t('content')"
        show-overflow-tooltip
      />
      <el-table-column
        prop="isRead"
        :label="$t('status')"
        width="80px"
      >
        <template slot-scope="scope">
          <el-tag :type="scope.row.isRead === 0 ? 'info' : 'success'">
            {{ scope.row.isRead === 0 ? $t('unread') : $t('read') }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="gmtCreate"
        :label="$t('pushTime')"
        width="200px"
      />
      <el-table-column
        :label="$t('operation')"
        width="200px"
      >
        <template slot-scope="scope">
          <router-link v-if="scope.row.type === 1" :to="`/view/${scope.row.sourceId}`">
            <el-button type="text" size="mini" @click="setRead(scope.row)">{{ $t('viewDoc') }}</el-button>
          </router-link>
          <el-button v-show="scope.row.isRead === 0" type="text" size="mini" @click="setRead(scope.row)">{{ $t('setRead') }}</el-button>
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
        pageSize: 10
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
    deletePushMessage() {
      this.confirm(this.$t('deleteConfirm', this.$t('myMessage')), () => {
        this.post('/user/message/deletePushMessage', { }, () => {
          this.tipSuccess(this.$t('deleteSuccess'))
          this.loadTable()
        })
      })
    },
    hasUnreadMessage() {
      return this.pageInfo.list.filter(row => row.isRead === 0).length > 0
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
