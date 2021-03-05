<template>
  <currentDocInfodiv class="doc-view">
    <div class="doc-title">
      <h2 class="doc-title">
        {{ docInfo.name }} <span v-show="docInfo.id" class="doc-id">ID：{{ docInfo.id }}</span>
        <div v-show="showOptBar" style="float: right">
          <el-tooltip placement="top" :content="isSubscribe ? '点击取消关注' : '点击关注'">
            <el-button
              type="text"
              :icon="isSubscribe ? 'el-icon-star-on' : 'el-icon-star-off'"
              style="font-size: 16px"
              @click="onSubscribe"
            />
          </el-tooltip>
          <el-dropdown trigger="click" @command="handleCommand">
            <el-button type="primary" size="mini">
              导出 <i class="el-icon-arrow-down el-icon--right"></i>
            </el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item :command="onExportMarkdown">导出markdown</el-dropdown-item>
              <el-dropdown-item :command="onExportHtml">导出html</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </h2>
      <span v-show="showOptBar" class="doc-modify-info">
        {{ docInfo.creatorName }} 创建于 {{ docInfo.gmtCreate }}，
        {{ docInfo.modifierName }} 最后修改于 {{ docInfo.gmtModified }}
      </span>
    </div>
    <h4>URL</h4>
    <ul v-if="docInfo.debugEnvs.length > 0" class="debug-url">
      <li v-for="hostConfig in docInfo.debugEnvs" :key="hostConfig.configKey">
        {{ hostConfig.configKey }}: <http-method :method="docInfo.httpMethod" /> {{ buildRequestUrl(hostConfig) }}
      </li>
    </ul>
    <span v-else class="debug-url">
      <http-method :method="docInfo.httpMethod" /> {{ docInfo.url }}
    </span>
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
    <div v-if="docInfo.remark" class="doc-info-remark">
      <el-divider content-position="left">修改备注</el-divider>
      <span>{{ docInfo.remark }}</span>
    </div>
  </currentDocInfodiv>
</template>

<style lang="scss" scoped>
.doc-view {
  .http-method {
    height: auto !important;
    line-height: 20px !important;
    padding: 0 4px !important;
  }
  .doc-overview {margin-top: 20px;margin-bottom: 30px;color: #666;font-size: 14px;}
  .doc-modify-info { font-size: 12px;color: #909399 }
  .debug-url { font-size: 14px;color: #606266 }
}
.doc-title {
  margin: 0
}
</style>

<script>
import ParameterTable from '@/components/ParameterTable'
import HttpMethod from '@/components/HttpMethod'
import ExportUtil from '@/utils/export'
import { get_effective_url } from '@/utils/common'

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
        debugEnvs: [],
        folders: []
      },
      responseSuccessExample: {},
      isSubscribe: false
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
          this.initDocInfo(data)
          this.setData(data)
        })
      }
    },
    loadSubscribe(id) {
      this.get('/user/subscribe/doc/isSubscribe', { sourceId: id }, resp => {
        this.isSubscribe = resp.data
      })
    },
    setData: function(data) {
      if (data.id) {
        this.loadSubscribe(data.id)
      }
      Object.assign(this.docInfo, data)
      this.$store.state.settings.moduleId = this.docInfo.moduleId
      this.responseSuccessExample = this.doCreateResponseExample(data.responseParams)
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
          this.tipSuccess('关注成功')
          this.isSubscribe = true
        })
      } else {
        this.post('/user/subscribe/doc/cancelSubscribe', { sourceId: this.docInfo.id }, resp => {
          this.tipSuccess('取消关注成功')
          this.isSubscribe = false
        })
      }
    }
  }
}
</script>

