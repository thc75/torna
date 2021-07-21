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
        prop="content"
        label="查看"
        width="80"
      >
        <template slot-scope="scope">
          <el-link type="primary" @click="onExtDocContentView(scope.row)">查看</el-link>
        </template>
      </el-table-column>
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
          <el-popconfirm
            v-if="scope.row.status === 1"
            :title="$ts('disableConfirm', scope.row.title)"
            @confirm="onExtDocUpdateStatus(scope.row, 0)"
          >
            <el-link slot="reference" :underline="false" type="danger">{{ $ts('disable') }}</el-link>
          </el-popconfirm>
          <el-popconfirm
            v-if="scope.row.status === 0"
            :title="$ts('enableConfirm', scope.row.title)"
            @confirm="onExtDocUpdateStatus(scope.row, 1)"
          >
            <el-link slot="reference" :underline="false" type="primary">{{ $ts('enable') }}</el-link>
          </el-popconfirm>
          <el-link type="primary" size="mini" @click="onExtDocUpdate(scope.row)">{{ $ts('update') }}</el-link>
          <el-popconfirm
            :title="$ts('deleteConfirm', scope.row.title)"
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
        label-position="top"
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
          label="文档内容(markdown)"
        >
          <mavon-editor
            v-model="dialogExtFormData.content"
            :boxShadow="false"
            :scrollStyle="true"
            :subfield="false"
            :toolbars="toolbars"
            :style="editorStyle"
            @fullScreen="onFullScreen"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogExtVisible = false">取 消</el-button>
        <el-button type="primary" @click="onExtDialogSave">保 存</el-button>
      </div>
    </el-dialog>
    <el-dialog
      :title="extViewTitle"
      :visible.sync="extViewShow"
      fullscreen
    >
      <mavon-editor
        v-model="extViewContent"
        :boxShadow="false"
        :subfield="false"
        defaultOpen="preview"
        :editable="false"
        :toolbarsFlag="false"
      />
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
        projectId: '',
        title: '',
        content: ''
      },
      dialogFormRules: {
        title: [
          { required: true, message: '不能为空', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '不能为空', trigger: 'blur' }
        ]
      },
      editorStyle: 'max-height: 400px',
      toolbars: {
        bold: true, // 粗体
        italic: true, // 斜体
        header: true, // 标题
        underline: true, // 下划线
        strikethrough: true, // 中划线
        mark: true, // 标记
        superscript: true, // 上角标
        subscript: true, // 下角标
        quote: true, // 引用
        ol: true, // 有序列表
        ul: true, // 无序列表
        link: true, // 链接
        imagelink: true, // 图片链接
        code: true, // code
        table: true, // 表格
        fullscreen: true, // 全屏编辑
        readmodel: false, // 沉浸式阅读
        htmlcode: false, // 展示html源码
        help: true, // 帮助
        /* 1.3.5 */
        undo: true, // 上一步
        redo: true, // 下一步
        trash: true, // 清空
        save: false, // 保存（触发events中的save事件）
        /* 1.4.2 */
        navigation: true, // 导航目录
        /* 2.1.8 */
        alignleft: true, // 左对齐
        aligncenter: true, // 居中
        alignright: true, // 右对齐
        /* 2.2.1 */
        subfield: true, // 单双栏模式
        preview: true // 预览
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
      this.post('/compose/additional/delete', { id: row.id }, resp => {
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
      this.get('/compose/additional/get', { id: row.id }, resp => {
        const data = resp.data
        this.dialogExtTitle = '修改'
        this.dialogExtVisible = true
        this.$nextTick(() => {
          Object.assign(this.dialogExtFormData, data)
        })
      })
    },
    onExtDocUpdateStatus(row, status) {
      this.post('/compose/additional/status/update', { id: row.id, status: status }, resp => {
        this.tipSuccess(this.$ts('operateSuccess'))
        this.loadExtTable()
      })
    },
    onExtDialogSave() {
      this.$refs.dialogForm.validate((valid) => {
        if (valid) {
          const uri = this.dialogExtFormData.id ? '/compose/additional/update' : '/compose/additional/add'
          this.dialogExtFormData.projectId = this.projectId
          this.post(uri, this.dialogExtFormData, () => {
            this.dialogExtVisible = false
            this.loadExtTable()
          })
        }
      })
    },
    onFullScreen(status) {
      if (status) {
        this.editorStyle = ''
      } else {
        this.editorStyle = 'max-height: 400px'
      }
    }
  }
}
</script>
