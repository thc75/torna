<template>
  <div class="doc-debug">
    <el-row :gutter="20">
      <el-col :span="24-rightSpanSize">
        <div v-if="currentItem.debugEnvs.length > 0">
          <el-radio-group v-if="currentItem.debugEnvs.length > 0" v-model="debugId" size="mini" style="margin-bottom: 4px;" @change="changeHostEnv">
            <el-radio-button
              v-for="hostConfig in currentItem.debugEnvs"
              :key="hostConfig.id"
              :label="hostConfig.id"
            >
              {{ hostConfig.name }}
            </el-radio-button>
          </el-radio-group>
          <span class="split">|</span>
          <el-checkbox v-model="isProxy" :label="$t('proxyForward')" @change="saveProxySelect" />
          <el-popover
            placement="right"
            :title="$t('proxyForward')"
            width="400"
            :open-delay="500"
            trigger="hover"
          >
            <p>{{ $t('proxyForwardOn') }}</p>
            <p>{{ $t('proxyForwardOff') }}</p>
            <i slot="reference" class="el-icon-question"></i>
          </el-popover>
          <el-input v-model="requestUrl" :readonly="pathData.length > 0" class="request-url">
            <span slot="prepend">
              {{ currentMethod }}
            </span>
            <el-button slot="append" :loading="sendLoading" class="btn-send" @click="send">{{ $t('debugSend') }}</el-button>
          </el-input>
        </div>
        <el-alert v-else :closable="false">
          <span v-if="internal" slot="title">
            {{ $t('noDebugEvnTip1') }}
            【<router-link class="el-link el-link--primary" :to="getProjectHomeUrl(currentItem.projectId, 'id=ModuleSetting')">{{ $t('moduleSetting') }}</router-link>】
            {{ $t('noDebugEvnTip2') }}
            <el-link type="primary" :underline="false" @click="openLink('/help?id=debug')">{{ $t('referenceDoc') }}</el-link>
          </span>
          <span v-else>
            {{ $t('noDebugEvnTip3') }}
          </span>
        </el-alert>
        <div v-show="pathData.length > 0" class="path-param">
          <el-table
            :data="pathData"
            border
            :header-cell-style="cellStyle"
            :cell-style="cellStyle"
          >
            <el-table-column
              prop="name"
              :label="$t('pathVariable')"
              width="300"
            />
            <el-table-column :label="$t('value')">
              <template slot-scope="scope">
                <el-form :model="scope.row" size="mini">
                  <el-form-item label-width="0">
                    <div v-if="scope.row.type === 'enum'">
                      <el-select v-model="scope.row.example">
                        <el-option v-for="val in scope.row.enums" :key="val" :value="val" :label="val"></el-option>
                      </el-select>
                    </div>
                    <div v-else>
                      <el-input v-model="scope.row.example" />
                    </div>
                  </el-form-item>
                </el-form>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <el-collapse v-model="collapseActive" accordion style="margin-top: 10px;">
          <el-collapse-item title="Headers" name="header">
            <span slot="title" class="result-header-label">
              <span>Headers <span class="param-count">({{ headerData.length }})</span></span>
            </span>
            <div>
              <el-link type="primary" :underline="false" @click="onTempHeaderAdd">{{ $t('add') + 'Header' }}</el-link>
              <el-table
                ref="headerDataRef"
                :data="headerData"
                border
                :empty-text="$t('noHeader')"
                @selection-change="handleHeaderSelectionChange"
              >
                <el-table-column type="selection" width="50" />
                <el-table-column label="Name" prop="name" width="300px">
                  <template slot-scope="scope">
                    <el-form v-if="scope.row.temp === 1" :model="scope.row" size="mini">
                      <el-form-item label-width="0">
                        <el-input v-model="scope.row.name" />
                      </el-form-item>
                    </el-form>
                    <span v-else>{{ scope.row.name }}</span>
                  </template>
                </el-table-column>
                <el-table-column label="Value">
                  <template slot-scope="scope">
                    <el-form :model="scope.row" size="mini">
                      <el-form-item label-width="0">
                        <el-input v-model="scope.row.example" />
                      </el-form-item>
                    </el-form>
                  </template>
                </el-table-column>
                <el-table-column :label="$t('operation')" width="100">
                  <template slot-scope="scope">
                    <el-link
                      v-show="scope.row.temp === 1"
                      type="danger"
                      icon="el-icon-delete"
                      @click="removeTableRow(headerData, scope.row)"
                    />
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </el-collapse-item>
        </el-collapse>
        <el-tabs v-model="requestActive" type="card" style="margin-top: 10px">
          <el-tab-pane name="query">
            <span slot="label" class="result-header-label">
              <span>Query Parameter <span class="param-count">({{ queryData.length }})</span></span>
            </span>
            <el-link type="primary" :underline="false" style="margin-bottom: 5px" @click="onTempQueryAdd">{{ $t('add') + $t('param') }}</el-link>
            <el-table
              ref="queryDataRef"
              :data="queryData"
              border
              @selection-change="handleQuerySelectionChange"
            >
              <el-table-column type="selection" width="50" />
              <el-table-column
                prop="name"
                :label="$t('paramName')"
                width="300"
              >
                <template slot-scope="scope">
                  <el-form v-if="scope.row.temp === 1" :model="scope.row" size="mini">
                    <el-form-item label-width="0">
                      <el-input v-model="scope.row.name" />
                    </el-form-item>
                  </el-form>
                  <span v-else>{{ scope.row.name }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('value')">
                <template slot-scope="scope">
                  <el-form :model="scope.row" size="mini">
                    <el-form-item label-width="0">
                      <div v-if="scope.row.enumInfo">
                        <enum-select :row="scope.row" />
                      </div>
                      <div v-else>
                        <el-input v-model="scope.row.example" />
                      </div>
                    </el-form-item>
                  </el-form>
                </template>
              </el-table-column>
              <el-table-column :label="$t('operation')" width="100">
                <template slot-scope="scope">
                  <el-link
                    v-show="scope.row.temp === 1"
                    type="danger"
                    icon="el-icon-delete"
                    @click="removeTableRow(queryData, scope.row)"
                  />
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
          <el-tab-pane name="body">
            <span slot="label" class="result-header-label">
              <el-badge :is-dot="hasBody" type="danger">
                <span>Body Parameter</span>
              </el-badge>
            </span>
            <el-radio-group v-model="postActive" size="small" style="margin-bottom: 20px;">
              <el-radio-button label="text" class="json-badge">Text</el-radio-button>
              <el-radio-button label="form">x-www-form-urlencoded <span class="param-count">({{ formData.length }})</span></el-radio-button>
              <el-radio-button label="multipart">multipart <span class="param-count">({{ multipartData.length }})</span></el-radio-button>
            </el-radio-group>
            <div v-show="showBody('text')">
              <el-radio-group v-model="textRadio" style="margin-bottom: 10px;">
                <el-radio label="application/json">JSON</el-radio>
                <el-radio label="text/plain">Text</el-radio>
                <el-radio label="application/xml">XML</el-radio>
                <el-radio label="text/html">HTML</el-radio>
                <el-radio label="application/x-javascript">JavaScript</el-radio>
              </el-radio-group>
              <el-form>
                <el-form-item label-width="0">
                  <editor
                    v-if="textRadio === 'application/json'"
                    ref="requestEditor"
                    v-model="bodyText"
                    lang="json"
                    theme="chrome"
                    :height="requestEditorConfig.height"
                    class="normal-boarder"
                    :options="requestEditorConfig"
                    @init="requestEditorInit"
                  />
                  <el-input v-else v-model="bodyText" type="textarea" :autosize="{ minRows: 2, maxRows: 100}" @blur="onBodyBlur" />
                </el-form-item>
              </el-form>
            </div>
            <div v-show="showBody('form')">
              <el-link type="primary" :underline="false" style="margin-bottom: 5px" @click="onTempFormAdd">{{ $t('add') + $t('param') }}</el-link>
              <el-table
                ref="formDataRef"
                :data="formData"
                border
                @selection-change="handleFormSelectionChange"
              >
                <el-table-column type="selection" width="50" />
                <el-table-column
                  prop="name"
                  :label="$t('paramName')"
                  width="300"
                >
                  <template slot-scope="scope">
                    <el-form v-if="scope.row.temp === 1" :model="scope.row" size="mini">
                      <el-form-item label-width="0">
                        <el-input v-model="scope.row.name" />
                      </el-form-item>
                    </el-form>
                    <span v-else>{{ scope.row.name }}</span>
                  </template>
                </el-table-column>
                <el-table-column :label="$t('value')">
                  <template slot-scope="scope">
                    <el-form :model="scope.row" size="mini">
                      <el-form-item label-width="0">
                        <div v-if="scope.row.enumInfo">
                          <enum-select :row="scope.row" />
                        </div>
                        <div v-else>
                          <el-input v-model="scope.row.example" />
                        </div>
                      </el-form-item>
                    </el-form>
                  </template>
                </el-table-column>
                <el-table-column :label="$t('operation')" width="100">
                  <template slot-scope="scope">
                    <el-link
                      v-show="scope.row.temp === 1"
                      type="danger"
                      icon="el-icon-delete"
                      @click="removeTableRow(formData, scope.row)"
                    />
                  </template>
                </el-table-column>
              </el-table>
            </div>
            <div v-show="showBody('multipart')">
              <el-upload
                action=""
                :multiple="true"
                :auto-upload="false"
                style="width: 500px;margin-bottom: 10px"
                :on-remove="(file, fileList) => onSelectMultiFile(file, fileList)"
                :on-change="(file, fileList) => onSelectMultiFile(file, fileList)"
              >
                <el-button slot="trigger" type="primary" size="mini">{{ $t('uploadMultiFiles') }}</el-button>
              </el-upload>
              <el-link type="primary" :underline="false" style="margin-bottom: 5px" @click="onTempMultipartAdd">{{ $t('add') + $t('param') }}</el-link>
              <el-table
                v-show="showBody('multipart')"
                ref="multipartDataRef"
                :data="multipartData"
                border
                @selection-change="handleMultipartSelectionChange"
              >
                <el-table-column type="selection" width="50" />
                <el-table-column
                  prop="name"
                  :label="$t('paramName')"
                  width="300"
                >
                  <template slot-scope="scope">
                    <el-form v-if="scope.row.temp === 1" :model="scope.row" size="mini">
                      <el-form-item label-width="0">
                        <el-input v-model="scope.row.name" />
                      </el-form-item>
                    </el-form>
                    <span v-else>{{ scope.row.name }}</span>
                  </template>
                </el-table-column>
                <el-table-column :label="$t('value')">
                  <template slot-scope="scope">
                    <el-form :model="scope.row" size="mini">
                      <el-form-item label-width="0" style="margin-bottom: 0">
                        <el-upload
                          v-if="isFileParam(scope.row)"
                          action=""
                          :multiple="scope.row.type === 'file[]'"
                          :auto-upload="false"
                          :on-change="(file, fileList) => onSelectFile(file, fileList, scope.row)"
                          :on-remove="(file, fileList) => onSelectFile(file, fileList, scope.row)"
                        >
                          <el-button slot="trigger" class="choose-file" type="primary">{{ $t('chooseFile') }}</el-button>
                        </el-upload>
                        <div v-else-if="scope.row.enumInfo">
                          <enum-select :row="scope.row" />
                        </div>
                        <div v-else>
                          <el-input v-model="scope.row.example" />
                        </div>
                      </el-form-item>
                    </el-form>
                  </template>
                </el-table-column>
                <el-table-column :label="$t('operation')" width="100">
                  <template slot-scope="scope">
                    <el-link
                      v-show="scope.row.temp === 1"
                      type="danger"
                      icon="el-icon-delete"
                      @click="removeTableRow(multipartData, scope.row)"
                    />
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </el-tab-pane>
        </el-tabs>
      </el-col>
      <el-col :span="rightSpanSize" style="border-left: 1px #E4E7ED solid;">
        <div class="result-status">
          Status: <el-tag :type="result.status >= 200 && result.status < 300 ? 'success' : 'danger'">{{ result.status }}</el-tag>
        </div>
        <el-tabs v-model="resultActive" type="card">
          <el-tab-pane :label="$t('returnResult')" name="body">
            <img v-if="result.image.length > 0" :src="result.image" />
            <el-input v-else v-model="result.content" type="textarea" :readonly="true" style="font-size: 13px;" :autosize="{ minRows: 2, maxRows: 200}" />
          </el-tab-pane>
          <el-tab-pane label="Headers" name="headers">
            <span slot="label" class="result-header-label">
              <span>Headers <span class="param-count">({{ result.headerData.length }})</span></span>
            </span>
            <el-table
              :data="result.headerData"
            >
              <el-table-column label="Name" prop="name" />
              <el-table-column label="Value" prop="value" />
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </el-col>
    </el-row>
  </div>
</template>
<style>
.cell .choose-file {padding: 5px;}
.doc-debug .cell .el-form-item {margin-bottom: 0;}
.result-header-label {font-size: 14px;}
.result-header-label .el-badge__content.is-fixed {top: 10px;right: 0; }
.json-badge .el-badge__content.is-fixed {top: 0;right: -3px; }
.param-count {color: #909399;}
.el-radio-group .el-badge {vertical-align: baseline;}
.el-radio-group .is-active .param-count {color: #fff;}
.result-status {margin-bottom: 12px; font-size: 13px;color: #606266;}
.path-param {margin-top: 5px;}
</style>
<script>
require('fast-text-encoding')
const xmlFormatter = require('xml-formatter')
import { get_full_url, request } from '@/utils/http'
import { get_effective_url, is_array_string, parse_root_array } from '@/utils/common'
import EnumSelect from '@/components/EnumSelect'
const HOST_KEY = 'torna.debug-host'
const FILE_TYPES = ['file', 'file[]']
const TEXT_DECODER = new TextDecoder('utf-8')

export default {
  name: 'DocDebug',
  components: { EnumSelect, editor: require('vue2-ace-editor') },
  props: {
    item: {
      type: Object,
      default: () => {}
    },
    internal: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      rightSpanSize: 0,
      isProxy: true,
      currentItem: {
        debugEnvs: [],
        headerParams: []
      },
      itemMap: null,
      textRadio: 'application/json',
      currentMethod: 'GET',
      cellStyle: { paddingTop: '5px', paddingBottom: '5px' },
      requestUrl: '',
      bodyText: '',
      hasBody: false,
      isPostJson: false,
      contentType: '',
      requestActive: 'body',
      postActive: '',
      collapseActive: '',
      pathData: [],
      headerData: [],
      queryData: [],
      formData: [],
      multipartData: [],
      // check
      headerDataChecked: [],
      queryDataChecked: [],
      formDataChecked: [],
      multipartDataChecked: [],
      uploadFiles: [],
      fieldTypes: [
        { type: 'text', label: $t('text') },
        { type: 'file', label: $t('file') }
      ],
      debugEnv: '',
      debugId: '',
      resultActive: 'result',
      sendLoading: false,
      result: {
        headerData: [],
        content: '',
        status: 200,
        image: ''
      },
      resultEditorConfig: {
        // 去除编辑器里的竖线
        showPrintMargin: false,
        readOnly: true,
        height: 420
      },
      requestEditorConfig: {
        // 去除编辑器里的竖线
        showPrintMargin: false,
        height: 240
      }
    }
  },
  computed: {
    url: {
      get() {
        let url = this.requestUrl
        this.pathData.forEach(row => {
          url = url.replace(new RegExp(`{${row.name}}`), row.example || `{${row.name}}`)
        })
        return url
      },
      set(val) {
        this.requestUrl = val
      }
    },
    isResponseJson() {
      return this.isJsonString(this.result.content)
    }
  },
  watch: {
    item(newVal) {
      this.loadItem(newVal)
    }
  },
  created() {
    this.requestEditorConfig.height = window.innerHeight - 480
    this.resultEditorConfig.height = window.innerHeight - 305
  },
  methods: {
    loadItem(item) {
      this.currentItem = item
      this.currentMethod = item.httpMethod
      this.hasBody = item.requestParams.length > 0
      this.contentType = item.contentType || ''
      this.isPostJson = this.contentType.toLowerCase().indexOf('json') > -1
      this.initActive()
      this.initDebugHost()
    },
    initDebugHost() {
      const debugId = this.getAttr(this.getHostKey()) || ''
      this.changeHostEnv(debugId)
    },
    getHostKey() {
      return HOST_KEY
    },
    changeHostEnv(debugId) {
      const item = this.currentItem
      this.bindRequestParam(item)
      this.setTableCheck()
      const debugEnvs = item.debugEnvs
      if (debugEnvs.length === 0) {
        this.requestUrl = item.url
        return
      }
      const debugConfigs = debugEnvs.filter(row => row.id === debugId || row.name === debugId)
      const debugConfig = debugConfigs.length === 0 ? debugEnvs[0] : debugConfigs[0]
      const baseUrl = debugConfig.url
      this.requestUrl = get_effective_url(baseUrl, item.url)
      this.debugEnv = debugConfig.name
      this.debugId = debugConfig.id
      this.setAttr(this.getHostKey(), this.debugId)
      this.loadGlobalHeader(debugId)
      this.loadProxySelect()
      this.loadProps()
    },
    loadGlobalHeader(debugId) {
      this.get('/doc/headers/global', { environmentId: debugId }, resp => {
        const globalHeaders = resp.data
        const headers = this.headerData
        for (const target of headers) {
          for (const globalHeader of globalHeaders) {
            if (globalHeader.name === target.name) {
              target.example = globalHeader.example
            }
          }
        }
      })
    },
    bindRequestParam(item) {
      const formData = []
      const multipartData = []
      const requestParameters = item.requestParams
      // 是否是上传文件请求
      const uploadRequest = this.isUploadRequest(this.contentType, requestParameters)
      if (this.isPostJson) {
        this.bodyText = this.buildJsonText(requestParameters)
      } else {
        requestParameters.forEach(row => {
          if (uploadRequest) {
            multipartData.push(row)
          } else {
            formData.push(row)
          }
        })
      }
      this.pathData = this.deepCopy(item.pathParams)
      this.headerData = this.deepCopy(item.headerParams)
      this.queryData = this.deepCopy(item.queryParams)
      this.formData = formData
      this.multipartData = multipartData
    },
    buildJsonText(requestParameters) {
      const arrayBody = this.item.isRequestArray === 1
      let jsonObj = this.doCreateResponseExample(requestParameters)
      if (arrayBody) {
        jsonObj = [jsonObj]
        const arrayType = this.item.requestArrayType
        if (arrayType !== 'object') {
          const filterRow = requestParameters.filter(el => el.isDeleted === 0)
          jsonObj = filterRow.length > 0 ? parse_root_array(arrayType, filterRow[0].example) : []
        }
      }
      return this.formatJson(jsonObj)
    },
    initActive() {
      if (this.hasBody) {
        this.requestActive = 'body'
        const contentType = (this.contentType || '').toLowerCase()
        if (contentType.indexOf('multipart') > -1 || this.multipartData.length > 0) {
          this.postActive = 'multipart'
        } else if (contentType.indexOf('json') > -1) {
          this.postActive = 'text'
        } else {
          this.postActive = 'form'
        }
      } else {
        this.requestActive = 'query'
        this.postActive = ''
      }
    },
    isUploadRequest(contentType, requestParameters) {
      if (contentType.toLowerCase().indexOf('multipart') > -1) {
        return true
      }
      return requestParameters.filter(row => row.type === 'file').length > 0
    },
    showBody(active) {
      return this.postActive === active
    },
    onSelectFile(f, fileList, row) {
      const files = []
      fileList.forEach(file => {
        const rawFile = file.raw
        files.push(rawFile)
      })
      row.__file__ = { name: row.name, files: files }
    },
    onSelectMultiFile(file, fileList) {
      const files = []
      fileList.forEach(file => {
        const rawFile = file.raw
        files.push(rawFile)
      })
      this.uploadFiles = files
    },
    send() {
      const item = this.currentItem
      const headers = this.buildRequestHeaders()
      const params = this.getQueryParams(this.queryDataChecked)
      let data = {}
      let isMultipart = false
      // 如果请求body
      if (this.hasBody || this.bodyText.length > 0) {
        headers['Content-Type'] = this.contentType
        const contentType = (this.contentType || '').toLowerCase()
        if (contentType.indexOf('json') > -1) {
          data = JSON.parse(this.bodyText)
        } else if (contentType.indexOf('multipart') > -1 || this.multipartDataChecked.length > 0) {
          isMultipart = true
          data = this.getParamObj(this.multipartDataChecked)
        } else if (contentType.indexOf('x-www-form-urlencoded') > -1) {
          data = this.getQueryParams(this.formDataChecked)
        } else {
          data = this.bodyText
        }
      }
      this.sendLoading = true
      let realHeaders
      let url = this.url
      // 代理转发
      if (this.isProxy) {
        realHeaders = {}
        realHeaders['target-headers'] = JSON.stringify(headers)
        realHeaders['target-url'] = this.url
        url = this.getProxyUrl('/doc/debug/v1')
      } else {
        realHeaders = headers
      }
      request.call(this, item.httpMethod, url, params, data, realHeaders, isMultipart, this.doProxyResponse, error => {
        const resp = error.response
        if (resp) {
          this.doProxyResponse(resp)
        } else {
          this.sendLoading = false
          this.result.content = $t('sendErrorTip')
          this.openRightPanel()
        }
      })
      this.setProps()
    },
    getProxyUrl(uri) {
      return get_full_url(uri)
    },
    getQueryParams(paramsArr) {
      const data = {}
      for (const row of paramsArr) {
        if (!row.name) {
          continue
        }
        let value = row.example || ''
        const type = row.type || 'string'
        // 如果是数组
        if (type.toLowerCase().indexOf('array') > -1) {
          // 空数组不传递
          if (value === '[]' || value === '') {
            continue
          }
          if (is_array_string(value)) {
            value = value.substring(1, value.length - 1)
          }
          const arr = value.split(',')
          const finalArr = []
          for (let i = 0; i < arr.length; i++) {
            let val = arr[i]
            if (val === '' || val === undefined) {
              continue
            }
            val = val.trim()
            // 去除首尾'，"
            if ((val.startsWith('\'') && val.endsWith('\'')) || (val.startsWith('"') && val.endsWith('"'))) {
              // 只有'',""的情况
              if (val.length === 2) {
                val = ''
              } else {
                val = val.substring(1, val.length - 1)
              }
            }
            finalArr.push(val)
          }
          data[row.name] = finalArr
        } else {
          data[row.name] = value
        }
      }
      return data
    },
    setProps() {
      const arr = []
      const formatData = (arr) => {
        const data = {}
        const temps = []
        data.temps = temps
        arr.forEach(row => {
          // 全局属性不加入
          if (!row.global && row.name) {
            data[row.name] = row.example
            if (row.temp) {
              temps.push(row.name)
            }
          }
        })
        return data
      }
      const props = {
        headerData: formatData(this.headerData),
        pathData: formatData(this.pathData),
        queryData: formatData(this.queryData),
        multipartData: formatData(this.multipartData.filter(row => row.type !== 'file')),
        formData: formatData(this.formData),
        bodyText: this.bodyText
      }
      for (const key in props) {
        if (props[key] === '' || JSON.stringify(props[key]) === '{}') {
          delete props[key]
        }
      }
      const debugDataStr = JSON.stringify(props)
      arr.push({
        refId: this.item.id,
        type: this.getEnums().PROP_TYPE.DEBUG,
        name: this.debugId,
        val: debugDataStr
      })
      this.post('/prop/save', { propList: arr }, resp => {})
    },
    saveProxySelect() {
      if (this.debugId) {
        const arr = [this.getProxyProp()]
        this.post('/prop/save', { propList: arr }, resp => {})
      }
    },
    getProxyProp() {
      return {
        refId: this.item.moduleId,
        type: this.getEnums().PROP_TYPE.DEBUG_PROXY,
        name: `torna.debug.proxy.${this.debugId}`,
        val: this.isProxy
      }
    },
    loadProxySelect() {
      if (this.debugId) {
        this.get('/prop/find', {
          refId: this.item.moduleId,
          type: this.getEnums().PROP_TYPE.DEBUG_PROXY,
          name: `torna.debug.proxy.${this.debugId}`
        }, resp => {
          const data = resp.data
          if (!data) {
            this.isProxy = true
          } else {
            this.isProxy = data.val === 'true'
          }
        })
      }
    },
    loadProps() {
      const data = {
        refId: this.item.id,
        type: this.getEnums().PROP_TYPE.DEBUG,
        name: this.debugId
      }
      this.get('/prop/find', data, resp => {
        const respData = resp.data
        if (!respData) {
          return
        }
        const debugData = respData.val
        if (!debugData) {
          return
        }
        const props = JSON.parse(debugData)
        const setProp = (params, data, ref) => {
          if (data && Object.keys(data).length > 0 && params) {
            // 临时添加的
            const temps = data.temps
            for (const tempName of temps) {
              const val = data[tempName]
              if (ref && val) {
                const row = {
                  id: this.nextId() + '',
                  name: tempName,
                  example: val,
                  temp: 1,
                  description: ''
                }
                params.push(row)
              }
            }
            params.forEach(row => {
              const val = data[row.name]
              if (val !== undefined) {
                row.example = val
              }
            })
          }
        }
        setProp(this.headerData, props.headerData, 'headerDataRef')
        setProp(this.pathData, props.pathData)
        setProp(this.queryData, props.queryData, 'queryDataRef')
        setProp(this.multipartData, props.multipartData, 'multipartDataRef')
        setProp(this.formData, props.formData, 'formDataRef')
        if (props.bodyText !== undefined) {
          this.bodyText = props.bodyText
        }
        this.setTableCheck()
      })
    },
    setTableCheck() {
      this.$nextTick(() => {
        this.tableCheckAll('headerDataRef', this.headerData)
        this.tableCheckAll('queryDataRef', this.queryData)
        this.tableCheckAll('formDataRef', this.formData)
        this.tableCheckAll('multipartDataRef', this.multipartData)
      })
    },
    tableCheckAll(ref, params) {
      const refObj = this.$refs[ref]
      if (refObj) {
        for (const row of params) {
          refObj.toggleRowSelection(row, true)
        }
      }
    },
    handleHeaderSelectionChange(val) {
      this.headerDataChecked = val
    },
    handleQuerySelectionChange(val) {
      this.queryDataChecked = val
    },
    handleFormSelectionChange(val) {
      this.formDataChecked = val
    },
    handleMultipartSelectionChange(val) {
      this.multipartDataChecked = val
    },
    buildRequestHeaders() {
      const headers = {}
      this.headerDataChecked.filter(row => row.name && row.name.length > 0).forEach(row => {
        headers[row.name] = row.example || ''
      })
      return headers
    },
    getParamObj(array) {
      const data = {}
      array.forEach(row => {
        // 处理文件上传
        const fileConfig = row.__file__
        if (fileConfig) {
          const fileConfigs = this.getFileConfigs(data)
          fileConfigs.push(fileConfig)
        } else {
          data[row.name] = row.example
        }
      })
      // 全局上传
      if (this.uploadFiles.length > 0) {
        const fileConfigs = this.getFileConfigs(data)
        fileConfigs.push({ name: 'file', files: this.uploadFiles })
      }
      return data
    },
    getFileConfigs(data) {
      let fileConfigs = data['__files__']
      if (!fileConfigs) {
        fileConfigs = []
        data['__files__'] = fileConfigs
      }
      return fileConfigs
    },
    doProxyResponse(response) {
      this.sendLoading = false
      this.result = {
        headerData: [],
        content: '',
        status: 200,
        image: ''
      }
      this.buildResultHeaders(response)
      this.buildResultStatus(response)
      this.buildResultContent(response)
    },
    buildResultStatus(response) {
      this.result.status = response.status
    },
    buildResultContent(response) {
      const headers = response.targetHeaders
      const contentType = this.getHeaderValue(headers, 'Content-Type') || ''
      const contentDisposition = this.getHeaderValue(headers, 'Content-Disposition') || ''
      // 如果是下载文件
      if (contentType.indexOf('stream') > -1 || contentDisposition.indexOf('attachment') > -1) {
        const filename = this.getDispositionFilename(contentDisposition)
        this.downloadFile(filename, response.data)
      } else if (contentType.indexOf('image') > -1) {
        // 如果返回图片
        this.result.image = `data:${contentType};base64,` + btoa(new Uint8Array(response.data).reduce((data, byte) => data + String.fromCharCode(byte), ''))
      } else {
        let content = ''
        let json
        const arrayBuffer = response.data
        // 如果是ArrayBuffer
        if (arrayBuffer && arrayBuffer.toString() === '[object ArrayBuffer]') {
          if (arrayBuffer.byteLength > 0) {
            json = TEXT_DECODER.decode(new Uint8Array(arrayBuffer))
          }
        } else {
          // 否则认为是json
          json = arrayBuffer
        }
        // 格式化json
        try {
          if (json) {
            content = this.formatResponse(contentType, json)
          }
        } catch (e) {
          console.error($t('parseError'), e)
        }
        this.result.content = content
      }
      this.openRightPanel()
    },
    getHeaderValue(headers, key) {
      for (const k in headers) {
        if (k.toLowerCase() === key.toLowerCase()) {
          return headers[k]
        }
      }
      return null
    },
    onBodyBlur() {
      if (this.bodyText && this.textRadio === 'application/json') {
        try {
          this.bodyText = this.formatJson(JSON.parse(this.bodyText))
          // eslint-disable-next-line no-empty
        } catch (e) {
          console.error('format json error', e)
        }
      }
    },
    formatResponse(contentType, stringBody) {
      if (this.isObject(stringBody) || this.isArray(stringBody)) {
        return this.formatJson(stringBody)
      }
      if (!contentType) {
        return stringBody
      }
      const contentTypeLower = contentType.toLowerCase()
      if (contentTypeLower.indexOf('json') > -1) {
        return this.formatJson(JSON.parse(stringBody))
      } else if (contentTypeLower.indexOf('xml') > -1) {
        return xmlFormatter(stringBody)
      } else {
        return stringBody
      }
    },
    downloadFile(filename, buffer) {
      const blob = new Blob([buffer])
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.setAttribute('download', filename)
      document.body.appendChild(link)
      link.click()
    },
    getDispositionFilename(disposition) {
      const dispositionArr = disposition.split(';')
      for (let i = 0; i < dispositionArr.length; i++) {
        const item = dispositionArr[i].trim()
        // filename=xx.xls
        if (item.toLowerCase().startsWith('filename')) {
          const result = item.split('=')
          if (result && result.length > 1) {
            let filename = result[1]
            if (filename.startsWith('\'') || filename.startsWith('\"')) {
              filename = filename.substring(1)
            }
            if (filename.endsWith('\'') || filename.endsWith('\"')) {
              filename = filename.substring(0, filename.length - 1)
            }
            return filename
          }
          return ''
        }
      }
    },
    buildResultHeaders(response) {
      const targetHeaders = this.getTargetHeaders(response)
      response.targetHeaders = targetHeaders
      const headersData = []
      if (targetHeaders) {
        for (const key in targetHeaders) {
          headersData.push({ name: key, value: targetHeaders[key] })
        }
      }
      this.result.headerData = headersData
    },
    getTargetHeaders(response) {
      const headers = response.headers
      if (this.isProxy) {
        const targetHeadersString = headers['target-response-headers'] || '{}'
        return JSON.parse(targetHeadersString)
      } else {
        return headers
      }
    },
    openRightPanel() {
      this.resultActive = 'body'
      this.rightSpanSize = 10
    },
    isFileParam(row) {
      const type = row.type
      for (const fileType of FILE_TYPES) {
        if (type === fileType) {
          return true
        }
      }
      return false
    },
    onTempHeaderAdd() {
      const row = {
        id: this.nextId() + '',
        name: '',
        example: '',
        temp: 1,
        description: '',
        children: []
      }
      this.addTempParam('headerDataRef', this.headerData, row)
    },
    onTempQueryAdd() {
      const row = {
        id: this.nextId() + '',
        name: '',
        example: '',
        temp: 1,
        description: '',
        children: []
      }
      this.addTempParam('queryDataRef', this.queryData, row)
    },
    onTempMultipartAdd() {
      const row = {
        id: this.nextId() + '',
        name: '',
        example: '',
        temp: 1,
        description: '',
        children: []
      }
      this.addTempParam('multipartDataRef', this.multipartData, row)
    },
    onTempFormAdd() {
      const row = {
        id: this.nextId() + '',
        name: '',
        example: '',
        temp: 1,
        description: '',
        children: []
      }
      this.addTempParam('formDataRef', this.formData, row)
    },
    addTempParam(ref, params, row) {
      params.push(row)
      this.$nextTick(() => {
        const refObj = this.$refs[ref]
        if (refObj) {
          refObj.toggleRowSelection(row, true)
        }
      })
    },
    removeTableRow(rows, row) {
      this.removeRow(rows, row.id)
    },
    requestEditorInit: function(editor) {
      // language extension prerequsite...
      require('brace/ext/language_tools')
      // language
      require('brace/mode/json')
      require('brace/theme/chrome')
      // 监听值的变化
      const that = this
      editor.on('blur', event => {
        that.onBodyBlur()
      })
    },
    resultEditorInit: function() {
      // language extension prerequsite...
      require('brace/ext/language_tools')
      // language
      require('brace/mode/json')
      require('brace/theme/chrome')
    }
  }
}
</script>
