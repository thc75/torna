<template>
  <div class="app-container">
    <el-form :inline="true" :model="searchFormData" size="mini">
      <el-form-item :label="$t('releaseNo')">
        <el-input v-model="searchFormData.releaseNo" :clearable="true" style="width: 250px;" />
      </el-form-item>
      <el-form-item :label="$t('status')">
        <el-select v-model="searchFormData.status" clearable>
          <el-option v-for="item in getStatusCodeConfig()" :key="item.code" :value="item.code" :label="item.label">
            {{ item.label }}
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="loadTable">{{ $t('search') }}</el-button>
      </el-form-item>
    </el-form>
    <el-button
      v-if="hasRole(`project:${projectId}`, [Role.admin, Role.dev])"
      type="primary"
      size="mini"
      icon="el-icon-plus"
      style="margin-bottom: 10px;"
      @click="onReleaseAdd"
    >
      {{ $t('addRelease') }}
    </el-button>
    <el-table
      :data="releaseData"
      border
      highlight-current-row
    >
      <el-table-column
        prop="releaseNo"
        :label="$t('releaseNo')"
        width="100"
      />
      <el-table-column
        prop="releaseDesc"
        :label="$t('releaseDesc')"
        width="300"
      />
      <el-table-column
        prop="status"
        :label="$t('status')"
        width="80"
      >
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.status"
            :active-value="1"
            :inactive-value="0"
            :disabled="!hasRole(`project:${projectId}`, [Role.admin, Role.dev])"
            @change="onStatusChange(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column
        prop="dingdingWebhook"
        :label="$t('DingDingSetting.dingdingWebhookUrl')"
        width="400"
      />
      <el-table-column
        prop="weComWebhook"
        :label="$t('WeComSetting.weComWebhookUrl')"
        width="400"
      />
      <el-table-column
        prop="gmtCreate"
        :label="$t('joinTime')"
        width="180"
      />
      <el-table-column
        :label="$t('operation')"
        width="300"
      >
        <template slot-scope="scope">
          <el-link type="primary" @click="onSubscribe(scope.row) ">
            {{ scope.row.isSubscribe ? $t('cancelSubscribe') : $t('clickSubscribe') }}
            <i :class="scope.row.isSubscribe ? 'el-icon-star-on' : 'el-icon-star-off'" />
          </el-link>
          <el-link type="primary" @click="bindDocs(scope.row)">{{ $t('viewAssociatedDocuments') }}</el-link>
          <el-link v-if="hasRole(`project:${projectId}`, [Role.admin, Role.dev])" type="primary" @click="onReleaseUpdate(scope.row)">{{ $t('update') }}</el-link>
          <el-popconfirm
            :title="$t('removeConfirm', scope.row.releaseNo)"
            @confirm="onReleaseRemove(scope.row)"
          >
            <el-link v-if="hasRole(`project:${projectId}`, [Role.admin, Role.dev])" slot="reference" type="danger">{{ $t('remove') }}</el-link>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <!-- dialog   -->
    <el-dialog
      v-if="hasRole(`project:${projectId}`, [Role.admin, Role.dev])"
      :title="dialogTitle"
      :close-on-click-modal="false"
      :visible.sync="releaseDlgShow"
      @close="onHide"
    >
      <el-form
        ref="releaseAddForm"
        :model="releaseAddFormData"
        :rules="releaseAddRules"
        size="mini"
        style="width: 600px;"
        label-width="150px"
      >
        <el-form-item :label="$t('releaseNo')" required>
          <el-input v-model="releaseAddFormData.releaseNo" :disabled="operator === 'Update'" />
        </el-form-item>
        <el-form-item :label="$t('releaseDesc')">
          <el-input v-model="releaseAddFormData.releaseDesc" />
        </el-form-item>
        <el-form-item :label="$t('status')" required>
          <el-switch v-model="releaseAddFormData.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item :label="$t('DingDingSetting.dingdingWebhookUrl')">
          <el-input v-model="releaseAddFormData.dingdingWebhook" />
        </el-form-item>
        <el-form-item :label="$t('WeComSetting.weComWebhookUrl')">
          <el-input v-model="releaseAddFormData.weComWebhook" />
        </el-form-item>
        <el-form-item>
          <module-doc-tree
            v-if="releaseDlgShow"
            ref="docTreeRef"
            :module-data="moduleData"
            :module-source-id-map="releaseAddFormData.moduleSourceIdMap"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="releaseDlgShow = false">{{ $t('dlgCancel') }}</el-button>
        <el-button type="primary" @click="onReleaseSave">{{ $t('dlgSave') }}</el-button>
      </div>
    </el-dialog>
    <!-- query bind doc dialog   -->
    <el-dialog
      :title="$t('associatedDocument')"
      :close-on-click-modal="false"
      :visible.sync="bindDocDlgShow"
    >
      <el-tree
        :data="releaseDocData"
        :default-expand-all="true"
      >
        <span slot-scope="{ node, data }" class="custom-tree-node">
          <span>{{ node.label }}</span>
          <el-link v-if="!isFolder(data)" type="success" icon="el-icon-view" :title="$t('preview')" :underline="false" @click="openLink(getViewUrl(data))" />
        </span>
      </el-tree>
    </el-dialog>
  </div>
</template>
<script>

import ModuleDocTree from '@/components/ModuleDocTree'

