<template>
  <div>
    <mavon-editor
      ref="md"
      v-model="content"
      :ishljs="true"
      :editable="editable"
      :placeholder="placeholder"
      :box-shadow="false"
      :subfield="false"
      :tab-size="2"
      :toolbars-flag="showInfo.toolbarsFlag"
      :default-open="showInfo.defaultOpen"
      code-style="atom-one-dark"
      :toolbars="toolbars"
      :style="{'font-size': fontSize + 'px', 'min-height': minHeight + 'px'}"
    >
    </mavon-editor>
  </div>
</template>
<script>

import { mavonEditor } from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'

export default {
  name: 'RichTextEditor',
  components: {
    mavonEditor
  },
  props: {
    value: {
      type: String,
      default: ''
    },
    editable: {
      type: Boolean,
      required: false,
      default: true
    },
    placeholder: {
      type: String,
      required: false,
      default: ''
    },
    height: {
      type: [Number, String],
      required: false,
      default: 250
    },
    minHeight: {
      type: [Number, String],
      required: false,
      default: 250
    },
    fontSize: {
      type: [Number, String],
      required: false,
      default: 13
    }
  },
  data() {
    return {
      content: '',
      showInfo: {
        defaultOpen: null,
        toolbarsFlag: true
      },
      toolbars: {
        bold: true, // 粗体
        italic: true, // 斜体
        header: true, // 标题
        underline: true, // 下划线
        strikethrough: true, // 中划线
        mark: false, // 标记
        superscript: true, // 上角标
        subscript: true, // 下角标
        quote: true, // 引用
        ol: true, // 有序列表
        ul: true, // 无序列表
        imagelink: false, // 图片链接
        code: true, // code
        fullscreen: false, // 全屏编辑
        readmodel: false, // 沉浸式阅读
        help: true, // 帮助
        undo: false, // 上一步
        redo: false, // 下一步
        trash: true, // 清空
        navigation: false, // 导航目录
        preview: true
      }
    }
  },
  computed: {},
  watch: {
    value: {
      handler: function(n, o) {
        this.content = this.value
      }
    },
    content: {
      handler: function(n, o) {
        this.$emit('input', n)
      }
    }
  },
  created() {
    if (!this.editable) {
      this.showInfo = {
        defaultOpen: 'preview',
        toolbarsFlag: false
      }
    }
  },
  mounted() {
  },
  methods: {}
}
</script>

<style scoped>
</style>
