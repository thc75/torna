<template>
  <div>
    <div :style="`border: 1px solid #DCDFE6;`">
      <Toolbar
        style="border-bottom: 1px solid #DCDFE6"
        :editor="editor"
        :defaultConfig="toolbarConfig"
        :mode="editMode"
      />
      <Editor
        v-model="content"
        :style="`height: ${height}px; overflow-y: hidden;`"
        :defaultConfig="editorConfig"
        :mode="editMode"
        @onCreated="onCreated"
      />
    </div>
  </div>
</template>
<script>
// https://www.wangeditor.com/
import '@wangeditor/editor/dist/css/style.css'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'

export default {
  name: 'RichTextEditor',
  components: {
    Editor, Toolbar
  },
  props: {
    value: {
      type: String,
      default: ''
    },
    mode: {
      type: String,
      default: 'simple'
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
      default: 200
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
      editor: null,
      html: '',
      toolbarConfig: {
        // 排除
        excludeKeys: [
          'group-image',
          'group-video',
          'insertVideo',
          'fullScreen'
        ]
      },
      editorConfig: {
        placeholder: this.placeholder || '请输入内容',
        height: '200px'
      },
      editMode: this.mode // 'default' or 'simple'
    }
  },
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
  beforeDestroy() {
    const editor = this.editor
    if (editor == null) return
    editor.destroy() // 组件销毁时，及时销毁编辑器
  },
  methods: {
    onCreated(editor) {
      this.editor = Object.seal(editor) // 一定要用 Object.seal() ，否则会报错
    }
  }
}
</script>
