<template>
  <div class="doc-view">
    <div class="doc-title">
      <h2 class="doc-title">
        <span :class="{ 'deprecated': isDeprecated }">{{ docInfo.name }}</span>
        <span v-show="docInfo.id" class="doc-id">ID：{{ docInfo.id }}</span>
        <el-tooltip placement="top" :content="isSubscribe ? $ts('cancelSubscribe') : $ts('clickSubscribe')">
          <el-button
            type="text"
            class="icon-button"
            :icon="isSubscribe ? 'el-icon-star-on' : 'el-icon-star-off'"
            style="font-size: 16px"
            @click="onSubscribe"
          />
        </el-tooltip>
        <div v-show="showOptBar" class="show-opt-bar" style="float: right;">
          <div class="item">
            <el-dropdown trigger="click" @command="handleCommand">
              <el-tooltip placement="top" :content="$ts('export')">
                <el-button type="text" class="icon-button" icon="el-icon-download" />
              </el-tooltip>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item :command="onExportMarkdown">{{ $ts('exportMarkdown') }}</el-dropdown-item>
                <el-dropdown-item :command="onExportHtml">{{ $ts('exportHtml') }}</el-dropdown-item>
                <el-dropdown-item :command="onExportWord">{{ $ts('exportWord') }}</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
          <div class="item">
            <el-tooltip placement="top" :content="$ts('viewConst')">
              <el-button type="text" class="icon-button" icon="el-icon-collection" @click="showConst" />
            </el-tooltip>
          </div>
        </div>
      </h2>
      <span v-show="showOptBar" class="doc-modify-info">
        {{ docInfo.creatorName }} {{ $ts('createdOn') }} {{ docInfo.gmtCreate }}，
        {{ docInfo.modifierName }} {{ $ts('lastModifiedBy') }} {{ docInfo.gmtModified }}
      </span>
    </div>
    <div v-show="isDeprecated" style="margin-top: 10px">
      <el-tag type="warning" class="el-tag-method">{{ $ts('deprecated') }}</el-tag>
      <span class="tip">{{ docInfo.deprecated }}</span>
    </div>
    <h4 v-if="docInfo.author">{{ $ts('maintainer') }}<span class="content">{{ docInfo.author }}</span></h4>
    <h4>URL</h4>
    <ul v-if="docInfo.debugEnvs.length > 0" class="debug-url">
      <li v-for="hostConfig in docInfo.debugEnvs" :key="hostConfig.name" @mouseenter="onMouseEnter(hostConfig.name)" @mouseleave="onMouseLeave()">
        {{ hostConfig.name }}: <http-method :method="docInfo.httpMethod" /> {{ buildRequestUrl(hostConfig) }}
        <el-tag
          v-show="hostConfigName === hostConfig.name"
          size="small"
          effect="plain"
          class="copyBtn"
          @click.stop="copy(docInfo.url)">{{ $ts('copy') }}</el-tag>
      </li>
    </ul>
    <div v-else class="debug-url" @mouseenter="isShowDebugUrlCopy=true" @mouseleave="isShowDebugUrlCopy=false">
      <http-method :method="docInfo.httpMethod" /> {{ docInfo.url }}
      <el-tag
        v-show="isShowDebugUrlCopy"
        size="small"
        effect="plain"
        class="copyBtn"
        @click.stop="copy(docInfo.url)"
      >{{ $ts('copy') }}</el-tag>
    </div>
    <h4 v-show="docInfo.description && docInfo.description !== emptyContent" class="doc-descr">
      {{ $ts('description') }}
    </h4>
    <div v-show="docInfo.description" class="rich-editor" v-html="docInfo.description.replace(/\n/g,'<br />')"></div>
    <h4 v-show="docInfo.contentType">ContentType<span class="content">{{ docInfo.contentType }}</span></h4>
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
      <el-alert v-if="docInfo.isRequestArray" :closable="false" show-icon :title="$ts('objectArrayReqTip')" />
      <parameter-table :data="docInfo.requestParams" :hidden-columns="requestParamHiddenColumns" />
    </div>
    <div v-show="isShowRequestExample">
      <h4>{{ $ts('requestExample') }}</h4>
      <div class="code-box" @mouseenter="isShowRequestExampleCopy=true" @mouseleave="isShowRequestExampleCopy=false">
        <pre class="code-block">{{ formatJson(requestExample) }}</pre>
        <el-tag
          v-show="isShowRequestExampleCopy"
          size="small"
          effect="plain"
          class="code-copy"
          @click.stop="copy(formatJson(requestExample))">{{ $ts('copy') }}</el-tag>
      </div>
    </div>
    <h4>{{ $ts('responseParam') }}</h4>
    <el-alert v-if="docInfo.isResponseArray" :closable="false" show-icon :title="$ts('objectArrayRespTip')" />
    <parameter-table v-show="!isResponseSingleValue" :data="docInfo.responseParams" :hidden-columns="responseParamHiddenColumns" />
    <div v-if="isResponseSingleValue">{{ responseSingleValue }}</div>
    <h4>{{ $ts('responseExample') }}</h4>
    <div class="code-box" @mouseenter="isShowResponseSuccessExample=true" @mouseleave="isShowResponseSuccessExample=false">
      <pre class="code-block">{{ formatJson(responseSuccessExample) }}</pre>
      <el-tag
        v-show="isShowResponseSuccessExample"
        size="small"
        effect="plain"
        class="code-copy"
        @click.stop="copy(formatJson(responseSuccessExample))">{{ $ts('copy') }}</el-tag>
    </div>
    <div v-show="docInfo.errorCodeParams && docInfo.errorCodeParams.length > 0">
      <h4>{{ $ts('errorCode') }}</h4>
      <parameter-table
        :data="docInfo.errorCodeParams"
        :empty-text="$ts('emptyErrorCode')"
        :hidden-columns="['required', 'maxLength', 'type']"
        :name-label="$ts('errorCode')"
        :description-label="$ts('errorDesc')"
        :example-label="$ts('solution')"
      />
    </div>
    <div v-show="docInfo.remark && docInfo.remark !== emptyContent" class="doc-info-remark">
      <el-divider content-position="left">{{ $ts('updateRemark') }}</el-divider>
      <div class="rich-editor" v-html="docInfo.remark.replace(/\n/g,'<br />')"></div>
    </div>
    <p></p>
    <const-view ref="constView" />
  </div>
