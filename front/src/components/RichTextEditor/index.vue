<template>
  <div>
    <ckeditor v-model="content" :editor="editor" :config="editorConfig" />
  </div>
</template>
<script>
import CKEditor from '@ckeditor/ckeditor5-vue2'
import ClassicEditor from '@torna/ckeditor5'

export default {
  name: 'RichTextEditor',
  components: {
    ckeditor: CKEditor.component
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
      editor: ClassicEditor,
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
        placeholder: this.placeholder || '请输入内容'
      },
      editMode: this.mode // 'default' or 'simple'
    }
  },
  watch: {
    value(val) {
      this.content = val
    },
    content(val) {
      this.$emit('input', val)
    }
  }
}
</script>
