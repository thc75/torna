<template>
  <el-container>
    <el-aside width="200px" style="padding: 20px">
      <el-button type="primary" size="mini" @click="addNew">添加国际化</el-button>
      <el-tree
        :data="list"
        :props="defaultProps"
        :highlight-current="true"
        :expand-on-click-node="true"
        :empty-text="$t('noData')"
        node-key="id"
        style="margin-top: 20px"
        @node-click="onNodeClick"
      />
    </el-aside>
    <el-main>
      <el-form v-if="formData.id || formData.isNew" ref="i18nForm"  :model="formData" :rules="formRules">
        <el-form-item label="语言简称" prop="lang">
          <el-alert>
            <div slot="title">
              建议跟ElementUI简称保持一致，
              <el-link
                type="primary"
                href="https://github.com/ElemeFE/element/tree/dev/src/locale/lang"
                target="_blank"
              >点击查看</el-link><br/>
              系统内置了zh-CN（简体中文）,en（英文）两个配置，可在此进行覆盖，如要覆盖简体中文填zh-CN
            </div>
          </el-alert>
          <el-input
            v-model="formData.lang"
            placeholder="语言简称，如：zh-TW"
            show-word-limit
            maxlength="10"
          />
        </el-form-item>
        <el-form-item label="语言描述" prop="description">
          <el-input
            v-model="formData.description"
            placeholder="语言描述，如：繁体中文"
            show-word-limit
            maxlength="50"
          />
        </el-form-item>
        <el-form-item label="翻译内容">
          <el-alert :closable="false">
            <div slot="title">
              json内容，key不变，翻译value值，{0}为占位符不用翻译。完成后前往
              <el-link type="primary" :href="`${getBaseUrl()}/#/user/systemSetting`" target="_blank">【个人中心】</el-link>查看
            </div>
          </el-alert>
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
          <el-button type="primary" @click="onSave">保存</el-button>
          <el-button v-show="formData.id.length > 0" type="danger" @click="onDel">删除</el-button>
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
      list: [],
      defaultProps: {
        children: 'children',
        label: 'description'
      },
      formData: {
        id: '',
        lang: '',
        description: '',
        content: '',
        isNew: false
      },
      formRules: {
        lang: [
          { required: true, message: $t('notEmpty'), trigger: ['blur', 'change'] }
        ],
        description: [
          { required: true, message: $t('notEmpty'), trigger: ['blur', 'change'] }
        ]
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
      this.get('admin/i18n/lang/list', {}, resp => {
        this.list = resp.data
      })
    },
    // 树点击事件
    onNodeClick(data, node, tree) {
      this.get('admin/i18n/get', { id: data.id }, resp => {
        this.formData = resp.data
      })
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
    },
    onSave() {
      this.$refs.i18nForm.validate(valid => {
        if (valid) {
          this.post('admin/i18n/save', this.formData, resp => {
            this.tipSuccess($t('saveSuccess'))
            if (!this.formData.id) {
              this.formData.id = resp.data.id
            }
            this.reload()
          })
        }
      })
    },
    onDel() {
      this.confirm(this.$t('deleteConfirm', this.formData.description), () => {
        const data = {
          id: this.formData.id
        }
        this.post('admin/i18n/delete', data, () => {
          this.formData.id = ''
          this.tipSuccess(this.$t('deleteSuccess'))
          this.reload()
        })
      })
    }
  }
}
</script>