</template>
<style scoped>
h4 .content {
  margin: 0 10px;
}
.debug-url .copyBtn {
  margin-left: 10px;
  cursor: pointer;
}
.code-box{
  position: relative
}
.code-box .code-copy{
  display: block;
  position: absolute;
  right: 2px;
  top: 2px;
  margin: 8px;
  cursor: pointer;
}
.show-opt-bar .item {
  margin-left: 10px;
  display: inline-block;
}
</style>
<script>
import ParameterTable from '@/components/ParameterTable'
import HttpMethod from '@/components/HttpMethod'
import ConstView from '@/components/ConstView'
import ExportUtil from '@/utils/export'
import { get_effective_url, parse_root_array } from '@/utils/common'

export default {
  name: 'DocView',
  components: { ParameterTable, HttpMethod, ConstView },
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
        deprecated: '$false$',
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
        errorCodeInfo: '',
        globalHeaders: [],
        globalParams: [],
        globalReturns: [],
        debugEnvs: [],
        folders: [],
        isRequestArray: 0,
        isResponseArray: 0,
        requestArrayType: 'object',
        responseArrayType: 'object',
        remark: ''
      },
      requestExample: {},
      responseSuccessExample: {},
      isSubscribe: false,
      responseHiddenColumns: [],
      hostConfigName: '',
      isShowDebugUrlCopy: false,
      isShowRequestExampleCopy: false,
      isShowResponseSuccessExample: false,
      emptyContent: '<p><br></p>'
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
    },
    isDeprecated() {
      return this.docInfo.deprecated !== '$false$'
    },
    isResponseSingleValue() {
      const responseParams = this.docInfo.responseParams
      if (responseParams && responseParams.length === 1 && !this.docInfo.isResponseArray) {
        const responseParam = responseParams[0]
        return !responseParam.name
      }
      return false
    },
    responseSingleValue() {
      const responseParams = this.docInfo.responseParams
      if (responseParams && responseParams.length === 1 && !this.docInfo.isResponseArray) {
        const responseParam = responseParams[0]
        return responseParam.type
      }
      return ''
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
      // 如果返回单个参数
      if (this.isResponseSingleValue) {
        this.responseSuccessExample = this.responseSingleValue
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
      const baseUrl = hostConfig.url
      const url = this.docInfo.url
      return get_effective_url(baseUrl, url)
    },
    onExportMarkdown() {
      ExportUtil.exportMarkdownSinglePage(this.docInfo)
    },
    onExportHtml() {
      ExportUtil.exportHtmlSinglePage(this.docInfo)
    },
    onExportWord() {
      ExportUtil.exportWordSinglePage(this.docInfo)
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
    },
    onMouseEnter(name) {
      this.hostConfigName = name
    },
    onMouseLeave() {
      this.hostConfigName = ''
    },
    copy(text) {
      this.copyText(text)
    },
    showConst() {
      this.$refs.constView.show(this.docInfo.moduleId)
    }
  }
}
</script>