export default {
  name: 'ProjectRelease',
  components: { ModuleDocTree },
  props: {
    projectId: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      dialogTitle: '',
      operator: '',
      searchFormData: {
        releaseNo: '',
        status: '',
        projectId: ''
      },
      releaseData: [],
      releaseDlgShow: false,
      bindDocDlgShow: false,
      releaseAddFormData: {
        id: '',
        releaseNo: '',
        releaseDesc: '',
        status: 0,
        dingdingWebhook: '',
        weComWebhook: '',
        moduleSourceIdMap: {}
      },
      releaseAddRules: {
        roleCode: [
          { required: true, message: this.$t('pleaseSelect'), trigger: ['blur', 'change'] }
        ]
      },
      moduleData: [],
      releaseDocData: []
    }
  },
  computed: {

  },
  watch: {
    projectId(projectId) {
      if (projectId) {
        this.searchFormData.projectId = projectId
        this.loadTable()
      }
    }
  },
  created() {

  },
  mounted() {
    if (this.projectId) {
      this.setProjectId(this.projectId)
    }
  },
  methods: {
    // 刷新数据表
    loadTable() {
      this.get('/project/release/list', this.searchFormData, resp => {
        this.releaseData = resp.data
      })
    },
    // 点击状态变更
    onStatusChange(row) {
      const data = {
        id: row.id,
        status: row.status
      }
      this.post('/project/release/status', data, () => {
        this.tipSuccess(this.$t('updateSuccess'))
      })
    },
    // 删除
    onReleaseRemove(row) {
      const data = {
        id: row.id
      }
      this.post('/project/release/remove', data, resp => {
        this.tipSuccess(this.$t('removeSuccess'))
        this.loadTable()
      })
    },
    // 点击新增弹窗
    onReleaseAdd() {
      this.operator = 'Add'
      this.dialogTitle = this.$t('addRelease')
      // 重置表单
      this.releaseAddFormData = {
        id: '',
        releaseNo: '',
        releaseDesc: '',
        status: 0,
        dingdingWebhook: '',
        weComWebhook: '',
        moduleSourceIdMap: {} // 选中的模块稿件map
      }
      this.loadModule(this.projectId, function() {
        this.releaseDlgShow = true
      })
    },
    // 点击编辑弹窗
    onReleaseUpdate(row) {
      this.operator = 'Update'
      this.dialogTitle = this.$t('updateRelease')
      this.$nextTick(() => {
        Object.assign(this.releaseAddFormData, row)
      })
      this.loadModule(this.projectId, function() {
        this.releaseDlgShow = true
      })
    },
    // 新建、修改触发接口
    onReleaseSave() {
      const promiseMain = this.$refs.releaseAddForm.validate()
      // releaseAddFormData
      Promise.all([promiseMain]).then(validArr => {
        // 获取所有应用模块选中的文档id  Map<String, Array<String>>
        this.releaseAddFormData.moduleSourceIdMap = this.$refs.docTreeRef.getModuleSourceIdMap()
        // 到这里来表示全部内容校验通过
        Object.assign(this.releaseAddFormData, {
          projectId: this.projectId
        })
        console.info('release save form', this.releaseAddFormData)
        const isUpdate = this.releaseAddFormData.id && this.releaseAddFormData.id.length > 0
        if (isUpdate) {
          // 修改操作
          this.post('/project/release/update', this.releaseAddFormData, resp => {
            this.tipSuccess($t('updateSuccess'))
            // 关闭弹窗，刷新列表
            this.releaseDlgShow = false
            this.loadTable()
          })
        } else {
          // 新增操作
          this.post('/project/release/add', this.releaseAddFormData, resp => {
            this.tipSuccess($t('addSuccess'))
            // 关闭弹窗，刷新列表
            this.releaseDlgShow = false
            this.loadTable()
          })
        }
      }).catch((e) => {
      }) // 加上这个控制台不会报Uncaught (in promise)
    },
    // map合并value成数组
    // mergeModulesKeys(modulesKeys) {
    //   const mergedSet = new Set()
    //   for (const ids of Object.values(modulesKeys)) {
    //     ids.forEach(id => mergedSet.add(id))
    //   }
    //   return Array.from(mergedSet)
    // },
    onHide() {
      this.resetForm('releaseAddForm')
    },
    // 加载应用模块
    loadModule(projectId, callback) {
      if (projectId) {
        this.get('/module/list', { projectId: projectId }, function(resp) {
          this.moduleData = resp.data
          callback && callback.call(this)
        })
      }
    },
    // 订阅/取消订阅
    onSubscribe(row) {
      if (!row.isSubscribe) {
        this.post('/project/release/doc/subscribe', { sourceId: row.id }, resp => {
          this.tipSuccess($t('subscribeSuccess'))
          row.isSubscribe = true
        })
      } else {
        this.post('/project/release/doc/cancelSubscribe', { sourceId: row.id }, resp => {
          this.tipSuccess($t('unsubscribeSuccess'))
          row.isSubscribe = false
        })
      }
    },
    // 关联文档
    bindDocs(row, callback) {
      this.post('/project/release/bind/list', {
        'projectId': row.projectId,
        'releaseId': row.id
      }, function(resp) {
        this.releaseDocData = resp.data
        this.bindDocDlgShow = true
        callback && callback.call(this)
      })
    },
    isFolder(row) {
      return row.isFolder === 1
    },
    getViewUrl(row) {
      return `/view/${row.id}`
    }
  }
}
</script>

