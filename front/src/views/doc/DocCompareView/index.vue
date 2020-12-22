<template>
  <div class="doc-view">
    <div class="doc-title">
      <h2 style="margin-top: 0">
        {{ docInfo.name }}
      </h2>
      <span class="doc-modify-info">
        {{ docInfo.creatorName }} 创建于 {{ docInfo.gmtCreate }}，
        {{ docInfo.modifierName }} 最后修改于 {{ docInfo.gmtModified }}
      </span>
    </div>
    <h4>
      URL：
      <span>
        <el-tag type="info" class="http-method">{{ docInfo.httpMethod }}</el-tag> {{ getRequestUrl(docInfo) }}
      </span>
    </h4>
    <h4 v-if="docInfo.description">描述：<span>{{ docInfo.description }}</span></h4>
    <h4 v-if="docInfo.contentType">ContentType：<span>{{ docInfo.contentType }}</span></h4>
    <div v-if="docInfo.pathParams.length > 0">
      <h4>Path参数</h4>
      <parameter-table
        :data="docInfo.pathParams"
        :can-add-node="false"
        :hidden-columns="['maxLength']"
      />
    </div>
    <h4>请求Header</h4>
    <parameter-table
      :data="docInfo.headerParams"
      :can-add-node="false"
      :hidden-columns="['type', 'maxLength']"
      empty-text="无Header"
    />
    <h4>请求参数</h4>
    <parameter-table :data="docInfo.requestParams" />
    <h4>响应参数</h4>
    <parameter-table :data="docInfo.responseParams" :hidden-columns="['required', 'maxLength']" />
    <h4>响应示例</h4>
    <pre class="normal-text">{{ JSON.stringify(responseSuccessExample, null, 4) }}</pre>
    <h4>错误码</h4>
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

<style lang="scss" scoped>
.doc-view {
  .http-method {
    height: auto !important;
    line-height: 20px !important;
  }
  .doc-overview {margin-top: 20px;margin-bottom: 30px;color: #666;font-size: 14px;}
  .doc-modify-info { font-size: 12px;color: #909399 }
  .doc-request-method {margin-bottom: 20px;color: #666;font-size: 14px;}
}
</style>

<script>
import ParameterTable from '@/components/ParameterTable'
import md5 from 'js-md5'

export default {
  name: 'DocCompareView',
  components: { ParameterTable },
  props: {
    docId: {
      type: String,
      default: ''
    },
    item: {
      type: Object,
      default: () => {}
    },
    url: {
      type: String,
      default: '/doc/detail'
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
        creatorName: '',
        modifierName: '',
        gmtCreate: '',
        gmtModified: '',
        pathParams: [],
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
    docInfoString(docInfoString) {
      const docInfo = JSON.parse(docInfoString)
      this.setData(docInfo)
    },
    item(data) {
      this.setData(data)
    }
  },
  created() {
  },
  methods: {
    loadData: function(docId) {
      if (docId) {
        this.get(this.url, { id: docId }, function(resp) {
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
    getData() {
      return this.docInfo
    },
    compare(otherData) {
      this.compareParams(otherData.pathParams, this.docInfo.pathParams)
      this.compareParams(otherData.headerParams, this.docInfo.headerParams)
      this.compareParams(otherData.requestParams, this.docInfo.requestParams)
      this.compareParams(otherData.responseParams, this.docInfo.responseParams)
      this.compareParams(otherData.errorCodeParams, this.docInfo.errorCodeParams)
    },
    compareParams(otherParams, thisParams) {
      const thisJson = {}
      for (const thisParam of thisParams) {
        thisJson[thisParam.id] = thisParam
      }
      for (const otherParam of otherParams) {
        const thisParam = thisJson[otherParam.id]
        // 如果没找到，表示已删除
        if (!thisParam) {
          this.setChanged(otherParam)
        } else {
          const otherMd5 = md5(this.buildParamContent(otherParam))
          const thisMd5 = md5(this.buildParamContent(thisParam))
          if (otherMd5 !== thisMd5) {
            this.setChanged(otherParam)
            this.setChanged(thisParam)
          }
          const otherChildren = otherParam.children || []
          const thisChildren = thisParam.children || []
          if (otherChildren.length > 0 && thisChildren.length > 0) {
            this.compareParams(otherChildren, thisChildren)
          }
        }
      }
    },
    buildParamContent(param) {
      return `${param.name}:${param.type}:${param.required}:${param.description}:${param.example}`
    },
    setChanged(obj) {
      if (obj) {
        obj._changed = true
      }
    },
    createResponseExample: function(data) {
      this.responseSuccessExample = this.doCreateResponseExample(data.responseParams)
    }
  }
}
</script>

