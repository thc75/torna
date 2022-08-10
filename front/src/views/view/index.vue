<template>
  <div class="doc-view-container">
    <el-tabs v-show="showDoc" v-model="active" @tab-click="onTabSelect">
      <el-tab-pane name="info">
        <span slot="label"><i class="el-icon-document"></i> {{ $ts('apiInfo') }}</span>
        <doc-view ref="docView" :item="infoItem" />
      </el-tab-pane>
      <el-tab-pane name="debug">
        <span slot="label"><i class="el-icon-s-promotion"></i> {{ $ts('debugApi') }}</span>
        <doc-debug :item="debugItem" />
      </el-tab-pane>
      <el-tab-pane name="mock">
        <span slot="label"><i class="el-icon-s-marketing"></i> Mock</span>
        <mock :item="mockItem" />
      </el-tab-pane>
    </el-tabs>
    <el-tabs v-show="showDubbo" v-model="active" type="card" @tab-click="onTabSelect">
      <el-tab-pane name="info">
        <span slot="label"><i class="el-icon-document"></i> {{ $ts('apiInfo') }}</span>
        <dubbo-view ref="docView" :item="infoItem" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script>
import DocView from '../doc/DocView'
import DubboView from '../doc/DubboView'
import DocDebug from '@/components/DocDebug'
import Mock from '@/components/Mock'

export default {
  components: { DocView, DocDebug, Mock, DubboView },
  data() {
    return {
      active: 'info',
      item: {
        type: 1
      },
      infoItem: {},
      debugItem: {},
      mockItem: {}
    }
  },
  computed: {
    showDoc() {
      return this.item.type === this.getEnums().DOC_TYPE.HTTP
    },
    showDubbo() {
      return this.item.type === this.getEnums().DOC_TYPE.DUBBO
    }
  },
  created() {
    const docId = this.$route.params.docId
    if (docId) {
      this.$nextTick(() => {
        this.get('/doc/view/detail', { id: docId }, function(resp) {
          const data = resp.data
          this.setProjectId(data.projectId)
          this.initDocInfoView(data)
          this.item = data
          this.selectTab('info')
          this.setTitle(data.name)
        })
      })
    } else {
      this.item.type = -9
    }
  },
  methods: {
    onTabSelect(tab) {
      this.selectTab(tab.name)
    },
    selectTab(name) {
      this[`${name}Item`] = this.item
    }
  }
}
</script>
