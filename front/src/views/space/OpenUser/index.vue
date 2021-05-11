<template>
  <div v-if="spaceId && hasRole(`space:${spaceId}`, Role.admin)" class="app-container">
    <el-form :inline="true" :model="searchFormData" class="demo-form-inline" size="mini">
      <el-form-item
        label="appKey"
      >
        <el-input v-model="searchFormData.appKey" clearable placeholder="appKey" style="width: 250px;"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="reload">{{ $ts('search') }}</el-button>
      </el-form-item>
    </el-form>
    <el-button type="primary" size="mini" icon="el-icon-plus" style="margin-bottom: 10px;" @click="onAdd">{{ $ts('createAccount') }}</el-button>
    <el-table
      :data="pageInfo.rows"
      border
      highlight-current-row
    >
      <el-table-column
        prop="appKey"
        label="AppKey"
      />
      <el-table-column
        prop="secret"
        label="Secret"
      />
      <el-table-column
        prop="applicant"
        :label="$ts('applicant')"
        width="150px"
      />
      <el-table-column
        prop="status"
        :label="$ts('status')"
        width="100px"
      >
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === 1" type="success">{{ $ts('enable') }}</el-tag>
          <el-tag v-if="scope.row.status === 0" type="danger">{{ $ts('disable') }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="gmtCreate"
        :label="$ts('addTime')"
        width="160px"
      />
      <el-table-column
        :label="$ts('operation')"
        width="180px"
      >
        <template slot-scope="scope">
          <el-popconfirm
            :title="$ts('enableAccountConfirm')"
            @confirm="onEnable(scope.row)"
          >
            <el-link v-show="scope.row.status === 0" slot="reference" type="success">{{ $ts('enable') }}</el-link>
          </el-popconfirm>
          <el-popconfirm
            :title="$ts('disableAccountConfirm')"
            @confirm="onDisable(scope.row)"
          >
            <el-link v-show="scope.row.status === 1" slot="reference" type="danger">{{ $ts('disable') }}</el-link>
          </el-popconfirm>
          <el-popconfirm
            :title="$ts('resetSecretConfirm')"
            @confirm="onRestSecret(scope.row)"
          >
            <el-link slot="reference" type="primary">{{ $ts('resetSecret') }}</el-link>
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
        pageSize: 10
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
        this.tipSuccess(this.$ts('operateSuccess'))
        this.reload()
      })
    },
    onDisable(row) {
      this.post('/openuser/disable', {
        id: row.id
      }, resp => {
        this.tipSuccess(this.$ts('operateSuccess'))
        this.reload()
      })
    },
    onRestSecret(row) {
      this.post('/openuser/secret/reset', {
        id: row.id
      }, resp => {
        this.tipSuccess(this.$ts('operateSuccess'))
        this.reload()
      })
    },
    onAdd() {
      this.$prompt(this.$ts('applicant'), this.$ts('createAccount'), {
        confirmButtonText: this.$ts('ok'),
        cancelButtonText: this.$ts('cancel'),
        inputPattern: /^.{1,64}$/,
        inputErrorMessage: this.$ts('notEmptyLengthLimit', 64)
      }).then(({ value }) => {
        const data = {
          spaceId: this.spaceId,
          applicant: value
        }
        this.post('/openuser/add', data, resp => {
          this.tipSuccess(this.$ts('createSuccess'))
          this.reload()
        })
      }).catch(() => {
      })
    },
    onSizeChange(size) {
      this.searchFormData.pageIndex = 1
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
