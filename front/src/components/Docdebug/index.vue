<template>
  <div class="doc-debug">
    <h2>{{ docInfo.summary }}</h2>
    <el-form
      ref="configForm"
      size="mini"
      :model="configFormData"
      :rules="configFormRules"
      label-width="120px"
    >
      <el-form-item prop="url" label="网关地址">
        <el-input v-model="configFormData.url" clearable />
      </el-form-item>
      <el-form-item prop="appKey" label="AppId">
        <el-input v-model="configFormData.appKey" clearable />
      </el-form-item>
      <el-form-item prop="privateKey" label="应用私钥">
        <el-input v-model="configFormData.privateKey" clearable @change="onPrivateKeyChange" />
      </el-form-item>
      <el-form-item label="token">
        <el-input v-model="configFormData.token" clearable />
      </el-form-item>
    </el-form>
    <h2>请求参数</h2>
    <parameter-table-edit ref="paramTableRef" :data="docInfo.requestParameters" />
    <!-- 多文件选择 -->
    <el-upload
      v-show="docInfo.multiple"
      action=""
      :multiple="true"
      :auto-upload="false"
      style="width: 500px;margin-top: 10px"
      :on-remove="(file, fileList) => onSelectMultiFile(file, fileList)"
      :on-change="(file, fileList) => onSelectMultiFile(file, fileList)"
    >
      <el-button slot="trigger" type="primary" size="mini">上传多个文件</el-button>
    </el-upload>
    <br/>
    <el-form
      size="mini"
    >
      <el-form-item label="HttpMethod">
        <el-radio-group v-model="httpMethod">
          <el-radio v-for="method in docInfo.httpMethodList" :key="method" :label="method">{{ method.toUpperCase() }}</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <el-button type="primary" @click="send">发送请求</el-button>
    <el-tabs v-show="resultShow">
      <el-tabs v-model="resultActive" type="card">
        <el-tab-pane label="请求信息" name="reqInfo">
          <el-input v-model="reqInfo" type="textarea" :rows="10" readonly />
        </el-tab-pane>
        <el-tab-pane label="请求结果" name="resultContent">
          <el-input v-model="resultContent" type="textarea" :rows="16" readonly />
        </el-tab-pane>
      </el-tabs>
    </el-tabs>
  </div>
