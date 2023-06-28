<template>
  <div class="doc-view-container">
    <el-tabs v-if="showDoc" v-model="active" @tab-click="onTabSelect">
      <el-tab-pane name="info">
        <span slot="label"><i class="el-icon-document"></i> {{ $t('apiInfo') }}</span>
        <doc-view ref="docView" :item="infoItem" />
      </el-tab-pane>
      <el-tab-pane name="debug">
        <span slot="label"><i class="el-icon-s-promotion"></i> {{ $t('debugApi') }}</span>
        <doc-debug :item="debugItem" />
      </el-tab-pane>
      <el-tab-pane name="mock">
        <span slot="label"><i class="el-icon-s-marketing"></i> Mock</span>
        <mock :item="mockItem" />
      </el-tab-pane>
    </el-tabs>
    <el-tabs v-if="showDubbo" v-model="active" @tab-click="onTabSelect">
      <el-tab-pane name="info">
        <span slot="label"><i class="el-icon-document"></i> {{ $t('apiInfo') }}</span>
        <dubbo-view ref="docViewDubbo" />
      </el-tab-pane>
    </el-tabs>
    <div v-if="showCustom">
      <doc-view-custom :doc-info="item" />
    </div>
  </div>
</template>
<script>
import DocView from '@/components/DocView'
import DubboView from '@/components/DubboView'
import DocDebug from '@/components/DocDebug'
import Mock from '@/components/Mock'
import DocViewCustom from '@/components/DocViewCustom'

export default {
  components: { DocView, DocDebug, Mock, DubboView, DocViewCustom },
  data() {
    return {
      active: 'info',
      item: {
        type: 0
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
    },
    showCustom() {
      return this.item.type === this.getEnums().DOC_TYPE.CUSTOM || this.item.type === this.getEnums().DOC_TYPE.MARKDOWN
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
          if (this.item.type === this.getEnums().DOC_TYPE.DUBBO) {
            this.$nextTick(() => {
              this.$refs.docViewDubbo.setData(this.item)
            })
          }
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
