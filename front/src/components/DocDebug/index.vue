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
              {{ hostConfig.configKey }}
            </el-radio-button>
          </el-radio-group>
          <span class="split">|</span>
          <el-checkbox v-model="isProxy" :label="$ts('proxyForward')" />
          <el-popover
            placement="right"
            :title="$ts('proxyForward')"
            width="400"
            :open-delay="500"
            trigger="hover"
          >
            <p>{{ $ts('proxyForwardOn') }}</p>
            <p>{{ $ts('proxyForwardOff') }}</p>
            <i slot="reference" class="el-icon-question"></i>
          </el-popover>
          <el-input v-model="requestUrl" :readonly="pathData.length > 0" class="request-url">
            <span slot="prepend">
              {{ currentMethod }}
            </span>
            <el-button slot="append" :loading="sendLoading" class="btn-send" @click="send">{{ $ts('debugSend') }}</el-button>
          </el-input>
        </div>
        <el-alert v-else :closable="false">
          <span v-if="internal" slot="title">
            {{ $ts('noDebugEvnTip1') }}
            【<router-link class="el-link el-link--primary" :to="getProjectHomeUrl(currentItem.projectId, 'id=ModuleSetting')">{{ $ts('moduleSetting') }}</router-link>】
            {{ $ts('noDebugEvnTip2') }}
            <el-link type="primary" :underline="false" @click="openLink('/help?id=debug')">{{ $ts('referenceDoc') }}</el-link>
          </span>
          <span v-else>
            {{ $ts('noDebugEvnTip3') }}
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
              :label="$ts('pathVariable')"
              width="300"
            />
            <el-table-column :label="$ts('value')">
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
              <el-table
                ref="headerDataRef"
                :data="headerData"
                border
                :empty-text="$ts('noHeader')"
                @selection-change="handleHeaderSelectionChange"
              >
                <el-table-column type="selection" width="50" />
                <el-table-column label="Name" prop="name" width="300px" />
                <el-table-column label="Value">
                  <template slot-scope="scope">
                    <el-form :model="scope.row" size="mini">
                      <el-form-item label-width="0">
                        <el-input v-model="scope.row.example" />
                      </el-form-item>
                    </el-form>
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
            <el-table
              ref="queryDataRef"
              :data="queryData"
              border
              @selection-change="handleQuerySelectionChange"
            >
              <el-table-column type="selection" width="50" />
              <el-table-column
                prop="name"
                :label="$ts('paramName')"
                width="300"
              />
              <el-table-column :label="$ts('value')">
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
                  <el-input v-model="bodyText" type="textarea" :autosize="{ minRows: 2, maxRows: 100}" @blur="onBodyBlur" />
                </el-form-item>
              </el-form>
            </div>
            <div v-show="showBody('form')">
              <el-table
                ref="formDataRef"
                :data="formData"
                border
                @selection-change="handleFormSelectionChange"
              >
                <el-table-column type="selection" width="50" />
                <el-table-column
                  prop="name"
                  :label="$ts('paramName')"
                  width="300"
                />
                <el-table-column :label="$ts('value')">
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
            <div v-show="showBody('multipart')">
              <el-upload
                action=""
                :multiple="true"
                :auto-upload="false"
                style="width: 500px;margin-bottom: 10px"
                :on-remove="(file, fileList) => onSelectMultiFile(file, fileList)"
                :on-change="(file, fileList) => onSelectMultiFile(file, fileList)"
              >
                <el-button slot="trigger" type="primary" size="mini">{{ $ts('uploadMultiFiles') }}</el-button>
              </el-upload>
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
                  :label="$ts('paramName')"
                  width="300"
                />
                <el-table-column :label="$ts('value')">
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
                          <el-button slot="trigger" class="choose-file" type="primary">{{ $ts('chooseFile') }}</el-button>
                        </el-upload>
                        <div v-else-if="scope.row.type === 'enum'">
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
          </el-tab-pane>
        </el-tabs>
      </el-col>
      <el-col :span="rightSpanSize" style="border-left: 1px #E4E7ED solid;">
        <div class="result-status">
          Status: <el-tag :type="result.status === 200 ? 'success' : 'danger'">{{ result.status }}</el-tag>
        </div>
        <el-tabs v-model="resultActive" type="card">
          <el-tab-pane :label="$ts('returnResult')" name="body">
            <el-input v-model="result.content" type="textarea" :readonly="true" :autosize="{ minRows: 2, maxRows: 200}" />
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

const HOST_KEY = 'torna.debug-host'
const FILE_TYPES = ['file', 'file[]']
const TEXT_DECODER = new TextDecoder('utf-8')

