<template>
  <div>
    <el-alert v-if="showTip" type="info" title="" :closable="false">
      <div slot="title">
        {{ $t('RichTextEditor.imgUploadInfo') }} <help-link :button-text="$t('RichTextEditor.imgHelpBtnText')" style="margin-left: 20px;" to="/help?id=upload" />
      </div>
    </el-alert>
    <ckeditor ref="ckeditor" v-model="content" :editor="editor" :config="editorConfig" @ready="onReady" />
  </div>
</template>
<script>
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
    showTip: {
      type: Boolean,
      default: true
    },
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
      this.content = val || ''
    },
    content(val) {
      this.$emit('input', val)
    }
  },
  methods: {
    onReady(editor) {
      const scope = this
      // 自定义上传图片插件
      editor.plugins.get('FileRepository').createUploadAdapter = loader => {
        return new MyUploadAdapter(loader, scope)
      }
    }
  }
}
</script>
