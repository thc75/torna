<template>
  <div class="doc-debug">
    <el-row :gutter="20">
      <el-col :span="24-rightSpanSize">
        <div v-if="currentItem.debugEnvs.length > 0 || currentItem.moduleType === 2">
          <el-radio-group v-if="currentItem.debugEnvs.length > 0" v-model="debugEnv" size="mini" style="margin-bottom: 4px;" @change="changeHostEnv">
            <el-radio-button
              v-for="hostConfig in currentItem.debugEnvs"
              :key="hostConfig.configKey"
              :label="hostConfig.configKey"
            />
          </el-radio-group>
          <span class="split">|</span>
          <el-checkbox v-model="isProxy" label="代理转发" />
          <el-popover
            placement="right"
            title="代理转发"
            width="400"
            :open-delay="500"
            trigger="hover"
          >
            <p>勾选：服务端代理转发请求</p>
            <p>取消勾选：页面使用axios请求，需要处理跨域</p>
            <i slot="reference" class="el-icon-question"></i>
          </el-popover>
          <el-input v-model="requestUrl" :readonly="pathData.length > 0" class="request-url">
            <span slot="prepend">
              {{ currentMethod }}
            </span>
            <el-button slot="append" :loading="sendLoading" class="btn-send" @click="send"> 发 送 </el-button>
          </el-input>
        </div>
        <el-alert v-else :closable="false">
          <span slot="title">
            尚未指定调试环境，请前往
            【<router-link class="el-link el-link--primary" :to="getProjectHomeUrl(currentItem.projectId, 'id=ModuleSetting')">模块配置</router-link>】
            进行添加。
            <router-link class="el-link el-link--primary" target="_blank" to="/help?id=debug">参考文档</router-link>
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
              label="Path参数"
              width="300"
            />
            <el-table-column label="参数值">
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
                :data="headerData"
                border
                :header-cell-style="cellStyle"
                :cell-style="cellStyle"
                empty-text="无header"
              >
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
          <el-tab-pane label="Body" name="body">
            <span slot="label" class="result-header-label">
              <el-badge :is-dot="hasBody" type="danger">
                <span>Body</span>
              </el-badge>
            </span>
            <el-radio-group v-model="postActive" size="small" style="margin-bottom: 20px;">
              <el-radio-button label="text" class="json-badge">Text</el-radio-button>
              <el-radio-button label="form">x-www-form-urlencoded <span class="param-count">({{ formData.length }})</span></el-radio-button>
              <el-radio-button label="multipart">multipart <span class="param-count">({{ multipartData.length }})</span></el-radio-button>
            </el-radio-group>
            <div v-show="showBody('text')">
              <el-radio-group v-model="contentType" style="margin-bottom: 10px;">
                <el-radio label="application/json">JSON</el-radio>
                <el-radio label="text/plain">Text</el-radio>
                <el-radio label="application/xml">XML</el-radio>
                <el-radio label="text/html">HTML</el-radio>
                <el-radio label="application/x-javascript">JavaScript</el-radio>
              </el-radio-group>
              <el-form>
                <el-form-item label-width="0">
                  <el-input v-model="bodyText" type="textarea" :autosize="{ minRows: 2, maxRows: 100}" />
                </el-form-item>
              </el-form>
            </div>
            <div v-show="showBody('form')">
              <el-table
                :data="formData"
                border
                :header-cell-style="cellStyle"
                :cell-style="cellStyle"
              >
                <el-table-column
                  prop="name"
                  label="参数名"
                  width="300"
                />
                <el-table-column label="参数值">
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
                <el-button slot="trigger" type="primary" size="mini">上传多个文件</el-button>
              </el-upload>
              <el-table
                v-show="showBody('multipart')"
                :data="multipartData"
                border
                :header-cell-style="cellStyle"
                :cell-style="cellStyle"
              >
                <el-table-column
                  prop="name"
                  label="参数名"
                  width="300"
                />
                <el-table-column label="参数值">
                  <template slot-scope="scope">
                    <el-form :model="scope.row" size="mini">
                      <el-form-item label-width="0" style="margin-bottom: 0">
                        <el-upload
                          v-if="scope.row.type === 'file' || scope.row.elementType === 'file'"
                          action=""
                          :multiple="true"
                          :auto-upload="false"
                          :on-change="(file, fileList) => onSelectFile(file, fileList, scope.row)"
                          :on-remove="(file, fileList) => onSelectFile(file, fileList, scope.row)"
                        >
                          <el-button slot="trigger" class="choose-file" type="primary">选择文件</el-button>
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
          <el-tab-pane label="Query" name="query">
            <span slot="label" class="result-header-label">
              <span>Query <span class="param-count">({{ queryData.length }})</span></span>
            </span>
            <el-table
              :data="queryData"
              border
              :header-cell-style="cellStyle"
              :cell-style="cellStyle"
            >
              <el-table-column
                prop="name"
                label="参数名"
                width="300"
              />
              <el-table-column label="参数值">
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
        </el-tabs>
      </el-col>
      <el-col :span="rightSpanSize" style="border-left: 1px #E4E7ED solid;">
        <div class="result-status">
          Status: <el-tag :type="result.status === 200 ? 'success' : 'danger'">{{ result.status }}</el-tag>
        </div>
        <el-tabs v-model="resultActive" type="card">
          <el-tab-pane label="返回结果" name="body">
            <el-input v-model="result.content" type="textarea" :readonly="true" :autosize="{ minRows: 2, maxRows: 200}" />
          </el-tab-pane>
          <el-tab-pane label="Headers" name="headers">
            <span slot="label" class="result-header-label">
              <span>Headers <span class="param-count">({{ result.headerData.length }})</span></span>
            </span>
            <el-table
              :data="result.headerData"
              :header-cell-style="cellStyle"
              :cell-style="cellStyle"
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
.btn-send {
  color: #FFFFFF !important;
  width: 100px;
  background-color: #409EFF !important;
  border-radius: 0 !important;
}
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
import { request, get_full_url } from '@/utils/http'
import { get_effective_url } from '@/utils/common'

