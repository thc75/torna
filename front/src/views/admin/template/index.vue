<template>
  <div>
    <el-container>
      <el-aside width="200px" style="padding: 20px">
        <el-button type="primary" size="mini" @click="addNew">{{ $t('CodeGenTemplate.addTemplate') }}</el-button>
        <template-tree
          ref="templateTree"
          :node-click="onNodeClick"
        />
      </el-aside>
      <el-main>
        <el-alert
          :title="$t('CodeGenTemplate.codeGenTitle')"
          type="info"
          :closable="false"
          class="btn-alert"
          style="margin-bottom: 20px;"
        >
          <el-popover
            placement="bottom"
            :title="$t('CodeGenTemplate.howToUse')"
            trigger="click"
          >
            <div class="tips">{{ $t('CodeGenTemplate.tipText') }}</div>
            <img :src="`${getBaseUrl()}/static/help/images/gen.png`"/>
            <el-link slot="reference" type="primary" icon="el-icon-question" style="margin-left: 20px">
              {{ $t('CodeGenTemplate.howToUseLink') }}
            </el-link>
          </el-popover>
        </el-alert>

        <el-form
          v-if="showRight"
          ref="i18nForm"
          label-width="80px"
          :model="formData"
          :rules="formRules"
        >
          <el-form-item :label="$t('CodeGenTemplate.templateName')" prop="name">
            <el-input
              v-model="formData.name"
              :placeholder="$t('CodeGenTemplate.templateName')"
              show-word-limit
              maxlength="50"
              style="width: 500px"
            />
          </el-form-item>
          <el-form-item :label="$t('CodeGenTemplate.group')" prop="groupName">
            <el-select
              v-model="formData.groupName"
              filterable
              allow-create
              default-first-option
              clearable
              :placeholder="$t('CodeGenTemplate.groupPlaceholder')"
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
          <el-form-item :label="$t('CodeGenTemplate.templateContent')" prop="content">
            <el-alert :closable="false">
              <div slot="title">
                {{ $t('CodeGenTemplate.velocityTip') }}
                <el-link type="primary" :underline="false" @click="$refs.help.open('static/help/velocity.md')">
                  {{ $t('CodeGenTemplate.syntaxRef') }}
                </el-link>
                <span class="split">|</span>
                {{ $t('CodeGenTemplate.exampleTemplates') }}:
                <el-link type="primary" :underline="false" style="margin-left: 10px" @click="insertTemplate('javaBean')">
                  {{ $t('CodeGenTemplate.javaClass') }}
                </el-link>
                <el-link type="primary" :underline="false" style="margin-left: 10px" @click="insertTemplate('curl')">
                  curl
                </el-link>
                <el-link type="primary" :underline="false" style="margin-left: 10px" @click="insertTemplate('vueTable')">
                  {{ $t('CodeGenTemplate.vueTable') }}
                </el-link>
              </div>
            </el-alert>
            <codemirror
              ref="editor"
              v-model="formData.content"
              :options="cmOptions"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="onSave">{{ $t('save') }}</el-button>
            <el-button v-show="!formData.isNew" type="danger" @click="onDel">{{ $t('delete') }}</el-button>
            <el-button v-show="!formData.isNew" type="info" @click="onCopy">{{ $t('copy') }}</el-button>
          </el-form-item>
        </el-form>
      </el-main>
      <el-aside v-if="showRight" style="width: 350px">
        <div :class="{ hasFix: 'hasFix' }" style="font-size: 14px;margin-left: 10px">
          <h4 style="margin: 5px 0">
            {{ $t('CodeGenTemplate.velocityVars') }}
            <span class="velocity-tip">
              {{ $t('CodeGenTemplate.clickToInsert') }}
            </span>
          </h4>
          <div class="velocity-var">
            <el-tree
              :data="rightTreeData"
              :props="defaultPropsRight"
              accordion
            >
              <span slot-scope="{ data }">
                <span v-if="data.children && data.children.length > 0">
                  {{ data.label }}
                </span>
                <span v-else>
                  <a @click="onExpressionClick(data)">{{ data.label }}</a>
                  <span v-show="data.description">：{{ data.description }}</span>
                </span>
              </span>
            </el-tree>
          </div>
        </div>
      </el-aside>
    </el-container>
    <help ref="help" />
  </div>
