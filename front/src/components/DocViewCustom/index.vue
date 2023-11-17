<template>
  <div>
    <h1 style="margin-bottom: 0">
      {{ docInfo.name }}
      <el-tooltip placement="top" :content="isSubscribe ? $t('cancelSubscribe') : $t('clickSubscribe')">
        <el-button
          v-show="showOptBar && docInfo.id"
          type="text"
          class="icon-button"
          :icon="isSubscribe ? 'el-icon-star-on' : 'el-icon-star-off'"
          style="font-size: 16px"
          @click="onSubscribe"
        />
      </el-tooltip>
    </h1>
    <span v-show="showOptBar" class="doc-modify-info">
      {{ docInfo.creatorName }} {{ $t('createdOn') }} {{ docInfo.gmtCreate }}，
      {{ docInfo.modifierName }} {{ $t('lastModifiedBy') }} {{ docInfo.gmtModified }}
    </span>
    <div v-show="showOptBar" class="show-opt-bar" style="float: right;">
      <div class="item">
        <el-tooltip placement="top" :content="$ts('changeHistory')">
          <el-button type="text" icon="el-icon-date" @click="onShowHistory"></el-button>
        </el-tooltip>
      </div>
      <div class="item">
        <el-dropdown trigger="click" @command="handleCommand">
          <el-tooltip placement="top" :content="$t('export')">
            <el-button type="text" class="icon-button" icon="el-icon-download" />
          </el-tooltip>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item v-show="docInfo.type === getEnums().DOC_TYPE.CUSTOM" :command="onExportHtml">{{ $t('exportHtml') }}</el-dropdown-item>
            <el-dropdown-item v-show="docInfo.type === getEnums().DOC_TYPE.MARKDOWN" :command="onExportMarkdown">{{ $t('exportMarkdown') }}</el-dropdown-item>
            <el-dropdown-item v-show="docInfo.type === getEnums().DOC_TYPE.CUSTOM" :command="onExportWord">{{ $t('exportWord') }}</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
      <div class="item">
        <el-tooltip placement="top" :content="$t('viewConst')">
          <el-button type="text" class="icon-button" icon="el-icon-collection" @click="showConst" />
        </el-tooltip>
      </div>
    </div>
    <div v-show="docInfo.type === getEnums().DOC_TYPE.CUSTOM" v-html="docInfo.description"></div>
    <mavon-editor
      v-show="docInfo.type === getEnums().DOC_TYPE.MARKDOWN"
      v-model="docInfo.description"
      :boxShadow="false"
      :subfield="false"
      defaultOpen="preview"
      :editable="false"
      :toolbarsFlag="false"
    />
    <doc-changelog-drawer ref="docChangelogDrawer" />
    <const-view ref="constView" />
  </div>
</template>
<style scoped>
/deep/ .v-note-wrapper .v-note-panel .v-note-show .v-show-content {
  background-color: #FFFFFF !important;
  padding: 0 !important;
}
.v-note-wrapper {
  background-color: #FFFFFF !important;
  border: 0 !important;
}
</style>
<script>
import ExportUtil from '@/utils/export'
import ConstView from '@/components/ConstView'
import { mavonEditor } from 'mavon-editor'
import DocChangelogDrawer from '@/components/DocChangelogDrawer'

export default {
  components: {DocChangelogDrawer, ConstView, mavonEditor },
  props: {
    docInfo: Object,
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
      isSubscribe: false
    }
  },
  watch: {
    docInfo(val) {
      this.initData(val)
    }
  },
  methods: {
    initData(docInfo) {
      this.loadSubscribe(docInfo.id)
    },
    onExportHtml() {
      ExportUtil.exportHtmlSinglePage(this.docInfo)
    },
    onExportWord() {
      ExportUtil.exportWordSinglePage(this.docInfo)
    },
    onExportMarkdown() {
      ExportUtil.exportMarkdownSinglePage(this.docInfo)
    },
    loadSubscribe(id) {
      if (!this.initSubscribe) {
        return
      }
      if (id && this.showOptBar) {
        this.get('/user/subscribe/doc/isSubscribe', { sourceId: id }, resp => {
          this.isSubscribe = resp.data
        })
      }
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
    },
    showConst() {
      this.$refs.constView.show(this.docInfo.moduleId)
    },
    onShowHistory() {
      this.$refs.docChangelogDrawer.show(this.docInfo.id)
    },
  }
}
</script>
