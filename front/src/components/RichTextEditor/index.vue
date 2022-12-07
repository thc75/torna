<template>
  <div>
    <el-alert type="info" title="" :closable="false">
      <div slot="title">
        {{ $ts('imgUploadInfo') }} <help-link :button-text="$ts('imgHelpBtnText')" style="margin-left: 20px;" to="/help?id=upload" />
      </div>
    </el-alert>
    <ckeditor v-model="content" :editor="editor" :config="editorConfig" @ready="onReady" />
  </div>
</template>
<script>
$addI18n({
  'imgUploadInfo': { 'zh': '支持剪贴板图片上传，本地图片上传，URL图片插入', 'en': 'Support clipboard image upload, URL image insert, local image upload insert' },
  'imgHelpBtnText': { 'zh': '图片上传设置', 'en': 'Upload image setting' }
})
import CKEditor from '@ckeditor/ckeditor5-vue2'
import ClassicEditor from '@torna/ckeditor5'
import { get_token, get_server_url } from '@/utils/http'
import HelpLink from '@/components/HelpLink'
import axios from 'axios'

class MyUploadAdapter {
  constructor(loader, scope) {
    // Save Loader instance to update upload progress.
    this.loader = loader
    this.scope = scope
  }

  async upload() {
    const data = new FormData()
    data.append('file', await this.loader.file)
    return new Promise((resolve, reject) => {
      axios({
        url: `${get_server_url()}/uploadFile`,
        method: 'post',
        data,
        headers: {
          'Authorization': get_token()
        },
        withCredentials: true // true 为不允许带 token, false 为允许，可能会遇到跨域报错：Error: Network Error 弹窗提示（感谢@ big_yellow 指正）
      }).then(res => {
        const resData = res.data
        if (resData.code !== '0') {
          this.scope.tipError('上传失败：' + resData.msg)
          return
        }
        const data = resData.data
        // 方法返回数据格式： {default: "url"}
        const returnUrl = data.url || ''
        let url
        // 如果是全路径
        if (returnUrl.startsWith('http://') || returnUrl.startsWith('https://')) {
          url = returnUrl
        } else {
          url = get_server_url() + returnUrl
        }

        const resolveData = {
          default: url
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
    ckeditor: CKEditor.component, HelpLink
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
      const scope = this
      // 自定义上传图片插件
      editor.plugins.get('FileRepository').createUploadAdapter = loader => {
        return new MyUploadAdapter(loader, scope)
      }

      editor.ui.componentFactory.add('timestamp', () => {
        // The button will be an instance of ButtonView.
        const button = new ButtonView()

        button.set({
          label: 'Timestamp',
          withText: true
        })

        return button
      })
    }
  }
}
</script>
