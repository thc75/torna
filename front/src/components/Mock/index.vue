<template>
  <div>
    <div>
      <el-button
        type="primary"
        size="mini"
        style="margin-bottom: 10px;margin-right: 10px;"
        @click="onMockAdd"
      >
        {{ $ts('newConfig') }}
      </el-button>
      <el-tooltip placement="top" :content="$ts('helpDoc')">
        <i class="el-icon-question cursor-pointer" @click="openLink('/help?id=mock')"></i>
      </el-tooltip>
    </div>
    <el-tabs
      v-show="isShow"
      v-model="activeMock"
      @tab-click="onTabSelect"
    >
      <el-tab-pane
        v-for="mock in mockConfigs"
        :key="mock.id"
        :label="mock.name"
        :name="mock.id"
      >
      </el-tab-pane>
    </el-tabs>
    <div v-show="isShow" class="mock-content">
      <div class="doc-modify-info">
        <div v-show="isAdded()" class="left-div">
          {{ formData.creatorName }} {{ $ts('createdOn') }} {{ formData.gmtCreate }}，
          {{ formData.modifierName }} {{ $ts('lastModifiedBy') }} {{ formData.gmtModified }}
        </div>
        <div class="right-div">
          <el-tooltip v-show="isAdded()" :content="$ts('copyCurrentConfig')" placement="top">
            <el-link type="primary" icon="el-icon-document-copy" @click="onCopy" />
          </el-tooltip>
          <el-popconfirm
            :title="$ts('deleteConfigConfirm')"
            @confirm="onMockDelete(formData)"
          >
            <el-link slot="reference" type="danger" icon="el-icon-delete" :title="$ts('delete')" />
          </el-popconfirm>
        </div>
      </div>
      <el-form
        ref="mockForm"
        :model="formData"
        :rules="formRules"
        size="mini"
        label-width="120px"
        class="mock-form"
      >
        <el-form-item :label="$ts('mockUrl')">
          <el-input v-model="formData.path" placeholder="path">
            <template slot="prepend">{{ mockBaseUrl }}</template>
          </el-input>
          <span class="tip">
            可在path后面添加query参数区分不同mock，如：{{ 'product/getDetail?id=2' }}
          </span>
        </el-form-item>
        <el-form-item :label="$ts('name')" prop="name">
          <el-input v-model="formData.name" maxlength="64" show-word-limit />
        </el-form-item>
<!--        <el-form-item v-show="!ignoreParam" :label="$ts('param')">
          <el-switch
            v-model="formData.requestDataType"
            :active-text="$ts('jsonType')"
            inactive-text=""
            :active-value="1"
            :inactive-value="0"
          />
          <name-value-table v-if="formData.requestDataType === 0" ref="dataKvRef" :data="formData.dataKv" />
          <editor
            v-if="formData.requestDataType === 1"
            v-model="formData.dataJson"
            lang="json"
            theme="chrome"
            height="200"
            class="normal-boarder"
            :options="aceEditorConfig"
            @init="editorInit"
          />
        </el-form-item>-->
        <el-divider content-position="left">{{ $ts('response') }}</el-divider>
        <el-form-item label="Http Status">
          <el-input-number v-model="formData.httpStatus" controls-position="right" />
        </el-form-item>
        <el-form-item :label="$ts('responseDelay')">
          <el-input-number v-model="formData.delayMills" :min="0" :max="60000" controls-position="right" /> ms
        </el-form-item>
        <el-form-item :label="$ts('responseHeader')">
          <name-value-table
            ref="responseHeadersRef"
            :data="formData.responseHeaders"
            :name-suggest-data="nameSuggestData"
            :value-suggest-data="valueSuggestData"
          />
        </el-form-item>
        <el-form-item :label="$ts('responseContent')">
          <el-switch
            v-model="formData.resultType"
            :active-text="$ts('mockScript')"
            inactive-text=""
            :active-value="1"
            :inactive-value="0"
            style="margin-bottom: 10px;"
          />
          <div v-if="formData.resultType === 0">
            <editor
              v-model="formData.responseBody"
              lang="json"
              theme="chrome"
              height="400"
              class="normal-boarder"
              :options="aceEditorConfig"
              @init="editorInit"
            />
          </div>
          <div v-else>
            <el-button type="success" icon="el-icon-caret-right" style="margin-bottom: 10px;" @click="onRunMockScript">{{ $ts('run') }}</el-button>
            <span class="info-tip">
              {{ $ts('baseOnMockjs') }}，<el-link type="primary" :underline="false" @click="openLink('/help?id=mock')">{{ $ts('helpDoc') }}</el-link>
            </span>
            <editor
              v-model="formData.mockScript"
              lang="javascript"
              theme="chrome"
              height="350"
              class="normal-boarder"
              :options="aceEditorConfig"
              @init="editorInit"
            />
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="medium" @click="onSave">{{ $ts('save') }}</el-button>
        </el-form-item>
      </el-form>
    </div>
    <el-dialog
      :title="$ts('runResult')"
      :visible.sync="mockResultDlgShow"
    >
      <el-alert v-if="mockResultRunResult" :title="$ts('runSuccess')" type="success" :closable="false" />
      <el-alert v-else :title="$ts('runError')" type="error" :closable="false" />
      <el-input
        v-model="mockResultDlgView"
        type="textarea"
        :autosize="{ minRows: 2, maxRows: 15}"
        readonly
        style="margin-top: 20px;"
      />
    </el-dialog>
  </div>
