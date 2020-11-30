<template>
  <div>
    <div class="doc-baseinfo">
      <h2 style="margin-top: 0">{{ docInfo.name }} #{{ docInfo.id }}</h2>
    </div>
    <h3>接口描述</h3>
    <div class="doc-overview">{{ docInfo.description || docInfo.name }}</div>
    <h3>请求地址</h3>
    <span class="normal-text">
      <el-tag type="info">{{ docInfo.httpMethod }}</el-tag> {{ docInfo.url }}
    </span>
    <h3>请求Header</h3>
    <parameter-table
      :data="docInfo.headerParams"
      :can-add-node="false"
      :hidden-columns="['type', 'maxLength']"
      empty-text="无Header"
    />
    <h3>请求参数</h3>
    <parameter-table :data="docInfo.requestParams" />
    <h3>响应参数</h3>
    <parameter-table :data="docInfo.responseParams" :hidden-columns="['required', 'maxLength']" />
    <h3>响应示例</h3>
    <pre class="normal-text">{{ JSON.stringify(responseSuccessExample, null, 4) }}</pre>
    <h3>错误码</h3>
    <parameter-table
      :data="docInfo.errorCodeParams"
      empty-text="无错误码"
      :hidden-columns="['required', 'maxLength', 'type']"
      name-label="错误码"
      description-label="错误描述"
      example-label="解决方案"
    />
  </div>
</template>

<script>
import ParameterTable from '@/components/ParameterTable'
export default {
  name: 'DocView',
  components: { ParameterTable },
  props: {
    docId: {
      type: String,
      default: ''
    },
    docInfoString: {
      type: String,
      default: '{}'
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
      commonParams: [],
      commonResult: [],
      docBaseInfoData: [],
      docInfo: {
        docId: '',
        name: '',
        url: '',
        contentType: '',
        description: '',
        httpMethod: 'GET',
        parentId: '',
        moduleId: '',
        isShow: 1,
        headerParams: [],
        requestParams: [],
        responseParams: [],
        errorCodeParams: [],
        folders: []
      },
      responseSuccessExample: {}
    }
  },
  watch: {
    docId(newVal) {
      this.loadData(newVal)
    },
    docInfoString(docInfoString) {
      const docInfo = JSON.parse(docInfoString)
      this.setData(docInfo)
    }
  },
  created() {
  },
  methods: {
    loadData: function(docId) {
      if (docId) {
        this.get('/doc/detail', { id: docId }, function(resp) {
          const data = resp.data
          data.requestParams = this.convertTree(data.requestParams)
          data.responseParams = this.convertTree(data.responseParams)
          this.setData(data)
        })
      }
    },
    setData: function(data) {
      this.docInfo = data
      this.createResponseExample(data)
    },
    createResponseExample: function(data) {
      this.responseSuccessExample = this.doCreateResponseExample(data.responseParams)
    },
    doCreateResponseExample: function(params) {
      const responseJson = {}
      params.forEach(row => {
        let val
        // 如果有子节点
        if (row.children && row.children.length > 0) {
          const childrenValue = this.doCreateResponseExample(row.children)
          // 如果是数组
          if (row.type === 'Array') {
            val = [childrenValue]
          } else {
            val = childrenValue
          }
        } else {
          // 单值
          val = row.example
        }
        responseJson[row.name] = val
      })
      return responseJson
    }
  }
}
</script>

<style scoped>
.doc-overview {margin-top: 20px;margin-bottom: 30px;color: #666;font-size: 14px;}
.doc-request-method {margin-bottom: 20px;color: #666;font-size: 14px;}
</style>
