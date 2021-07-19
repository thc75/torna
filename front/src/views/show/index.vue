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
    </el-tabs>
  </div>
</template>
<script>
import DocView from '../doc/DocView'
import DocDebug from '@/components/DocDebug'

export default {
  components: { DocView, DocDebug },
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
      }
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
        this.initDocInfoView(data)
        this.item = data
        this.setTitle(data.name)
        this.hasData = true
      }
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
      this.selectTab(tab.name)
    },
    selectTab(name) {
      this[`${name}Item`] = this.item
    }
  }
}
</script>