</template>
<style>
.CodeMirror {
  height: auto !important;
}
.el-form-item--mini .el-form-item__content,
.el-form-item--mini .el-form-item__label,
.el-form-item__content {
  line-height: 20px;
}

.velocity-tip {
  color: #606266;
  font-size: 13px;
  font-weight: normal;
}
.velocity-var {
}
.velocity-var li {
  font-size: 14px;
  color: #606266;
  line-height: 26px;
}
.velocity-var a {
  color: #409EFF;
  font-weight: 500;
}
.velocity-var a:hover {
  color: rgba(64, 158, 255, 0.75);
}
.hasFix {
  position: fixed;
  top: 50px;
}
.el-dropdown-link {
  cursor: pointer;
  color: #409EFF;
}
.el-icon-arrow-down {
  font-size: 12px;
}
span.split {
  color: #ccc;
  margin: 0 3px;
}
</style>
<script>
import Help from '@/components/Help'
import TemplateTree from '@/components/TemplateTree'
import { codemirror } from 'vue-codemirror'
import 'codemirror/theme/neat.css'
import 'codemirror/lib/codemirror.css'
require('codemirror/mode/velocity/velocity')

export default {
  name: 'I18nSetting',
  components: { Help, TemplateTree, codemirror },
  data() {
    return {
      list: [],
      groupNameList: [],
      rightTreeData: [],
      defaultProps: {
        children: 'children',
        label: 'label'
      },
      defaultPropsRight: {
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
        ],
        content: [
          { required: true, message: $t('notEmpty'), trigger: ['blur', 'change'] }
        ]
      },
      cmOptions: {
        value: '',
        mode: 'text/velocity',
        theme: 'neat',
        lineNumbers: true,
        readOnly: false,
        autofocus: true,
        fontSize: 14
      },
      systemSettingData: {
        lang: 'zh-CN'
      },
      languageOptions: [
        { label: '简体中文', value: 'zh-CN' },
        { label: 'English', value: 'en' }
      ],
      needFix: false,
      treeId: 1
    }
  },
  computed: {
    showRight() {
      return this.formData.id || this.formData.isNew
    }
  },
  created() {
    this.reload()
    this.loadVelocityVar()
    window.addEventListener('scroll', this.handlerScroll)
  },
  methods: {
    reload() {
      this.loadTree()
      this.loadGroupName()
    },
    loadTree() {
      this.$refs.templateTree && this.$refs.templateTree.reload()
    },
    handlerScroll() {
      const scrollTop = window.pageYOffset ||
        document.documentElement.scrollTop ||
        document.body.scrollTop
      this.needFix = scrollTop > 50
    },
    loadVelocityVar() {
      this.getFile(`static/help/template.json?q=${new Date().getTime()}`, content => {
        const list = content.data
        this.rightTreeData = list
      })
    },
    getTreeId() {
      return this.treeId++
    },
    // 树点击事件
    onNodeClick(data, node, tree) {
      if (data.id) {
        this.get('admin/gen/template/detail', { id: data.id }, resp => {
          this.formData = resp.data
        })
      }
    },
    onExpressionClick(data) {
      const exp = data.expression || data.label
      const codemirror = this.$refs.editor.codemirror
      // 插入表达式
      codemirror.replaceSelection(exp)
      // 重新获得光标
      codemirror.focus()
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
    onSave() {
      this.$refs.i18nForm.validate(valid => {
        if (valid) {
          if (this.formData.isNew) {
            this.post('admin/gen/template/save', this.formData, resp => {
              this.formData.id = resp.data
              this.formData.isNew = false
              this.tipSuccess($t('saveSuccess'))
              this.reload()
            })
          } else {
            this.post('admin/gen/template/update', this.formData, resp => {
              this.tipSuccess($t('saveSuccess'))
              this.reload()
            })
          }
        }
      })
    },
    onDel() {
      this.confirm(this.$t('deleteConfirm', this.formData.name), () => {
        const data = {
          id: this.formData.id
        }
        this.post('admin/gen/template/delete', data, () => {
          this.formData.id = ''
          this.tipSuccess(this.$t('deleteSuccess'))
          this.reload()
        })
      })
    },
    onCopy() {
      this.formData.isNew = true
      this.formData.name = this.formData.name + ' copy'
    },
    insertTemplate(file) {
      this.getFile(`static/help/template/${file}.txt?q=${new Date().getTime()}`, content => {
        this.formData.content = content
      })
    }
  }
}
</script>
