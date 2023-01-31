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
        <el-dropdown trigger="click" @command="handleCommand">
          <el-tooltip placement="top" :content="$t('export')">
            <el-button type="text" class="icon-button" icon="el-icon-download" />
          </el-tooltip>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item :command="onExportHtml">{{ $t('exportHtml') }}</el-dropdown-item>
            <el-dropdown-item :command="onExportWord">{{ $t('exportWord') }}</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
      <div class="item">
        <el-tooltip placement="top" :content="$t('viewConst')">
          <el-button type="text" class="icon-button" icon="el-icon-collection" @click="showConst" />
        </el-tooltip>
      </div>
    </div>
    <div v-html="docInfo.description"></div>
    <const-view ref="constView" />
  </div>
</template>
<script>
import ExportUtil from '@/utils/export'
import ConstView from '@/components/ConstView'

export default {
  components: { ConstView },
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
    }
  }
}
</script>
