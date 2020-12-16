<template>
  <div class="doc-debug">
    <el-row :gutter="20">
      <el-col :span="24-rightSpanSize">
        <el-input v-model="url" :readonly="pathData.length > 0" class="request-url">
          <span slot="prepend">
            {{ currentMethod }}
          </span>
          <el-button slot="append" :loading="sendLoading" class="btn-send" @click="send"> 发 送 </el-button>
        </el-input>
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
import { request } from '@/utils/http'
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
      currentItem: null,
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
      this.requestUrl = this.getRequestUrl(item)
      this.bindRequestParam(item)
      this.initActive()
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
      this.headerData = item.globalHeaderParams.concat(item.headerParams)
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
          isMultipart = this.multipartData.length > 0 || this.uploadFiles.length > 0
          data = this.getParamObj(this.multipartData)
          break
        default:
      }
      this.sendLoading = true
      request(item.httpMethod, '/doc/debug', data, headers, isJson, isForm, isMultipart, this.doProxyResponse)
    },
    buildRequestHeaders() {
      const headers = {}
      this.headerData.forEach(row => {
        headers[row.name] = row.example || ''
      })
      headers['target-url'] = this.url
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
    doProxyResponse(error, response) {
      this.sendLoading = false
      if (error) {
        this.$message.error(error)
        return
      }
      this.buildResultHeaders(response)
      this.buildResultStatus(error, response)
      this.buildResultContent(error, response)
    },
    buildResultStatus(error, response) {
      if (!error) {
        this.result.status = response.statusCode || response.status
      }
    },
    buildResultContent(error, response) {
      const headers = response.targetHeaders
      const contentType = headers['content-type'] || ''
      const contentDisposition = headers['content-disposition'] || ''
      this.openRightPanel()
      // 如果是下载文件
      if (contentType.indexOf('stream') > -1 ||
        contentDisposition.indexOf('attachment') > -1
      ) {
        const disposition = headers['content-disposition']
        const filename = this.getDispositionFilename(disposition)
        this.downloadFile(filename, response.raw)
      } else {
        let content = ''
        if (error) {
          content = error.message
        } else {
          // axios返回data部分
          const data = response.data
          if (data) {
            try {
              content = this.formatResponse(contentType, data)
            } catch (e) {
              console.error('格式转换错误', e)
              content = response.data
            }
          } else {
            // needle返回部分
            const uint8Array = response.raw
            if (uint8Array && uint8Array.length > 0) {
              const resp = new TextDecoder().decode(uint8Array)
              try {
                content = this.formatResponse(contentType, resp)
              } catch (e) {
                console.error('格式转换错误', e)
                content = resp
              }
            }
          }
        }
        this.result.content = content
      }
    },
    formatResponse(contentType, stringBody) {
      if (this.isObject(stringBody)) {
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
    formatJson(json) {
      return JSON.stringify(json, null, 4)
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
      const headers = response.headers
      const targetHeadersString = headers['target-response-headers'] || '{}'
      const targetHeaders = JSON.parse(targetHeadersString)
      response.targetHeaders = targetHeaders
      const headersData = []
      if (targetHeaders) {
        for (const key in targetHeaders) {
          headersData.push({ name: key, value: targetHeaders[key] })
        }
      }
      this.result.headerData = headersData
    },
    openRightPanel() {
      this.resultActive = 'body'
      this.rightSpanSize = 10
    }
  }
}
</script>
