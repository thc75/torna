<template>
  <div>
    <el-tabs v-model="activeName">
      <el-tab-pane name="pre" :label="$ts('preRequestScript')">
        <div class="table-opt-btn">
          <el-button
            type="primary"
            size="mini"
            @click="onPreAdd"
          >
            {{ $ts('add') }}
          </el-button>
          <span class="split">|</span>
          <span class="tip">{{ $ts('preScriptTip') }}</span>
          <el-link type="primary" :underline="false" @click="openLink('/help?id=debug')">{{ $ts('document') }}</el-link>
        </div>
        <el-table
          v-if="!preEdit"
          :data="preData"
          border
          highlight-current-row
        >
          <el-table-column width="50">
            <template slot-scope="scope">
              <el-checkbox v-model="scope.row.checked" @change="(val) => handlePreTableCurrentChange(scope.row, val)" />
            </template>
          </el-table-column>
          <el-table-column :label="$ts('name')" prop="name" />
          <el-table-column :label="$ts('content')" prop="content" width="100">
            <template slot-scope="scope">
              <el-link type="primary" :underline="false" @click="showScript(scope.row)">查看</el-link>
            </template>
          </el-table-column>
          <el-table-column :label="$ts('canUseScope')" width="100">
            <template slot-scope="scope">
              {{ getScopeName(scope.row) }}
            </template>
          </el-table-column>
          <el-table-column :label="$ts('creator')" prop="creatorName" width="120" />
          <el-table-column
            prop="gmtCreate"
            :label="$ts('createTime')"
            width="110"
          >
            <template slot-scope="scope">
              <span :title="scope.row.gmtCreate">{{ scope.row.gmtCreate.split(' ')[0] }}</span>
            </template>
          </el-table-column>
          <el-table-column
            :label="$ts('operation')"
            width="120"
          >
            <template slot-scope="scope">
              <el-link type="primary" size="mini" @click="onPreScriptUpdate(scope.row)">{{ $ts('update') }}</el-link>
              <el-popconfirm
                :title="$ts('deleteConfirm', scope.row.name)"
                @confirm="onScriptDelete(scope.row)"
              >
                <el-link slot="reference" type="danger" size="mini">{{ $ts('delete') }}</el-link>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
        <div v-else>
          <el-form ref="preFormRef" :model="preForm" :rules="rules" size="mini" label-width="80px">
            <el-form-item :label="$ts('name')" prop="name">
              <el-input v-model="preForm.name" maxlength="50" show-word-limit />
            </el-form-item>
            <el-form-item :label="$ts('canUseScope')">
              <el-select v-model="preForm.scope">
                <el-option v-for="opt in scopeOptions" :key="opt.value" :label="opt.label" :value="opt.value">
                  {{ opt.label }}
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item :label="$ts('content')" prop="content">
              <editor
                v-model="preForm.content"
                lang="javascript"
                theme="chrome"
                height="300"
                class="normal-boarder"
                :options="aceEditorConfig"
                @init="editorInit"
              />
            </el-form-item>
            <el-form-item label-width="0">
              <el-button type="text" @click="preEdit = false">{{ $ts('cancel') }}</el-button>
              <el-button type="primary" @click="onPreSave">{{ $ts('save') }}</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-tab-pane>
      <el-tab-pane name="after" :label="$ts('afterResponseScript')">
        <div class="table-opt-btn">
          <el-button
            type="primary"
            size="mini"
            @click="onAfterAdd"
          >
            {{ $ts('add') }}
          </el-button>
          <span class="split">|</span>
          <span class="tip">{{ $ts('afterScriptTip') }}</span>
          <el-link type="primary" :underline="false" @click="openLink('/help?id=debug')">{{ $ts('document') }}</el-link>
        </div>
        <el-table
          v-if="!afterEdit"
          :data="afterData"
          border
          highlight-current-row
        >
          <el-table-column width="50">
            <template slot-scope="scope">
              <el-checkbox v-model="scope.row.checked" @change="(val) => handleAfterTableCurrentChange(scope.row, val)" />
            </template>
          </el-table-column>
          <el-table-column :label="$ts('name')" prop="name" />
          <el-table-column :label="$ts('content')" prop="content" width="100">
            <template slot-scope="scope">
              <el-link type="primary" :underline="false" @click="showScript(scope.row)">查看</el-link>
            </template>
          </el-table-column>
          <el-table-column :label="$ts('canUseScope')" width="100">
            <template slot-scope="scope">
              {{ getScopeName(scope.row) }}
            </template>
          </el-table-column>
          <el-table-column :label="$ts('creator')" prop="creatorName" width="120" />
          <el-table-column
            prop="gmtCreate"
            :label="$ts('createTime')"
            width="110"
          >
            <template slot-scope="scope">
              <span :title="scope.row.gmtCreate">{{ scope.row.gmtCreate.split(' ')[0] }}</span>
            </template>
          </el-table-column>
          <el-table-column
            :label="$ts('operation')"
            width="120"
          >
            <template slot-scope="scope">
              <el-link type="primary" size="mini" @click="onAfterScriptUpdate(scope.row)">{{ $ts('update') }}</el-link>
              <el-popconfirm
                :title="$ts('deleteConfirm', scope.row.name)"
                @confirm="onScriptDelete(scope.row)"
              >
                <el-link slot="reference" type="danger" size="mini">{{ $ts('delete') }}</el-link>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
        <div v-else>
          <el-form ref="afterFormRef" :model="afterForm" :rules="rules" size="mini" label-width="80px">
            <el-form-item :label="$ts('name')" prop="name">
              <el-input v-model="afterForm.name" maxlength="50" show-word-limit />
            </el-form-item>
            <el-form-item :label="$ts('canUseScope')">
              <el-select v-model="afterForm.scope">
                <el-option v-for="opt in scopeOptions" :key="opt.value" :label="opt.label" :value="opt.value">
                  {{ opt.label }}
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item :label="$ts('content')" prop="content">
              <editor
                v-model="afterForm.content"
                lang="javascript"
                theme="chrome"
                height="300"
                class="normal-boarder"
                :options="aceEditorConfig"
                @init="editorInit"
              />
            </el-form-item>
            <el-form-item label-width="0">
              <el-button type="text" @click="afterEdit = false">{{ $ts('cancel') }}</el-button>
              <el-button type="primary" @click="onAfterSave">{{ $ts('save') }}</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-tab-pane>
    </el-tabs>
    <el-dialog
      :title="showTitle"
      :visible.sync="showVisible"
    >
      <el-input v-model="showContent" type="textarea" :rows="16" readonly />
      <div slot="footer" class="dialog-footer">
        <el-button @click="showVisible = false">{{ $ts('dlgClose') }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import CryptoJS from 'crypto-js'
import moment from 'moment'
import qs from 'qs'
import { RSA } from '@/utils/rsa'
import { loadJs } from '@/utils/loadjs'

function getLib() {
  return {
    CryptoJS: CryptoJS,
    moment: moment,
    qs: qs,
    RSA: RSA,
    loadJs: loadJs
  }
}

export default {
  name: 'DebugScript',
  components: { editor: require('vue2-ace-editor') },
  data() {
    const DEBUG_SCRIPT_SCOPE = this.getEnums().DEBUG_SCRIPT_SCOPE
    return {
      activeName: 'pre',
      scopeOptions: [
        { value: DEBUG_SCRIPT_SCOPE.DOC, label: '当前文档' },
        { value: DEBUG_SCRIPT_SCOPE.MODULE, label: '当前模块' },
        { value: DEBUG_SCRIPT_SCOPE.PROJECT, label: '当前项目' }
      ],
      preData: [],
      preEdit: false,
      preForm: {
        id: '',
        name: '',
        scope: 0,
        content: '',
        description: ''
      },
      preContent: '',
      // after
      afterData: [],
      afterEdit: false,
      afterForm: {
        id: '',
        name: '',
        scope: 0,
        content: '',
        description: ''
      },
      afterContent: '',
      aceEditorConfig: {
        // 去除编辑器里的竖线
        showPrintMargin: false
      },
      docId: '',
      moduleId: '',
      projectId: '',
      rules: {
        name: [
          { required: true, message: $ts('notEmpty'), trigger: ['change'] }
        ],
        content: [
          { required: true, message: $ts('notEmpty'), trigger: ['blur'] }
        ]
      },
      showVisible: false,
      showTitle: '',
      showContent: '',
      preCheckedId: '',
      afterCheckedId: ''
    }
  },
  methods: {
    editorInit: function() {
      // language extension prerequsite...
      require('brace/ext/language_tools')
      // language
      require('brace/mode/json')
      require('brace/mode/javascript')
      require('brace/mode/less')
      require('brace/theme/chrome')
      // snippet
      require('brace/snippets/javascript')
    },
    load(docId, preCheckedId, afterCheckedId) {
      this.docId = docId
      this.preCheckedId = preCheckedId || ''
      this.afterCheckedId = afterCheckedId || ''
      this.loadTable()
    },
    loadTable() {
      if (this.docId) {
        this.get('/doc/debugscript/list', { docId: this.docId }, resp => {
          const data = resp.data
          const preData = data.filter(row => row.type === this.getEnums().DEBUG_SCRIPT_TYPE.PRE)
          const afterData = data.filter(row => row.type === this.getEnums().DEBUG_SCRIPT_TYPE.AFTER)
          preData.forEach(row => {
            row.checked = row.id === this.preCheckedId
          })
          afterData.forEach(row => {
            row.checked = row.id === this.afterCheckedId
          })
          this.preData = preData
          this.afterData = afterData
        })
      }
    },
    onPreAdd() {
      this.preEdit = true
      this.preForm = {
        id: '',
        name: '',
        type: this.getEnums().DEBUG_SCRIPT_TYPE.PRE,
        scope: this.getEnums().DEBUG_SCRIPT_SCOPE.DOC,
        content: '',
        description: ''
      }
    },
    onAfterAdd() {
      this.afterEdit = true
      this.afterForm = {
        id: '',
        name: '',
        type: this.getEnums().DEBUG_SCRIPT_TYPE.AFTER,
        scope: this.getEnums().DEBUG_SCRIPT_SCOPE.DOC,
        content: '',
        description: ''
      }
    },
    handlePreTableCurrentChange(row, val) {
      const data = this.preData
      data.forEach(row => {
        row.checked = false
      })
      row.checked = val
      if (val) {
        this.preCheckedId = row.id
      }
    },
    handleAfterTableCurrentChange(row, val) {
      const data = this.afterData
      data.forEach(row => {
        row.checked = false
      })
      row.checked = val
      if (val) {
        this.afterCheckedId = row.id
      }
    },
    onPreSave() {
      this.$refs.preFormRef.validate(valid => {
        if (valid) {
          const uri = this.preForm.id ? '/doc/debugscript/update' : '/doc/debugscript/add'
          this.preForm.docId = this.docId
          this.post(uri, this.preForm, resp => {
            this.loadTable()
            this.preEdit = false
          })
        }
      })
    },
    onAfterSave() {
      this.$refs.afterFormRef.validate(valid => {
        if (valid) {
          const uri = this.afterForm.id ? '/doc/debugscript/update' : '/doc/debugscript/add'
          this.afterForm.docId = this.docId
          this.post(uri, this.afterForm, resp => {
            this.loadTable()
            this.afterEdit = false
          })
        }
      })
    },
    onPreScriptUpdate(row) {
      this.preEdit = true
      this.preForm = row
    },
    onAfterScriptUpdate(row) {
      this.afterEdit = true
      this.afterForm = row
    },
    onScriptDelete(row) {
      this.post('/doc/debugscript/delete', { id: row.id }, resp => {
        this.tipSuccess($ts('deleteSuccess'))
        this.loadTable()
      })
    },
    showScript(row) {
      this.showTitle = row.name
      this.showContent = row.content
      this.showVisible = true
    },
    getScopeName(row) {
      const scope = row.scope
      for (const key in this.scopeOptions) {
        const config = this.scopeOptions[key]
        if (config.value === scope) {
          return config.label
        }
      }
      return ''
    },
    runPre(req) {
      const data = this.getData()
      const script = data.preContent
      if (!script) {
        return req
      }
      const code = `(function() {
          ${script}
        }())`
      // eslint-disable-next-line no-eval
      // const data = eval(fn)
      const fn = new Function('lib', 'req', `return ${code}`)
      fn(getLib(), req)
      return req
    },
    runAfter(resp, req) {
      const data = this.getData()
      const script = data.afterContent
      if (!script) {
        return resp
      }
      const code = `(function() {
          ${script}
        }())`
      // eslint-disable-next-line no-eval
      // const data = eval(fn)
      const fn = new Function('lib', 'req', 'resp', `return ${code}`)
      fn(getLib(), req, resp)
      return resp
    },
    getData() {
      const preCheckedRow = this.getPreCheckedRow()
      const afterCheckedRow = this.getAfterCheckedRow()
      return {
        preCheckedRow: preCheckedRow,
        preContent: preCheckedRow ? preCheckedRow.content : '',
        afterCheckedRow: afterCheckedRow,
        afterContent: afterCheckedRow ? afterCheckedRow.content : ''
      }
    },
    getPreCheckedRow() {
      const data = this.preData
      for (const row of data) {
        if (row.checked) {
          return row
        }
      }
    },
    getAfterCheckedRow() {
      const data = this.afterData
      for (const row of data) {
        if (row.checked) {
          return row
        }
      }
    },
    getEnable() {
      const data = this.getData()
      return data.preCheckedRow
    }
  }
}
</script>
