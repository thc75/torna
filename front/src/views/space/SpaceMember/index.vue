<template>
  <div class="app-container">
    <el-form :inline="true" :model="searchFormData" size="mini" @submit.native.prevent>
      <el-form-item :label="$t('nickEmail')">
        <el-input v-model="searchFormData.username" :clearable="true" style="width: 250px;" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" native-type="submit" @click="onSearch">{{ $t('search') }}</el-button>
      </el-form-item>
    </el-form>
    <el-button
      v-if="hasRole(`space:${spaceId}`, Role.admin)"
      type="primary"
      size="mini"
      icon="el-icon-plus"
      style="margin-bottom: 10px;"
      @click="onMemberAdd"
    >
      {{ $t('addMember') }}
    </el-button>
    <el-table
      :data="pageInfo.list"
      border
      highlight-current-row
    >
      <el-table-column
        prop="username"
        :label="$t('member')"
        width="400"
      >
        <template slot-scope="scope">
          {{ scope.row.nickname }}<span v-show="scope.row.email">{{ `(${scope.row.email})` }}</span>
          <el-tag v-if="isSelf(scope.row.id)">{{ $t('me') }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="roleCode"
        :label="$t('role')"
        width="250"
      >
        <template slot-scope="scope">
          <el-select
            v-if="hasRole(`space:${spaceId}`, Role.admin)"
            v-model="scope.row.roleCode"
            size="mini"
            :disabled="isSelf(scope.row.id)"
            @change="onRoleChange(scope.row)"
          >
            <el-option v-for="item in getSpaceRoleCodeConfig()" :key="item.code" :value="item.code" :label="item.label">
              {{ item.label }}
            </el-option>
          </el-select>
          <span v-else>
            {{ getSpaceRoleName(scope.row.roleCode) }}
          </span>
        </template>
      </el-table-column>
      <el-table-column
        prop="gmtCreate"
        :label="$t('joinTime')"
        width="200"
      />
      <el-table-column
        v-if="hasRole(`space:${spaceId}`, [Role.admin])"
        :label="$t('operation')"
        width="150"
      >
        <template slot-scope="scope">
          <el-popconfirm
            v-if="!isSelf(scope.row.id)"
            :title="$t('removeConfirm', scope.row.nickname)"
            @confirm="onMemberRemove(scope.row)"
          >
            <el-link slot="reference" type="danger" size="mini">{{ $t('remove') }}</el-link>
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
    <!--    -->
    <el-dialog
      :title="$t('addUser')"
      :close-on-click-modal="false"
      :visible.sync="memberAddDlgShow"
      @close="onHide"
    >
      <el-form
        ref="memberAddForm"
        :model="memberAddFormData"
        :rules="memberAddRules"
        size="mini"
        style="width: 600px;"
        label-width="150px"
      >
        <el-form-item :label="$t('user')" required>
          <!-- role_pos -->
          <user-select-v2 ref="userSelect" multiple />
        </el-form-item>
        <el-form-item :label="$t('role')" prop="roleCode">
          <el-select v-model="memberAddFormData.roleCode">
            <el-option v-for="item in getSpaceRoleCodeConfig()" :key="item.code" :value="item.code" :label="item.label">
              {{ item.label }}
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="memberAddDlgShow = false">{{ $t('dlgCancel') }}</el-button>
        <el-button type="primary" @click="onMemberAddSave">{{ $t('dlgSave') }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import UserSelectV2 from '@/components/UserSelectV2'
export default {
  name: 'SpaceMember',
  components: {
    UserSelectV2
  },
  props: {
    spaceId: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      searchFormData: {
        username: '',
        spaceId: '',
        pageIndex: 1,
        pageSize: 20
      },
      pageInfo: {
        list: [],
        total: 0
      },
      memberAddDlgShow: false,
      memberAddFormData: {
        name: '',
        adminId: ''
      },
      memberAddRules: {
        roleCode: [
          { required: true, message: this.$t('pleaseSelect'), trigger: ['blur', 'change'] }
        ]
      }
    }
  },
  watch: {
    spaceId(spaceId) {
      if (spaceId) {
        this.searchFormData.spaceId = spaceId
        this.loadTable()
      }
    }
  },
  methods: {
    loadTable() {
      this.post('/space/member/page', this.searchFormData, resp => {
        this.pageInfo = resp.data
      })
    },
    onSearch() {
      this.searchFormData.pageIndex = 1
      this.loadTable()
    },
    onMemberRemove(row) {
      const data = {
        spaceId: this.spaceId,
        userId: row.id
      }
      this.post('/space/member/remove', data, resp => {
        this.tipSuccess(this.$t('removeSuccess'))
        this.loadTable()
      })
    },
    onMemberAdd() {
      this.memberAddDlgShow = true
      this.$nextTick(() => {
        this.post('/user/list', { }, resp => {
          this.$refs.userSelect.setData(resp.data)
        })
      })
    },
    onRoleChange(row) {
      const data = {
        spaceId: this.spaceId,
        userId: row.id,
        roleCode: row.roleCode
      }
      this.post('/space/member/update', data, () => {
        this.tipSuccess(this.$t('updateSuccess'))
      })
    },
    onMemberAddSave() {
      const promise = this.$refs.userSelect.validate()
      Promise.all([promise]).then(validArr => {
        // 到这里来表示全部内容校验通过
        const userIds = this.$refs.userSelect.getValue()
        const data = Object.assign(this.memberAddFormData, {
          spaceId: this.spaceId,
          userIds: userIds
        })
        this.post('/space/member/add', data, resp => {
          this.tipSuccess(this.$t('addSuccess'))
          this.memberAddDlgShow = false
          this.loadTable()
        })
      }).catch((e) => {
      }) // 加上这个控制台不会报Uncaught (in promise)
    },
    onSizeChange: function(size) {
      this.searchFormData.pageIndex = 1
      this.searchFormData.pageSize = size
      this.loadTable()
    },
    onPageIndexChange: function(pageIndex) {
      this.searchFormData.pageIndex = pageIndex
      this.loadTable()
    },
    onHide() {
      this.resetForm('memberAddForm')
      this.$refs.userSelect.resetForm()
    }
  }
}
</script>

