<template>
  <div class="doc-view">
    <div class="doc-title">
      <h2 class="doc-title">
        {{ docInfo.name }} <dubbo-service-tip :doc-id-getter="() => docInfo.parentId" placement="bottom" /> <span v-show="docInfo.id" class="doc-id">ID：{{ docInfo.id }}</span>
        <div v-show="showOptBar" style="float: right">
          <el-tooltip placement="top" :content="isSubscribe ? $t('cancelSubscribe') : $t('clickSubscribe')">
            <el-button
              type="text"
              :icon="isSubscribe ? 'el-icon-star-on' : 'el-icon-star-off'"
              style="font-size: 16px"
              @click="onSubscribe"
            />
          </el-tooltip>
          <el-dropdown trigger="click" @command="handleCommand">
            <el-button type="primary" size="mini">
              {{ $t('export') }} <i class="el-icon-arrow-down el-icon--right"></i>
            </el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item :command="onExportMarkdown">{{ $t('exportMarkdown') }}</el-dropdown-item>
              <el-dropdown-item :command="onExportHtml">{{ $t('exportHtml') }}</el-dropdown-item>
              <el-dropdown-item :command="onExportWord">{{ $t('exportWord') }}</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </h2>
      <span v-show="showOptBar" class="doc-modify-info">
        {{ docInfo.creatorName }} {{ $t('createdOn') }} {{ docInfo.gmtCreate }}，
        {{ docInfo.modifierName }} {{ $t('lastModifiedBy') }} {{ docInfo.gmtModified }}
      </span>
    </div>
    <h4 v-show="docInfo.author">{{ $t('maintainer') }}<span class="content">{{ docInfo.author }}</span></h4>
    <h4>{{ $t('interface') }}<span>{{ docInfo.dubboInfo && docInfo.dubboInfo.interfaceName }}</span></h4>
    <h4>{{ $t('method') }}<span>{{ buildDefinition(docInfo) }}</span></h4>
    <h4 v-if="docInfo.description">{{ $t('description') }}<span>{{ docInfo.description }}</span></h4>
    <h4>{{ $t('invokeParam') }}</h4>
    <parameter-table :data="docInfo.requestParams" />
    <h4>{{ $t('returnResult') }}</h4>
    <parameter-table v-show="!isResponseSingleValue" :data="docInfo.responseParams" />
    <div v-if="isResponseSingleValue">{{ responseSingleValue }}</div>
    <h4>{{ $t('errorCode') }}</h4>
    <parameter-table
      :data="docInfo.errorCodeParams"
      :empty-text="$t('emptyErrorCode')"
      :hidden-columns="['required', 'maxLength', 'type']"
      :name-label="$t('errorCode')"
      :description-label="$t('errorDesc')"
      :example-label="$t('solution')"
    />
    <div v-if="docInfo.remark" class="doc-info-remark">
      <el-divider content-position="left">{{ $t('updateRemark') }}</el-divider>
      <span>{{ docInfo.remark }}</span>
    </div>
  </div>
</template>

<script>
import ParameterTable from '@/components/ParameterTable'
import DubboServiceTip from '@/components/DubboServiceTip'
import ExportUtil from '@/utils/export'

export default {
  name: 'DubboView',
  components: { ParameterTable, DubboServiceTip },
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
        author: '',
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
        globalHeaders: [],
        globalParams: [],
        globalReturns: [],
        debugEnvs: [],
        folders: [],
        dubboInfo: {
          interfaceName: ''
        }
      },
      requestExample: {},
      responseSuccessExample: {},
      isSubscribe: false
    }
  },
  computed: {
    isRequestJson() {
      return this.docInfo.contentType && this.docInfo.contentType.toLowerCase().indexOf('json') > -1
    },
    isResponseSingleValue() {
      const responseParams = this.docInfo.responseParams
      if (responseParams && responseParams.length === 1) {
        const responseParam = responseParams[0]
        return !responseParam.name
      }
      return false
    },
    responseSingleValue() {
      const responseParams = this.docInfo.responseParams
      if (responseParams && responseParams.length === 1) {
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
  created() {
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
    },
    buildDefinition(docInfo) {
      let url = docInfo.url
      if (url && url.startsWith('/')) {
        url = url.substring(1)
      }
      return url
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
          this.tipSuccess($t('subscribeSuccess'))
          this.isSubscribe = true
        })
      } else {
        this.post('/user/subscribe/doc/cancelSubscribe', { sourceId: this.docInfo.id }, resp => {
          this.tipSuccess($t('unsubscribeSuccess'))
          this.isSubscribe = false
        })
      }
    }
  }
}
</script>
<style scoped>
h4 span {
  margin: 0 10px;
}
</style>
