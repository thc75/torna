<template>
  <div class="app-container">
    <el-tabs v-show="item.type === getEnums().DOC_TYPE.HTTP && load" v-model="active" type="card" @tab-click="onTabSelect">
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
    <el-tabs v-show="item.type === getEnums().DOC_TYPE.DUBBO && load" v-model="active" type="card" @tab-click="onTabSelect">
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
      load: false,
      item: {},
      infoItem: {},
      debugItem: {},
      mockItem: {}
    }
  },
  created() {
    const docId = this.$route.params.docId
    this.$nextTick(() => {
      if (docId) {
        this.get('/doc/view/detail', { id: docId }, function(resp) {
          this.load = true
          const data = resp.data
          this.initDocInfoView(data)
          this.item = data
          this.selectTab('info')
          this.setTitle(data.name)
        })
      }
    })
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
