<template>
  <div>
    <ckeditor v-model="content" :editor="editor" :config="editorConfig" @ready="onReady" />
  </div>
</template>
<script>
import CKEditor from '@ckeditor/ckeditor5-vue2'
import ClassicEditor from '@torna/ckeditor5'
import { get_token, get_server_url } from '@/utils/http'
import axios from 'axios'

class MyUploadAdapter {
  constructor(loader) {
    // Save Loader instance to update upload progress.
    this.loader = loader
  }

  async upload() {
    const data = new FormData()
    data.append('file', await this.loader.file)
    return new Promise((resolve, reject) => {
      axios({
        url: `${get_server_url()}/upload/image`,
        method: 'post',
        data,
        headers: {
          'Authorization': get_token()
        },
        withCredentials: true // true 为不允许带 token, false 为允许，可能会遇到跨域报错：Error: Network Error 弹窗提示（感谢@ big_yellow 指正）
      }).then(res => {
        const resData = res.data
        const data = resData.data
        // 方法返回数据格式： {default: "url"}
        const resolveData = {
          default: get_server_url() + data.url
        }
        resolve(resolveData)
      }).catch(error => {
        reject(error)
      })
    })
  }
}

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
      const els = document.getElementsByClassName('ck-editor__main')
      if (els != null && els.length > 0) {
        els[0].style.height = this.height + 'px'
      }
    })
  },
  methods: {
    onReady(editor) {
      // 自定义上传图片插件
      editor.plugins.get('FileRepository').createUploadAdapter = loader => {
        return new MyUploadAdapter(loader)
      }
    }
  }
}
</script>
