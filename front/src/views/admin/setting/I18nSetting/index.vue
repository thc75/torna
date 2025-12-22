<template>
  <div>
    <div style="display: inline-block">
      <el-form :model="systemSettingData" size="mini" label-width="120px">
        <el-form-item :label="$t('Internationalization.defaultLang')">
          <el-select
            v-model="systemSettingData.lang"
            :placeholder="$t('pleaseSelect')"
            @change="onDefaultLangChange"
          >
            <el-option
              v-for="item in languageOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
          <span class="info-tip">{{ $t('Internationalization.defaultLangTip') }}</span>
        </el-form-item>
      </el-form>
    </div>
    <el-container>
      <el-aside width="200px" style="padding: 20px">
        <el-button type="primary" size="mini" @click="addNew">{{ $t('Internationalization.addI18n') }}</el-button>
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
        <el-form
          v-if="formData.id || formData.isNew"
          ref="i18nForm"
          :model="formData"
          :rules="formRules"
        >
          <el-form-item :label="$t('Internationalization.langCode')" prop="lang">
            <el-alert>
              <div slot="title">
                {{ $t('Internationalization.langCodeTip1') }}
                <el-link
                  type="primary"
                  href="https://github.com/ElemeFE/element/tree/master/src/locale/lang"
                  target="_blank"
                >{{ $t('Internationalization.viewLink') }}</el-link><br/>
                {{ $t('Internationalization.langCodeTip2') }}
              </div>
            </el-alert>
            <el-input
              v-model="formData.lang"
              :placeholder="$t('Internationalization.langCodePlaceholder')"
              show-word-limit
              maxlength="10"
            />
          </el-form-item>
          <el-form-item :label="$t('Internationalization.langDesc')" prop="description">
            <el-input
              v-model="formData.description"
              :placeholder="$t('Internationalization.langDescPlaceholder')"
              show-word-limit
              maxlength="50"
            />
          </el-form-item>
          <el-form-item :label="$t('Internationalization.translationContent')">
            <el-alert :closable="false">
              <div slot="title">
                {{ $t('Internationalization.translationTip1') }}
                <el-link
                  type="primary"
                  :href="`${getBaseUrl()}/#/user/systemSetting`"
                  target="_blank"
                >{{ $t('Internationalization.viewPersonalCenter') }}</el-link>
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
            <el-button type="primary" @click="onSave">{{ $t('save') }}</el-button>
            <el-button
              v-show="formData.id.length > 0"
              type="danger"
              @click="onDel"
            >{{ $t('delete') }}</el-button>
          </el-form-item>
        </el-form>
      </el-main>
    </el-container>
  </div>
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
      },
      systemSettingData: {
        lang: 'zh-CN'
      },
      languageOptions: [
        { label: '简体中文', value: 'zh-CN' },
        { label: 'English', value: 'en' }
      ]
    }
  },
  methods: {
    reload() {
      this.get('admin/i18n/lang/list', {}, resp => {
        this.list = resp.data
      })
      this.get('system/i18n/lang/default', {}, resp => {
        this.systemSettingData.lang = resp.data
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
    },
    formatContent() {
      const val = this.formData.content
      if (val) {
        try {
          this.formData.content = this.formatJson(JSON.parse(val))
          return true
          // eslint-disable-next-line no-empty
        } catch (e) {
          this.tipError(this.$t('parseError'))
        }
      }
      return false
    },
    onSave() {
      this.$refs.i18nForm.validate(valid => {
        if (valid && this.formatContent()) {
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
    },
    onDefaultLangChange(lang) {
      this.post(`admin/i18n/lang/setDefaultLang?lang=${lang}`, {}, () => {
        this.tipSuccess(this.$t('saveSuccess'))
      })
    }
  }
}
</script>
