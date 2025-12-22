<template>
  <span>
    <el-tooltip
      v-if="showTip"
      effect="light"
      :content="$t('clickCopy')"
      :placement="placement"
      :open-delay="openDelay"
    >
      <el-link :underline="false" class="copy-text" @click.stop="onCopy">
        <div v-if="showContentHtml">
          <div v-html="showContentHtml" />
        </div>
        <span v-else>
          {{ showContent }}
        </span>
      </el-link>
    </el-tooltip>
    <span v-else>
      <el-link :underline="false" class="copy-text" @click.stop="onCopy">
        <div v-if="showContentHtml">
          <div v-html="showContentHtml" />
        </div>
        <span v-else>
          {{ showContent }}
        </span>
      </el-link>
    </span>
  </span>
</template>
<style>
.copy-text {
  vertical-align: text-top;
  font-size: 14px !important;
  font-weight: normal !important;
}
</style>
<script>
export default {
  name: 'CopyText',
  props: {
    showTip: {
      type: Boolean,
      default: true
    },
    copyContent: {
      type: String,
      required: true
    },
    showContent: {
      type: String,
      required: false,
      default: ''
    },
    showContentHtml: {
      type: String,
      required: false,
      default: ''
    },
    placement: {
      type: String,
      default: 'top'
    },
    openDelay: {
      type: Number,
      default: 200
    }
  },
  data() {
    return {
      dlgShow: false,
      docInfo: {
        type: -1,
        description: ''
      }
    }
  },
  methods: {
    onCopy() {
      this.copyText(this.copyContent)
    }
  }
}
</script>
