<template>
  <el-container>
    <el-aside width="200px" style="padding: 20px">
      <el-button type="primary" size="mini" @click="addNew">添加国际化</el-button>
    </el-aside>
    <el-main>
      <el-form v-if="formData.id || formData.isNew" :model="formData">
        <el-form-item label="语言" prop="lang">
          <el-input
            v-model="formData.lang"
            placeholder="语言简称，如：zh-CN"
          />
        </el-form-item>
        <el-form-item label="语言描述" prop="description">
          <el-input
            v-model="formData.description"
            placeholder="语言描述，如：简体中文"
          />
        </el-form-item>
        <el-form-item label="翻译内容">
          <el-alert title="key不变，翻译value值，{0}为占位符不用翻译。完成后前往【个人中心】-【系统设置】-【语言】查看" :closable="false" />
          <editor
            ref="requestEditor"
            v-model="formData.content"
            lang="json"
            theme="chrome"
            :height="requestEditorConfig.height"
            class="normal-boarder"
            :options="requestEditorConfig"
            @init="requestEditorInit"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary">保存</el-button>
        </el-form-item>
      </el-form>
    </el-main>
  </el-container>
</template>
<script>
import mappingZh from '@/utils/i18n/languages/zh-cn'

export default {
  name: 'I18nSetting',
  components: { editor: require('vue2-ace-editor') },
  data() {
    return {
      formData: {
        id: '',
        lang: '',
        description: '',
        content: '',
        isNew: false
      },
      requestEditorConfig: {
        // 去除编辑器里的竖线
        showPrintMargin: false,
        height: 500
      }
    }
  },
  methods: {
    reload() {

    },
    addNew() {
      this.formData = {
        id: '',
        lang: '',
        description: '',
        content: this.formatJson(mappingZh),
        isNew: true
      }
    },
    requestEditorInit: function(editor) {
      // language extension prerequsite...
      require('brace/ext/language_tools')
      // language
      require('brace/mode/json')
      require('brace/theme/chrome')
      // 监听值的变化
      const that = this
      editor.on('blur', event => {
        that.onBodyBlur()
      })
    },
    onBodyBlur() {
      const val = this.formData.content
      if (val) {
        try {
          this.formData.content = this.formatJson(JSON.parse(val))
          // eslint-disable-next-line no-empty
        } catch (e) {
          this.tipError('格式错误，确保为json')
        }
      }
    }
  }
}
</script>
