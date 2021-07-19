<template>
  <div style="padding: 20px">
    <h4>网关URL<span class="info-tip">所有接口显示同一个网关URL，自带URL将隐藏</span></h4>
    <div>
      <el-input v-model="setting.gatewayUrl" size="mini" style="width: 500px" />
    </div>
    <h4>接口调试</h4>
    <el-switch
      v-model="setting.showDebug"
      :active-value="1"
      :inactive-value="0"
      active-text="允许"
    />
    <br />
    <el-button type="primary" style="margin-top: 30px;" @click="onSettingSave">保存</el-button>
    <el-divider />
    <h4>公共参数</h4>
    <el-tabs active-name="commonRequestParam" @tab-click="onCommonTabChange">
      <el-tab-pane :label="$ts('commonRequest')" name="commonRequestParam">
        <common-param
          ref="commonRequestParamRef"
          list-url="/compose/project/setting/globalParams/list"
          add-url="/compose/project/setting/globalParams/add"
          update-url="/compose/project/setting/globalParams/update"
          delete-url="/compose/project/setting/globalParams/delete"
        />
      </el-tab-pane>
      <el-tab-pane :label="$ts('commonResponse')" name="commonResponseParam">
        <common-param
          ref="commonResponseParamRef"
          list-url="/compose/project/setting/globalReturns/list"
          add-url="/compose/project/setting/globalReturns/add"
          update-url="/compose/project/setting/globalReturns/update"
          delete-url="/compose/project/setting/globalReturns/delete"
        />
      </el-tab-pane>
    </el-tabs>
    <h4>文档附加页<span class="info-tip">可添加文档附加信息，如签名算法说明，状态码列表等</span></h4>
    <el-button
      type="primary"
      size="mini"
      style="margin-bottom: 10px"
      @click="onExtDocAdd"
    >
      {{ $ts('add') }}
    </el-button>
    <el-table
      :data="extPageData"
      border
      highlight-current-row
    >
      <el-table-column label="标题" prop="title" />
      <el-table-column
        prop="gmtCreate"
        :label="$ts('createTime')"
        width="110"
      >
        <template slot-scope="scope">
          <time-tooltip :time="scope.row.gmtCreate" />
        </template>
      </el-table-column>
      <el-table-column
        :label="$ts('operation')"
        :width="$width(200, { 'en': 240 })"
      >
        <template slot-scope="scope">
          <el-link type="primary" size="mini" @click="onExtDocUpdate(scope.row)">{{ $ts('update') }}</el-link>
          <el-popconfirm
            :title="$ts('deleteConfirm', scope.row.name)"
            @confirm="onExtDocDelete(scope.row)"
          >
            <el-link slot="reference" type="danger" size="mini">{{ $ts('delete') }}</el-link>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <!--dialog-->
    <el-dialog
      :title="dialogExtTitle"
      :visible.sync="dialogExtVisible"
      :close-on-click-modal="false"
      @close="resetForm('dialogForm')"
    >
      <el-form
        ref="dialogForm"
        :rules="dialogFormRules"
        :model="dialogExtFormData"
        label-width="120px"
        size="mini"
      >
        <el-form-item
          prop="title"
          label="文档标题"
        >
          <el-input v-model="dialogExtFormData.title" />
        </el-form-item>
        <el-form-item
          prop="content"
          label="文档内容"
        >
          <mavon-editor
            v-model="dialogExtFormData.content"
            :boxShadow="false"
            :subfield="false"
            :editable="true"
            :toolbarsFlag="true"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogExtVisible = false">取 消</el-button>
        <el-button type="primary" @click="onDialogSave">保 存</el-button>
      </div>
    </el-dialog>
    <el-dialog
      :title="extViewTitle"
      :visible.sync="extViewShow"
    >
      <div style="max-height: 400px;overflow-y: auto">
        <mavon-editor
          v-model="extViewContent"
          :boxShadow="false"
          :subfield="false"
          defaultOpen="preview"
          :editable="false"
          :toolbarsFlag="false"
        />
      </div>
    </el-dialog>
  </div>
</template>
<script>
import TimeTooltip from '@/components/TimeTooltip'
import CommonParam from './CommonParam'
import { mavonEditor } from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'
export default {
  components: { CommonParam, mavonEditor, TimeTooltip },
  props: {
    projectId: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      setting: {
        gatewayUrl: '',
        showDebug: 0
      },
      extPageData: [],
      extViewTitle: '',
      extViewShow: false,
      extViewContent: '',
      dialogExtVisible: false,
      dialogExtTitle: '',
      dialogExtFormData: {
        id: '',
        projectId: '', title: '', content: ''
      },
      dialogFormRules: {
        title: [
          { required: true, message: '不能为空', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    projectId(val) {
      this.loadSetting(val)
      this.$refs.commonRequestParamRef.reload(val)
    }
  },
  methods: {
    loadSetting(projectId) {
      this.get('/compose/project/setting/get', { id: projectId }, resp => {
        this.setting = resp.data
      })
      this.loadExtTable()
    },
    loadExtTable() {
      this.get('/compose/additional/list', { projectId: this.projectId }, resp => {
        this.extPageData = resp.data
      })
    },
    onSettingSave() {
      this.setting.id = this.projectId
      this.post('/compose/project/setting/save', this.setting, resp => {
        this.tipSuccess('修改成功')
      })
    },
    onCommonTabChange(tab) {
      this.$refs[`${tab.name}Ref`].reload(this.projectId)
    },
    onExtDocContentView(row) {
      this.get('/compose/additional/get', { id: row.id }, resp => {
        const data = resp.data
        this.extViewTitle = data.title
        this.extViewContent = data.content
        this.extViewShow = true
      })
    },
    onExtDocDelete(row) {
      this.post('/compose/additional/get', { id: row.id }, resp => {
        this.tipSuccess('删除成功')
        this.loadExtTable()
      })
    },
    onExtDocAdd() {
      this.dialogExtTitle = '新增'
      this.dialogExtVisible = true
      this.dialogExtFormData.id = 0
    },
    onExtDocUpdate(row) {
      this.dialogExtTitle = '修改'
      this.dialogExtVisible = true
      this.$nextTick(() => {
        Object.assign(this.dialogExtFormData, row)
      })
    },
    onDialogSave() {
      this.$refs.dialogForm.validate((valid) => {
        if (valid) {
          const uri = this.dialogExtFormData.id ? '/compose/additional/update' : '/compose/additional/add'
          this.post(uri, this.dialogExtFormData, () => {
            this.dialogExtVisible = false
            this.loadExtTable()
          })
        }
      })
    }
  }
}
</script>