export default {
  name: 'DocDebug',
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
        debugEnvs: []
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
        { type: 'text', label: $ts('text') },
        { type: 'file', label: $ts('file') }
      ],
      debugEnv: '',
      debugId: '',
      resultActive: 'result',
      sendLoading: false,
      result: {
        headerData: [],
        content: '',
        status: 200
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
    }
  },
  watch: {
    item(newVal) {
      this.loadItem(newVal)
    }
  },
  methods: {
    loadItem(item) {
      this.currentItem = item
      this.currentMethod = item.httpMethod
      this.hasBody = item.requestParams.length > 0
      this.contentType = item.contentType || ''
      this.isPostJson = this.contentType.toLowerCase().indexOf('json') > -1
      this.initDebugHost()
      this.bindRequestParam(item)
      this.initActive()
      this.setTableCheck()
    },
    initDebugHost() {
      const debugEnv = this.getAttr(HOST_KEY) || ''
      this.changeHostEnv(debugEnv)
    },
    changeHostEnv(debugId) {
      const item = this.currentItem
      const debugEnvs = item.debugEnvs
      if (debugEnvs.length === 0) {
        this.requestUrl = item.url
        return
      }
      const debugConfigs = debugEnvs.filter(row => row.id === debugId || row.configKey === debugId)
      const debugConfig = debugConfigs.length === 0 ? debugEnvs[0] : debugConfigs[0]
      const baseUrl = debugConfig.configValue
      this.requestUrl = get_effective_url(baseUrl, item.url)
      this.debugEnv = debugConfig.configKey
      this.debugId = debugConfig.id
      this.setAttr(HOST_KEY, this.debugId)
      this.loadProps()
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
      this.pathData = item.pathParams
      this.headerData = item.headerParams
      this.queryData = item.queryParams
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
      if (this.hasBody) {
        headers['Content-Type'] = this.contentType
        const contentType = (this.contentType || '').toLowerCase()
        if (contentType.indexOf('json') > -1) {
          data = this.bodyText
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
      const targetHeaders = JSON.stringify(headers)
      const realHeaders = Object.assign({}, headers)
      realHeaders['target-headers'] = targetHeaders
      let url = this.url
      if (this.isProxy) {
        url = this.getProxyUrl('/doc/debug/v1')
        realHeaders['target-url'] = this.url
      }
      request.call(this, item.httpMethod, url, params, data, realHeaders, isMultipart, this.doProxyResponse, () => {
        this.sendLoading = false
        this.result.content = $ts('sendErrorTip')
        this.openRightPanel()
      })
      this.setProps()
    },
    getProxyUrl(uri) {
      return get_full_url(uri)
    },
    getQueryParams(paramsArr) {
      const data = {}
      for (const row of paramsArr) {
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
      const formatData = (arr) => {
        const data = {}
        arr.forEach(row => {
          // 全局属性不加入
          if (!row.global) {
            data[row.name] = row.example
          }
        })
        return data
      }
      const props = {
        isProxy: this.isProxy,
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
      const propsData = {
        debugData: debugDataStr
      }
      propsData[this.debugId] = debugDataStr
      const data = {
        refId: this.item.id,
        type: this.getEnums().PROP_TYPE.DEBUG,
        props: propsData
      }
      this.post('/prop/set', data, resp => {})
    },
    loadProps() {
      const data = {
        refId: this.item.id,
        type: this.getEnums().PROP_TYPE.DEBUG
      }
      this.get('/prop/get', data, resp => {
        const respData = resp.data
        const debugData = respData[this.debugId] || respData.debugData
        if (!debugData) {
          return
        }
        const props = JSON.parse(debugData)
        const setProp = (params, data) => {
          if (data && Object.keys(data).length > 0 && params) {
            params.forEach(row => {
              const val = data[row.name]
              if (val !== undefined) {
                row.example = val
              }
            })
          }
        }
        if (props.isProxy !== undefined) {
          this.isProxy = props.isProxy
        }
        setProp(this.headerData, props.headerData)
        setProp(this.pathData, props.pathData)
        setProp(this.queryData, props.queryData)
        setProp(this.multipartData, props.multipartData)
        setProp(this.formData, props.formData)
        if (props.bodyText !== undefined) {
          this.bodyText = props.bodyText
        }
      })
    },
    setTableCheck() {
      this.$refs.headerDataRef.toggleAllSelection()
      this.$refs.queryDataRef.toggleAllSelection()
      this.$refs.formDataRef.toggleAllSelection()
      this.$refs.multipartDataRef.toggleAllSelection()
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
      this.headerDataChecked.forEach(row => {
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
          console.error($ts('parseError'), e)
        }
        this.result.content = content
      }
      this.openRightPanel()
    },
    getHeaderValue(headers, key) {
      return headers[key] || headers[key.toLowerCase()]
    },
    onBodyBlur() {
      if (this.bodyText && this.contentType && this.contentType.toLowerCase().indexOf('json') > -1) {
        try {
          this.bodyText = this.formatJson(JSON.parse(this.bodyText))
          // eslint-disable-next-line no-empty
        } catch (e) {
          console.log('format json error', e)
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
        // filename="xx"
        if (item.toLowerCase().startsWith('filename')) {
          const result = item.match(new RegExp('filename="(.*?)"', 'i'))
          return result ? result[1] : ''
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
    }
  }
}
</script>
