<template>
  <div class="doc-view">
    <div class="doc-title">
      <h2 class="doc-title">
        {{ docInfo.name }} <span v-show="docInfo.id" class="doc-id">ID：{{ docInfo.id }}</span>
        <div v-show="showOptBar" style="float: right">
          <el-tooltip placement="top" :content="isSubscribe ? $ts('cancelSubscribe') : $ts('clickSubscribe')">
            <el-button
              type="text"
              :icon="isSubscribe ? 'el-icon-star-on' : 'el-icon-star-off'"
              style="font-size: 16px"
              @click="onSubscribe"
            />
          </el-tooltip>
          <el-dropdown trigger="click" @command="handleCommand">
            <el-button type="primary" size="mini">
              {{ $ts('export') }} <i class="el-icon-arrow-down el-icon--right"></i>
            </el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item :command="onExportMarkdown">{{ $ts('exportMarkdown') }}</el-dropdown-item>
              <el-dropdown-item :command="onExportHtml">{{ $ts('exportHtml') }}</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </h2>
      <span v-show="showOptBar" class="doc-modify-info">
        {{ docInfo.creatorName }} {{ $ts('createdOn') }} {{ docInfo.gmtCreate }}，
        {{ docInfo.modifierName }} {{ $ts('lastModifiedBy') }} {{ docInfo.gmtModified }}
      </span>
    </div>
    <h4 v-if="docInfo.author">{{ $ts('maintainer') }}<span class="content">{{ docInfo.author }}</span></h4>
    <h4>URL</h4>
    <ul v-if="docInfo.debugEnvs.length > 0" class="debug-url">
      <li v-for="hostConfig in docInfo.debugEnvs" :key="hostConfig.configKey">
        {{ hostConfig.configKey }}: <http-method :method="docInfo.httpMethod" /> {{ buildRequestUrl(hostConfig) }}
      </li>
    </ul>
    <span v-else class="debug-url">
      <http-method :method="docInfo.httpMethod" /> {{ docInfo.url }}
    </span>
    <h4 v-if="docInfo.description" class="doc-descr">
      <div>{{ $ts('description') }}</div>
      <span class="content" v-html="docInfo.description.replace(/\n/g,'<br />')"></span>
    </h4>
    <h4 v-if="docInfo.contentType">ContentType<span class="content">{{ docInfo.contentType }}</span></h4>
    <div v-if="docInfo.pathParams.length > 0">
      <h4>{{ $ts('pathVariable') }}</h4>
      <parameter-table
        :data="docInfo.pathParams"
        :can-add-node="false"
        :hidden-columns="['maxLength']"
      />
    </div>
    <div v-if="docInfo.headerParams.length > 0">
      <h4>{{ $ts('requestHeader') }}</h4>
      <parameter-table
        :data="docInfo.headerParams"
        :can-add-node="false"
        :hidden-columns="['type', 'maxLength']"
        :empty-text="$ts('noHeader')"
      />
    </div>
    <h4>{{ $ts('requestParams') }}</h4>
    <span v-show="docInfo.queryParams.length === 0 && docInfo.requestParams.length === 0" class="normal-text">{{ $ts('empty') }}</span>
    <div v-show="docInfo.queryParams.length > 0">
      <h5>Query Parameter</h5>
      <parameter-table :data="docInfo.queryParams" />
    </div>
    <div v-show="docInfo.requestParams.length > 0">
      <h5>Body Parameter</h5>
      <el-alert v-if="docInfo.isRequestArray" :closable="false" :title="$ts('tip')" :description="$ts('objectArrayReqTip')" />
      <parameter-table :data="docInfo.requestParams" :hidden-columns="requestParamHiddenColumns" />
    </div>
    <div v-show="isShowRequestExample">
      <h4>{{ $ts('requestExample') }}</h4>
      <pre class="code-block">{{ formatJson(requestExample) }}</pre>
    </div>
    <h4>{{ $ts('responseParam') }}</h4>
    <el-alert v-if="docInfo.isResponseArray" :closable="false" :title="$ts('tip')" :description="$ts('objectArrayRespTip')" />
    <parameter-table :data="docInfo.responseParams" :hidden-columns="responseParamHiddenColumns" />
    <h4>{{ $ts('responseExample') }}</h4>
    <pre class="code-block">{{ formatJson(responseSuccessExample) }}</pre>
    <h4>{{ $ts('errorCode') }}</h4>
    <parameter-table
      :data="docInfo.errorCodeParams"
      :empty-text="$ts('emptyErrorCode')"
      :hidden-columns="['required', 'maxLength', 'type']"
      :name-label="$ts('errorCode')"
      :description-label="$ts('errorDesc')"
      :example-label="$ts('solution')"
    />
    <div v-if="docInfo.remark" class="doc-info-remark">
      <el-divider content-position="left">{{ $ts('updateRemark') }}</el-divider>
      <span>{{ docInfo.remark }}</span>
    </div>
  </div>
