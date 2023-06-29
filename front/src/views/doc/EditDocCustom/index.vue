<template>
  <div class="app-container">
    <el-form
      ref="docFormCustom"
      :model="docInfo"
      :rules="rulesCustom"
      label-position="left"
      class="text-form"
    >
      <table>
        <tr>
          <td>{{ $t('sourceFolder') }}</td>
          <td class="text-form-input">
            <el-form-item prop="parentId">
              <el-select v-model="docInfo.parentId" :placeholder="$t('pleaseSelect')" style="width: 300px;">
                <el-option :label="$t('empty')" :value="0">{{ $t('empty') }}</el-option>
                <el-option v-for="item in folders" :key="item.id" :label="item.name" :value="item.id">
                  {{ item.name }}
                </el-option>
              </el-select>
            </el-form-item>
          </td>
          <td>{{ $t('maintainer') }}</td>
          <td class="text-form-input">
            <el-form-item prop="author">
              <el-input v-model="docInfo.author" maxlength="64" show-word-limit />
            </el-form-item>
          </td>
          <td>{{ $t('isShow') }}</td>
          <td class="text-form-input">
            <el-form-item prop="isShow">
              <el-switch
                v-model="docInfo.isShow"
                active-color="#13ce66"
                inactive-color="#ff4949"
                :active-value="1"
                :inactive-value="0"
              />
            </el-form-item>
          </td>
          <td>{{ $t('orderIndex') }}</td>
          <td class="text-form-input">
            <el-form-item prop="orderIndex">
              <el-input-number v-model="docInfo.orderIndex" controls-position="right" />
            </el-form-item>
          </td>
        </tr>
      </table>
      <el-form-item prop="name" style="margin-bottom: 20px">
        <el-input v-model="docInfo.name" :placeholder="$t('docTitle')" maxlength="100" show-word-limit />
      </el-form-item>
      <rich-text-editor
        v-show="docInfo.type === getEnums().DOC_TYPE.CUSTOM"
        :value="docInfo.description"
        :placeholder="$t('EditDocCustom.inputDocContent')"
        :editable="true"
        @input="editorContent"
      />
      <mavon-editor
        v-show="docInfo.type === getEnums().DOC_TYPE.MARKDOWN"
        v-model="docInfo.description"
        :boxShadow="false"
        :scrollStyle="true"
        :subfield="true"
        :toolbars="toolbars"
        :style="editorStyle"
        @fullScreen="onFullScreen"
      />
    </el-form>
    <div style="margin-top: 10px;">
      <el-button type="text" icon="el-icon-back" @click="goBack">{{ $t('back') }}</el-button>
      <el-button type="primary" icon="el-icon-finished" @click="submitCustomDoc">{{ $t('save') }}</el-button>
      <el-button v-if="docInfo.type === getEnums().DOC_TYPE.CUSTOM" type="success" icon="el-icon-view" @click="viewCustomDoc">{{ $t('preview') }}</el-button>
    </div>
    <!-- dialog -->
    <el-dialog
      :title="$t('preview')"
      :visible.sync="viewCustomDialogVisible"
      width="70%"
    >
      <h1>{{ docInfo.name }}</h1>
      <div v-html="docInfo.description"></div>
    </el-dialog>
  </div>
</template>
<style>
.text-form td {
  color: #606266;
}
.text-form-input {
  padding-right: 20px;
}
.v-note-wrapper {
  padding: 0 !important;
}
</style>
<script>
import RichTextEditor from '@/components/RichTextEditor'
import { mavonEditor } from 'mavon-editor'

