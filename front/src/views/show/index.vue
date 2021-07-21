<template>
  <div v-show="hasData" class="app-container">
    <el-tabs active-name="info" type="card" @tab-click="onTabSelect">
      <el-tab-pane name="info">
        <span slot="label"><i class="el-icon-document"></i> {{ $ts('apiInfo') }}</span>
        <doc-view ref="docView" :show-opt-bar="false" :init-subscribe="false" :item="item" />
      </el-tab-pane>
      <el-tab-pane v-if="setting.showDebug" name="debug">
        <span slot="label"><i class="el-icon-s-promotion"></i> {{ $ts('debugApi') }}</span>
        <doc-debug :item="debugItem" :internal="false" />
      </el-tab-pane>
      <el-tab-pane v-for="page in extPageData" :key="page.id" :label="page.title" :label-content="() => page"></el-tab-pane>
    </el-tabs>
    <div v-show="extViewShow">
      <mavon-editor
        v-model="extViewContent"
        :boxShadow="false"
        :subfield="false"
        defaultOpen="preview"
        :editable="false"
        :toolbarsFlag="false"
      />
    </div>
  </div>
</template>
<script>
import DocView from '../doc/DocView'
import DocDebug from '@/components/DocDebug'
import { mavonEditor } from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'
export default {
  components: { DocView, DocDebug, mavonEditor },
  data() {
    return {
      item: {},
      debugItem: {},
      hasData: false,
      setting: {
        gatewayUrl: '',
        showDebug: 0,
        globalParams: [],
        globalReturns: []
      },
      extPageData: [],
      extViewContent: '',
      extViewShow: false
    }
  },
  created() {
    this.initData()
  },
  methods: {
    async initData() {
      const docId = this.$route.params.docId
      const projectId = this.$route.params.showId
      if (docId) {
        const data = await this.getDocViewData(docId)
        this.setting = await this.getSetting(projectId)
        this.formatGlobal(this.setting.globalParams)
        this.formatGlobal(this.setting.globalReturns)
        if (this.setting.globalParams.length > 0) {
          data.globalParams = this.setting.globalParams
        }
        if (this.setting.globalReturns.length > 0) {
          data.globalReturns = this.setting.globalReturns
        }
        if (this.setting.gatewayUrl) {
          data.url = this.setting.gatewayUrl
        }
        this.initExtPage(projectId)
        this.initDocInfoView(data)
        this.item = data
        this.setTitle(data.name)
        this.hasData = true
      }
    },
    initExtPage(projectId) {
      this.get('/compose/additional/listvisible', { projectId: projectId }, resp => {
        this.extPageData = resp.data
      })
    },
    async getSetting(projectId) {
      return new Promise(ok => {
        this.get('/compose/project/setting/getall', { id: projectId }, resp => {
          ok(resp.data)
        })
      })
    },
    async getDocViewData(docId) {
      return new Promise(ok => {
        this.get('/doc/viewShow', { id: docId }, function(resp) {
          ok(resp.data)
        })
      })
    },
    formatGlobal(globalParams) {
      globalParams.forEach(row => {
        row.id = this.nextId() + ''
      })
    },
    onTabSelect(tab) {
      const fun = tab.labelContent
      this.extViewShow = fun !== undefined
      if (tab.labelContent) {
        this.extViewShow = true
        const page = fun()
        this.get('/compose/additional/get', { id: page.id }, resp => {
          const data = resp.data
          this.$nextTick(() => {
            this.extViewContent = data.content
          })
        })
      } else {
        this.selectTab(tab.name)
      }
    },
    selectTab(name) {
      this[`${name}Item`] = this.item
    }
  }
}
</script>