</template>
<style scoped>
h4 .content {
  margin: 0 10px;
}
</style>
<script>
import ParameterTable from '@/components/ParameterTable'
import HttpMethod from '@/components/HttpMethod'
import ExportUtil from '@/utils/export'
import { get_effective_url, parse_root_array } from '@/utils/common'

export default {
  name: 'DocView',
  components: { ParameterTable, HttpMethod },
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
    },
    showOptBar: {
      type: Boolean,
      default: true
    },
    initSubscribe: {
      type: Boolean,
      default: true
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
        id: '',
        name: '',
        url: '',
        contentType: '',
        description: '',
        author: '',
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
        queryParams: [],
        requestParams: [],
        responseParams: [],
        errorCodeParams: [],
        globalHeaders: [],
        globalParams: [],
        globalReturns: [],
        debugEnvs: [],
        folders: [],
        isRequestArray: 0,
        isResponseArray: 0,
        requestArrayType: 'object',
        responseArrayType: 'object'
      },
      requestExample: {},
      responseSuccessExample: {},
      isSubscribe: false,
      responseHiddenColumns: []
    }
  },
  computed: {
    isRequestJson() {
      const docInfo = this.docInfo
      return docInfo.contentType && docInfo.contentType.toLowerCase().indexOf('json') > -1
    },
    isShowRequestExample() {
      return this.isRequestJson && this.docInfo.requestParams.length > 0
    },
    requestParamHiddenColumns() {
      const isRawArray = this.docInfo.isRequestArray && this.docInfo.requestArrayType !== 'object'
      return isRawArray ? ['name', 'required', 'maxLength'] : []
    },
    responseParamHiddenColumns() {
      const isRawArray = this.docInfo.isResponseArray && this.docInfo.responseArrayType !== 'object'
      return isRawArray ? ['name', 'required', 'maxLength'] : this.responseHiddenColumns
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
  mounted() {
    this.initResponseHiddenColumns()
  },
  methods: {
    loadData: function(docId) {
      if (docId) {
        this.get(this.url, { id: docId }, function(resp) {
          const data = resp.data
          this.initDocInfoView(data)
          this.setData(data)
        })
      }
    },
    loadSubscribe(id) {
      if (id && this.initSubscribe) {
        this.get('/user/subscribe/doc/isSubscribe', { sourceId: id }, resp => {
          this.isSubscribe = resp.data
        })
      }
    },
    setData: function(data) {
      this.loadSubscribe(data.id)
      Object.assign(this.docInfo, data)
      this.$store.state.settings.moduleId = this.docInfo.moduleId
      this.requestExample = this.doCreateResponseExample(data.requestParams)
      this.responseSuccessExample = this.doCreateResponseExample(data.responseParams)
      // 如果是数组对象
      if (this.docInfo.isRequestArray) {
        this.requestExample = [this.requestExample]
        const arrayType = this.docInfo.requestArrayType
        if (arrayType !== 'object') {
          const filterRow = data.requestParams.filter(el => el.isDeleted === 0)
          this.requestExample = filterRow.length > 0 ? parse_root_array(arrayType, filterRow[0].example) : []
        }
      }
      // 如果返回纯数组对象
      if (this.docInfo.isResponseArray) {
        this.responseSuccessExample = [this.responseSuccessExample]
        const arrayType = this.docInfo.responseArrayType
        if (arrayType !== 'object') {
          const filterRow = data.responseParams.filter(el => el.isDeleted === 0)
          this.responseSuccessExample = filterRow.length > 0 ? parse_root_array(arrayType, filterRow[0].example) : []
        }
      }
    },
    initResponseHiddenColumns() {
      this.pmsConfig().then(config => {
        const responseHiddenColumnsConfig = config.responseHiddenColumns
        const responseHiddenColumns = []
        if (responseHiddenColumnsConfig) {
          const columnNames = responseHiddenColumnsConfig.split(',')
          for (const columnName of columnNames) {
            responseHiddenColumns.push(columnName.trim())
          }
        }
        this.responseHiddenColumns = responseHiddenColumns
      })
    },
    buildRequestUrl(hostConfig) {
      const baseUrl = hostConfig.configValue
      const url = this.docInfo.url
      return get_effective_url(baseUrl, url)
    },
    onExportMarkdown() {
      ExportUtil.exportMarkdownSinglePage(this.docInfo)
    },
    onExportHtml() {
      ExportUtil.exportHtmlSinglePage(this.docInfo)
    },
    onSubscribe() {
      if (!this.isSubscribe) {
        this.post('/user/subscribe/doc/subscribe', { sourceId: this.docInfo.id }, resp => {
          this.tipSuccess($ts('subscribeSuccess'))
          this.isSubscribe = true
        })
      } else {
        this.post('/user/subscribe/doc/cancelSubscribe', { sourceId: this.docInfo.id }, resp => {
          this.tipSuccess($ts('unsubscribeSuccess'))
          this.isSubscribe = false
        })
      }
    }
  }
}
</script>