export default {
  components: { RichTextEditor, mavonEditor },
  props: {
    docType: {
      type: Number,
      default: -1
    }
  },
  data() {
    return {
      docInfo: {
        id: '',
        name: '',
        author: '',
        type: 0,
        description: '',
        parentId: '',
        projectId: '',
        isShow: 1,
        orderIndex: this.getEnums().INIT_ORDER_INDEX
      },
      markdownContent: '',
      viewCustomDialogVisible: false,
      folders: [],
      rulesCustom: {
        name: [
          { required: true, message: '请输入标题', trigger: 'blur' },
          { min: 1, max: 100, message: '长度在 1 到 100 个字符', trigger: 'blur' }
        ]
      },
      editorStyle: 'height: auto;',
      toolbars: {
        bold: true, // 粗体
        italic: true, // 斜体
        header: true, // 标题
        underline: true, // 下划线
        strikethrough: true, // 中划线
        mark: true, // 标记
        superscript: true, // 上角标
        subscript: true, // 下角标
        quote: true, // 引用
        ol: true, // 有序列表
        ul: true, // 无序列表
        link: true, // 链接
        imagelink: true, // 图片链接
        code: true, // code
        table: true, // 表格
        fullscreen: true, // 全屏编辑
        readmodel: false, // 沉浸式阅读
        htmlcode: false, // 展示html源码
        help: true, // 帮助
        /* 1.3.5 */
        undo: true, // 上一步
        redo: true, // 下一步
        trash: true, // 清空
        save: false, // 保存（触发events中的save事件）
        /* 1.4.2 */
        navigation: true, // 导航目录
        /* 2.1.8 */
        alignleft: true, // 左对齐
        aligncenter: true, // 居中
        alignright: true, // 右对齐
        /* 2.2.1 */
        subfield: true, // 单双栏模式
        preview: true // 预览
      }
    }
  },
  watch: {
    docType(val) {
      if (val > -1) {
        this.docInfo.type = val
      }
    }
  },
  created() {
    this.initData()
  },
  methods: {
    initData: function(id) {
      const docId = id || (this.$route.params.docId || '')
      const parentId = this.$route.params.parentId || ''
      const moduleId = this.$route.params.moduleId || ''
      const copyId = this.$route.params.copyId || ''
      this.moduleId = moduleId
      this.docInfo.docId = docId
      this.docInfo.parentId = parentId
      this.docInfo.moduleId = moduleId
      this.initFolders(moduleId)
      const finalId = docId || copyId
      if (finalId) {
        this.get('/doc/detail', { id: finalId }, function(resp) {
          const data = resp.data
          this.initDocInfo(data)
          Object.assign(this.docInfo, data)
          this.$store.state.settings.projectId = data.projectId
          if (copyId) {
            this.initCopyData(this.docInfo)
          }
        }, (resp) => {
          if (resp.code === '1000') {
            this.alert('文档不存在', '提示', function() {
              this.goRoute(`/platform/doc`)
            })
          } else {
            this.alert(resp.msg)
          }
        })
      }
      this.initOrderIndex()
    },
    editorContent(content) {
      if (this.docInfo.type === this.getEnums().DOC_TYPE.CUSTOM) {
        this.docInfo.description = content;
      }
    },
    goBack: function() {
      const projectId = this.docInfo.projectId || this.getCurrentProject().id
      this.goProjectHome(projectId)
    },
    initCopyData(docInfo) {
      docInfo.id = ''
      docInfo.name = `${this.docInfo.name} Copy`
    },
    initFolders(moduleId) {
      if (moduleId) {
        this.get('/doc/folder/list', { moduleId: moduleId }, resp => {
          this.folders = resp.data
        })
      }
    },
    initOrderIndex() {
      const order = this.$route.query.order
      if (order !== undefined) {
        this.docInfo.orderIndex = order
      }
    },
    viewCustomDoc() {
      this.viewCustomDialogVisible = true
    },
    submitCustomDoc() {
      this.$refs.docFormCustom.validate((valid) => {
        if (valid) {
          const data = this.docInfo
          this.post('/doc/save', data, resp => {
            this.tipSuccess('保存成功')
            const id = resp.data.id
            if (id !== data.id) {
              this.goRoute(`/doc/edit_custom/${data.moduleId}/${id}`)
            } else {
              this.initData()
            }
          })
        } else {
          this.tipError($t('pleaseFinishForm'))
        }
      })
    },
    onFullScreen(status) {
      if (status) {
        this.editorStyle = 'margin-left: 70px;margin-top: 50px;'
      } else {
        this.editorStyle = 'height: auto'
      }
    }
  }
}
</script>