</template>
<script>
import NameValueTable from '@/components/NameValueTable'
import { header_names, header_values } from '@/utils/headers'
const Mock = require('mockjs')
const Random = require('mockjs')

const FORM_DATA = {
  dataKv: [],
  dataJson: '',
  name: '',
  path: '',
  requestDataType: 0,
  httpStatus: 200,
  delayMills: 0,
  responseHeaders: [],
  resultType: 0,
  responseBody: '',
  mockScript: '',
  mockResult: '',
  remark: '',
  creatorName: ''
}

export default {
  name: 'Mock',
  components: { NameValueTable, editor: require('vue2-ace-editor') },
  props: {
    item: {
      type: Object,
      default: () => {}
    }
  },
  data() {
    return {
      activeMock: null,
      activeResult: 'content',
      mockUrl: '',
      responseSuccessExample: '',
      mockConfigs: [],
      formData: Object.assign({}, FORM_DATA),
      formRules: {
        name: [
          { required: true, message: $ts('notEmpty'), trigger: ['blur', 'change'] }
        ],
        path: [
          { required: true, message: $ts('notEmpty'), trigger: ['blur', 'change'] }
        ]
      },
      aceEditorConfig: {
        // 去除编辑器里的竖线
        showPrintMargin: false
      },
      mockResultDlgTitle: $ts('runResult'),
      mockResultDlgView: '',
      mockResultDlgShow: false,
      mockResultRunResult: false,
      ignoreParam: false
    }
  },
  computed: {
    nameSuggestData() {
      return header_names.map(name => {
        return { value: name }
      })
    },
    valueSuggestData() {
      return header_values.map(value => {
        return { value: value }
      })
    },
    isShow() {
      return this.mockConfigs.length > 0
    },
    mockBaseUrl() {
      return `${this.getBaseUrl()}/mock/`
    }
  },
  watch: {
    item(newVal) {
      this.init(newVal)
    }
  },
  mounted() {
    this.pmsConfig().then(config => {
      this.ignoreParam = config.ignoreParam
    })
  },
  methods: {
    init(item) {
      this.loadTab(item)
    },
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
    reload() {
      this.loadTab(this.item)
    },
    loadTab(item) {
      this.get('/doc/mock/list', { docId: item.id }, resp => {
        this.mockConfigs = resp.data
        let find
        const active = this.activeMock
        if (active && active !== '0') {
          find = this.mockConfigs.filter(row => row.id === active)
        }
        if (!find || find.length === 0) {
          find = this.mockConfigs
        }
        if (find.length > 0) {
          this.selectTab(find[0])
        }
      })
    },
    onTabSelect(tab) {
      const index = parseInt(tab.index)
      const node = this.mockConfigs[index]
      this.selectTab(node)
    },
    selectTab(node) {
      if (!node.path) {
        node.path = node.id
      }
      this.formData = node
      this.activeMock = node.id
      this.mockUrl = `${this.getBaseUrl()}/mock/${node.id}`
    },
    runScript(successCall, errorCall) {
      const globalVariable = this.getGlobalVariable()
      const script = this.formData.mockScript
      try {
        const code = `(function() {
          ${script}
        }())`
        // eslint-disable-next-line no-eval
        // const data = eval(fn)
        const fn = new Function('$params', '$body', 'Mock', 'Random', `return ${code}`)
        const data = fn(globalVariable.$params, globalVariable.$body, Mock, Random)
        if (data === undefined) {
          throw new Error($ts('noResultTip'))
        }
        successCall.call(this, data)
        return true
      } catch (e) {
        errorCall.call(this, e)
        return false
      }
    },
    getGlobalVariable() {
      const globalVariable = {}
      const params = {}
      this.formData.dataKv.forEach(row => {
        params[row.name] = row.value
      })
      globalVariable.$params = params
      if (this.formData.dataJson) {
        globalVariable.$body = JSON.parse(this.formData.dataJson)
      }
      return globalVariable
    },
    onRunMockScript() {
      this.runScript(data => {
        this.formData.mockResult = this.formatJson(data)
        this.mockResultDlgTitle = $ts('runResult')
        this.mockResultDlgView = this.formData.mockResult
        this.mockResultRunResult = true
      }, e => {
        this.mockResultRunResult = false
        this.mockResultDlgTitle = $ts('runError')
        this.mockResultDlgView = e
      })
      this.mockResultDlgShow = true
    },
    onMockAdd() {
      const respBody = this.doCreateResponseExample(this.item.responseParams)
      const node = Object.assign({}, FORM_DATA)
      let path = this.item.url
      if (path.startsWith('/')) {
        path = path.substring(1)
      }
      Object.assign(node, {
        id: '' + this.nextId(),
        name: $ts('newConfig'),
        path: path,
        responseBody: this.formatJson(respBody),
        responseHeaders: [
          { name: 'Content-Type', value: 'application/json;charset=UTF-8', isDeleted: 0, isNew: 1 }
        ],
        isNew: true
      })
      this.addMock(node)
    },
    onCopy() {
      const node = {
        isNew: true
      }
      Object.assign(node, this.formData)
      node.id = '' + this.nextId()
      node.name = node.name + ' copy'
      this.addMock(node)
    },
    addMock(node) {
      this.mockConfigs.push(node)
      this.selectTab(node)
    },
    onMockDelete(data) {
      if (data.isNew) {
        this.removeLocal(data)
      } else {
        this.post('/doc/mock/delete', data, resp => {
          this.tipSuccess($ts('deleteSuccess'))
          this.reload()
        })
      }
    },
    removeLocal(data) {
      const index = this.removeRow(this.mockConfigs, data.id)
      if (index !== undefined && this.mockConfigs.length > 0) {
        const nextIndex = index > 0 ? index - 1 : 0
        this.selectTab(this.mockConfigs[nextIndex])
      }
    },
    onSave() {
      this.validate(() => {
        if (this.formData.resultType === 1) {
          const success = this.runScript(data => {
            this.formData.mockResult = this.formatJson(data)
          }, e => {
            this.tipError($ts('mockScriptError'))
          })
          if (!success) {
            return
          }
        }
        this.formatData()
        this.formData.docId = this.item.id
        this.post('/doc/mock/save', this.formData, resp => {
          this.tipSuccess($ts('saveSuccess'))
          this.activeMock = resp.data.id
          this.reload()
        })
      })
    },
    validate(callback) {
      this.$refs.mockForm.validate(valid => {
        if (valid) {
          // 到这里来表示全部内容校验通过
          callback.call(this)
        }
      })
    },
    isAdded() {
      return !this.formData.isNew
    },
    formatData() {
      const filter = row => {
        return row.isDeleted === undefined || row.isDeleted === 0
      }
      // this.formData.dataKv = this.formData.dataKv.filter(filter)
      this.formData.responseHeaders = this.formData.responseHeaders.filter(filter)
    }
  }
}
</script>
<style scoped>
  .mock-content {
    width: 800px;
  }
  .mock-form {
    padding-top: 40px;
  }
</style>