</template>
<style>
.api-info {font-weight: bold;}
.doc-overview {margin-top: 20px;margin-bottom: 30px;color: #666;font-size: 14px;}
.doc-request-method {margin-bottom: 20px;color: #666;font-size: 14px;}
.cell .choose-file {padding: 5px;}
.doc-debug .cell .el-form-item {margin-bottom: 0;}
</style>
<script>
import ParameterTableEdit from '@/components/ParameterTableEdit'
const privateKeyStoreKey = 'sop.sendbox.privateKey'
export default {
  name: 'Docdebug',
  components: { ParameterTableEdit },
  props: {
    item: {
      type: Object,
      default: () => {}
    },
    appId: {
      type: String,
      default: ''
    },
    gatewayUrl: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      baseInfoCellStyle: (row) => {
        if (row.columnIndex === 0 || row.columnIndex === 2) {
          return { padding: '5px 0', background: '#f5f7fa' }
        } else {
          return { padding: '5px 0' }
        }
      },
      configFormRules: {
        appKey: [
          { required: true, message: '请填写AppId', trigger: 'blur' }
        ],
        privateKey: [
          { required: true, message: '请填写应用私钥', trigger: 'blur' }
        ],
        url: [
          { required: true, message: '请填写URL', trigger: 'blur' }
        ]
      },
      configFormData: {
        url: '',
        appKey: '',
        privateKey: '',
        token: ''
      },
      httpMethod: '',
      docInfo: {
        summary: '',
        name: '',
        version: '',
        multiple: false,
        uploadRequest: false,
        httpMethodList: [],
        requestParameters: [],
        responseParameters: [],
        bizCodes: []
      },
      uploadFiles: [],
      resultActive: 'resultContent',
      resultShow: false,
      reqInfo: '',
      resultContent: ''
    }
  },
  watch: {
    item(newVal) {
      this.initItem(newVal)
    },
    appId(newVal) {
      this.configFormData.appKey = newVal
    },
    gatewayUrl(url) {
      this.configFormData.url = url
    }
  },
  created() {
    const privateKey = this.getAttr(privateKeyStoreKey)
    if (privateKey) {
      this.configFormData.privateKey = privateKey
    }
  },
  methods: {
    send() {
      this.$refs.configForm.validate(valid => {
        if (valid) {
          // 验证表格参数
          const promiseRequestArr = this.validateTable(this.docInfo.requestParameters, ['req_form_example_'])
          Promise.all(promiseRequestArr).then(validArr => {
            // 到这里来表示全部内容校验通过
            this.doSend()
          }).catch((e) => {
            // this.tipError('请完善表单内容')
          }) // 加上这个控制台不会报Uncaught (in promise)
        }
      })
    },
    doSend() {
      const bizContent = this.buildParamData(this.docInfo.requestParameters)
      const data = {
        gatewayUrl: this.configFormData.url,
        appId: this.configFormData.appKey,
        privateKey: this.configFormData.privateKey,
        token: this.configFormData.token,
        method: this.docInfo.name,
        version: this.docInfo.version,
        httpMethod: this.httpMethod,
        bizContent: JSON.stringify(bizContent)
      }
      const files = this.buildFiles(this.docInfo.requestParameters)
      const isForm = this.httpMethod.toUpperCase() === 'POST'
      this.request(this.httpMethod, '/sandbox/test_v2', data, {}, false, isForm, files, function(error, response) {
        this.resultShow = true
        this.resultActive = 'resultContent'
        const status = response.statusCode || response.status
        if (!error && status === 200) {
          this.successHandler(response)
        } else {
          console.log(error)
          this.$message.error('请求异常，请查看日志')
        }
      })
    },
    validateTable: function(arr, refPrefixArr) {
      const $refs = this.$refs.paramTableRef.$refs
      let promiseArr = []
      for (let i = 0; i < arr.length; i++) {
        const row = arr[i]
        const id = row.id
        refPrefixArr.forEach(refPrefix => {
          const ref = $refs[refPrefix + id]
          if (ref) {
            promiseArr.push(ref.validate())
          }
        })
        const children = arr[i].children
        if (children && children.length > 0) {
          const childrenPromiseArr = this.validateTable(children, refPrefixArr)
          promiseArr = promiseArr.concat(childrenPromiseArr)
        }
      }
      return promiseArr
    },
    successHandler(response) {
      this.setReqInfo(response)
      this.setRespInfo(response)
    },
    setReqInfo(response) {
      const headers = response.headers
      if (headers) {
        const html = []
        html.push('【请求参数】：' + decodeURIComponent(headers['sendbox-params']))
        html.push('【待签名内容】：' + decodeURIComponent(headers['sendbox-beforesign']))
        html.push('【签名(sign)】：' + decodeURIComponent(headers['sendbox-sign']))
        this.reqInfo = html.join('\r\n')
      }
    },
    setRespInfo(response) {
      const headers = response.headers
      const targetHeadersString = headers['target-response-headers'] || '{}'
      const targetHeaders = JSON.parse(targetHeadersString)
      const contentType = targetHeaders['content-type'] || ''
      const contentDisposition = targetHeaders['content-disposition'] || ''
      if (contentType.indexOf('stream') > -1 || contentDisposition.indexOf('attachment') > -1) {
        const filename = this.getDispositionFilename(contentDisposition)
        this.downloadFile(filename, response.raw)
      } else {
        const body = response.body || response.data
        this.resultContent = JSON.stringify(body, null, 4)
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
    resetResult() {
      this.reqInfo = ''
      this.resultContent = ''
      this.resultShow = false
    },
    initItem(item) {
      if (item) {
        this.setData(item)
      }
    },
    setData: function(data) {
      this.resetResult()
      this.docInfo = data
      this.httpMethod = this.docInfo.httpMethodList[0]
    },
    onSelectMultiFile(file, fileList) {
      const files = []
      fileList.forEach(file => {
        const rawFile = file.raw
        files.push(rawFile)
      })
      this.uploadFiles = files
    },
    onPrivateKeyChange() {
      this.setAttr(privateKeyStoreKey, this.configFormData.privateKey)
    },
    buildParamData: function(params) {
      const responseJson = {}
      for (let i = 0; i < params.length; i++) {
        const row = params[i]
        if (row.in === 'header') {
          continue
        }
        let val
        // 如果有子节点
        if (row.refs && row.refs.length > 0) {
          const childrenValue = this.buildParamData(row.refs)
          // 如果是数组
          if (row.type === 'array') {
            val = [childrenValue]
          } else {
            val = childrenValue
          }
        } else {
          // 单值
          val = row.paramExample
        }
        responseJson[row.name] = val
      }
      const isOneArray = Object.keys(responseJson).length === 1 && this.isArray(Object.values(responseJson)[0])
      if (isOneArray) {
        return Object.values(responseJson)[0]
      }
      return responseJson
    },
    buildFiles(params) {
      const files = []
      for (let i = 0; i < params.length; i++) {
        const row = params[i]
        // 处理文件上传
        const fileConfig = row.__file__
        if (fileConfig) {
          files.push(fileConfig)
        }
      }
      // 全局上传
      if (this.uploadFiles.length > 0) {
        files.push({ name: 'file', files: this.uploadFiles })
      }
      return files
    }
  }
}
</script>
