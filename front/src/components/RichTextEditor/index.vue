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
      editorConfig: {
        placeholder: this.placeholder || '请输入内容'
      }
    }
  },
  watch: {
    value(val) {
      this.content = val
    },
    content(val) {
      this.$emit('input', val)
    }
  },
  mounted() {
    this.$nextTick(() => {
      const els = document.getElementsByClassName('ck-editor__main');
      if (els != null && els.length > 0) {
        els[0].style.height = this.height + 'px'
      }
    })
  }
}
</script>
