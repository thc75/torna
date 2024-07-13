<template>
  <div>
    <el-container>
      <el-aside width="200px" style="padding: 20px">
        <el-button type="primary" size="mini" @click="addNew">添加模板</el-button>
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
        <el-form v-if="formData.id || formData.isNew" ref="i18nForm" label-width="80px" :model="formData" :rules="formRules">
          <el-form-item label="模板名称" prop="name">
            <el-input
              v-model="formData.name"
              placeholder="模板名称"
              show-word-limit
              maxlength="50"
              style="width: 500px"
            />
          </el-form-item>
          <el-form-item label="分组" prop="groupName">
            <el-select
              v-model="formData.groupName"
              filterable
              allow-create
              default-first-option
              clearable
              placeholder="输入可新建分组，不填为默认分组"
              show-word-limit
              maxlength="50"
              style="width: 500px"
            >
              <el-option
                v-for="item in groupNameList"
                :key="item"
                :label="item"
                :value="item"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="模板内容">
            <el-alert :closable="false">
              <div slot="title">
                基于Velocity，
                <el-link type="primary" :underline="false" @click="$refs.help.open('static/help/velocity.md')">
                  参考语法
                </el-link>
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
    <help ref="help" />
  </div>
</template>
<script>
import Help from '@/components/Help'

export default {
  name: 'I18nSetting',
  components: { Help, editor: require('vue2-ace-editor') },
  data() {
    return {
      list: [],
      groupNameList: [],
      defaultProps: {
        children: 'children',
        label: 'label'
      },
      formData: {
        id: '',
        name: '',
        groupName: '',
        content: '',
        isNew: false
      },
      formRules: {
        name: [
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
  created() {
    this.reload()
  },
  methods: {
    reload() {
      this.loadTree()
      this.loadGroupName()
    },
    loadTree() {
      this.get('admin/gen/template/tree', {}, resp => {
        this.list = resp.data
      })
    },
    // 树点击事件
    onNodeClick(data, node, tree) {
      if (data.id) {
        this.get('admin/gen/template/detail', { id: data.id }, resp => {
          this.formData = resp.data
        })
      }
    },
    addNew() {
      this.formData = {
        id: '',
        lang: '',
        description: '',
        content: '',
        isNew: true
      }
    },
    loadGroupName() {
      this.get('admin/gen/template/listGroupName', {}, resp => {
        this.groupNameList = resp.data
      })
    },
    requestEditorInit: function(editor) {
      // language extension prerequsite...
      require('brace/ext/language_tools')
      // language
      require('brace/mode/html')
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
          this.tipError('格式错误，确保为json')
        }
      }
      return false
    },
    onSave() {
      this.$refs.i18nForm.validate(valid => {
        if (valid) {
          if (this.formData.id) {
            this.post('admin/gen/template/update', this.formData, resp => {
              this.tipSuccess($t('saveSuccess'))
            })
          } else {
            this.post('admin/gen/template/save', this.formData, resp => {
              this.tipSuccess($t('saveSuccess'))
              if (!this.formData.id) {
                this.formData.id = resp.data.id
              }
              this.reload()
            })
          }

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
