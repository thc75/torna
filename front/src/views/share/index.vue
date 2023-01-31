<template>
  <div class="doc-view-container">
    <el-tabs v-if="showDoc" v-model="active" @tab-click="onTabSelect">
      <el-tab-pane name="info">
        <span slot="label"><i class="el-icon-document" /> {{ $t('apiInfo') }}</span>
        <doc-view ref="docView" :show-opt-bar="false" :init-subscribe="false" :item="infoItem" />
      </el-tab-pane>
      <el-tab-pane v-if="isShowDebug && infoItem.debugEnvs.length > 0" name="debug">
        <span slot="label"><i class="el-icon-s-promotion" /> {{ $t('debugApi') }}</span>
        <doc-debug :item="debugItem" />
      </el-tab-pane>
    </el-tabs>
    <el-tabs v-if="showDubbo" v-model="active" @tab-click="onTabSelect">
      <el-tab-pane name="info">
        <span slot="label"><i class="el-icon-document"></i> {{ $t('apiInfo') }}</span>
        <dubbo-view ref="docViewDubbo" :show-opt-bar="false" :init-subscribe="false" />
      </el-tab-pane>
    </el-tabs>
    <div v-if="showCustom">
      <doc-view-custom :doc-info="item" :show-opt-bar="false" :init-subscribe="false" />
    </div>
  </div>
</template>
<script>
import DocView from '@/components/DocView'
import DubboView from '@/components/DubboView'
import DocDebug from '@/components/DocDebug'
import DocViewCustom from '@/components/DocViewCustom'

export default {
  components: { DocView, DocDebug, DubboView, DocViewCustom },
  data() {
    return {
      active: 'info',
      load: false,
      item: {
        type: 0
      },
      infoItem: {},
      debugItem: {},
      mockItem: {},
      isShowDebug: false
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
      return this.item.type === this.getEnums().DOC_TYPE.CUSTOM
    }
  },
  created() {
    const docId = this.$route.params.docId
    const shareId = this.$route.params.shareId
    if (docId) {
      this.$nextTick(() => {
        this.get('/doc/share/view', { docId: docId, shareConfigId: shareId }, function(resp) {
          this.load = true
          const data = resp.data
          this.initDocInfoView(data)
          this.item = data
          this.infoItem = data
          this.setTitle(data.name)
          this.showDebug()
          if (this.item.type === this.getEnums().DOC_TYPE.DUBBO) {
            this.$nextTick(() => {
              this.$refs.docViewDubbo.setData(this.item)
            })
          }
        })
      })
    }
  },
  methods: {
    onTabSelect(tab) {
      this[`${tab.name}Item`] = this.item
    },
    showDebug() {
      const shareId = this.$route.params.shareId
      if (shareId) {
        this.get('/share/get', { id: shareId }, resp => {
          this.isShowDebug = resp.data.isShowDebug === 1
        })
      }
    }
  }
}
</script>