const HOST_KEY = 'torna.debug-host'

export default {
  name: 'DocDebug',
  props: {
    item: {
      type: Object,
      default: () => {}
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
      currentMethod: 'GET',
      cellStyle: { paddingTop: '5px', paddingBottom: '5px' },
      requestUrl: '',
      bodyText: '',
      hasBody: false,
      isTextBody: false,
      contentType: '',
      requestActive: 'body',
      postActive: '',
      collapseActive: '',
      formData: [],
      multipartData: [],
      queryData: [],
      uploadFiles: [],
      fieldTypes: [
        { type: 'text', label: '文本' },
        { type: 'file', label: '文件' }
      ],
      headerData: [],
      pathData: [],
      debugEnv: '',
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
      this.hasBody = this.isRequestBody(this.currentMethod)
      this.contentType = item.contentType
      if (!this.contentType) {
        this.contentType = this.hasBody ? 'application/json' : ''
      }
      this.isTextBody = this.contentType.toLowerCase().indexOf('application') > -1
      this.initDebugHost()
      this.bindRequestParam(item)
      this.initActive()
    },
    initDebugHost() {
      const debugEnv = this.getAttr(HOST_KEY) || ''
      this.changeHostEnv(debugEnv)
    },
    changeHostEnv(debugEnv) {
      const item = this.currentItem
      const debugEnvs = item.debugEnvs
      if (debugEnvs.length === 0) {
        this.requestUrl = item.url
        return
      }
      const debugConfigs = debugEnvs.filter(row => row.configKey === debugEnv)
      const debugConfig = debugConfigs.length === 0 ? debugEnvs[0] : debugConfigs[0]
      debugEnv = debugConfig.configKey
      const baseUrl = debugConfig.configValue
      this.requestUrl = get_effective_url(baseUrl, item.url)
      this.setAttr(HOST_KEY, debugEnv)
      this.debugEnv = debugEnv
    },
    bindRequestParam(item) {
      const queryData = []
      const formData = []
      const multipartData = []
      const requestParameters = item.requestParams
      // 是否是上传文件请求
      const uploadRequest = this.isUploadRequest(this.contentType, requestParameters)
      requestParameters.forEach(row => {
        if (this.hasBody) {
          if (uploadRequest) {
            multipartData.push(row)
          } else {
            formData.push(row)
          }
        } else {
          queryData.push(row)
        }
      })
      this.pathData = item.pathParams
      this.headerData = item.headerParams
      this.queryData = queryData
      this.formData = formData
      this.multipartData = multipartData
      if (this.isJsonBody()) {
        const arrayBody = false
        let jsonObj = this.doCreateResponseExample(requestParameters)
        if (arrayBody) {
          jsonObj = [jsonObj]
        }
        this.bodyText = JSON.stringify(jsonObj, null, 4)
      }
    },
    initActive() {
      if (this.hasBody) {
        this.requestActive = 'body'
        if (this.multipartData.length > 0) {
          this.postActive = 'multipart'
        } else {
          this.postActive = this.isTextBody ? 'text' : 'form'
        }
      } else {
        this.requestActive = 'query'
        this.postActive = ''
      }
    },
    isJsonBody() {
      return this.isTextBody && this.contentType.toLowerCase().indexOf('json') > -1
    },
    isRequestBody(httpMethod) {
      const methods = ['get', 'head']
      for (const method of methods) {
        if (method === httpMethod.toLowerCase()) {
          return false
        }
      }
      return true
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
      let data = this.getParamObj(this.queryData)
      let isJson = false
      let isForm = false
      let isMultipart = false
      // 如果请求body
      switch (this.postActive) {
        case 'text':
          headers['Content-Type'] = this.contentType
          data = this.bodyText
          if (this.contentType.indexOf('json') > -1) {
            isJson = true
          }
          break
        case 'form':
          isForm = true
          data = this.getParamObj(this.formData)
          break
        case 'multipart':
          isMultipart = true
          data = this.getParamObj(this.multipartData)
          break
        default:
      }
      if (isForm) {
        headers['Content-Type'] = 'application/x-www-form-urlencoded'
      }
      if (isJson) {
        headers['Content-Type'] = 'application/json'
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
      request.call(this, item.httpMethod, url, data, realHeaders, isMultipart, this.doProxyResponse, () => {
        this.sendLoading = false
        this.result.content = '发送失败，请按F12查看Console'
        this.openRightPanel()
      })
    },
    getProxyUrl(uri) {
      return get_full_url(uri)
    },
    buildRequestHeaders() {
      const headers = {}
      this.headerData.forEach(row => {
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
      if (contentType.indexOf('stream') > -1 ||
        contentDisposition.indexOf('attachment') > -1
      ) {
        const filename = this.getDispositionFilename(contentDisposition)
        this.downloadFile(filename, response.data)
      } else {
        let content = ''
        // axios返回data部分
        const data = response.data
        try {
          content = this.formatResponse(contentType, data)
        } catch (e) {
          console.error('格式转换错误', e)
          content = response.data
        }
        this.result.content = content
      }
      this.openRightPanel()
    },
    getHeaderValue(headers, key) {
      return headers[key] || headers[key.toLowerCase()]
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
      const url = window.URL.createObjectURL(new Blob([buffer]))
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
    }
  }
}
</script>
