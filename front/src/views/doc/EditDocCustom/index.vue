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
        :value="docInfo.description"
        :placeholder="$t('EditDocCustom.inputDocContent')"
        :editable="true"
        @input="editorContent"
      />
    </el-form>
    <div style="margin-top: 10px;">
      <el-button type="text" icon="el-icon-back" @click="goBack">{{ $t('back') }}</el-button>
      <el-button type="primary" icon="el-icon-finished" @click="submitCustomDoc">{{ $t('save') }}</el-button>
      <el-button type="success" icon="el-icon-view" @click="viewCustomDoc">{{ $t('preview') }}</el-button>
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
</style>
<script>
import RichTextEditor from '@/components/RichTextEditor'

export default {
  components: { RichTextEditor },
  data() {
    return {
      docInfo: {
        name: '',
        author: '',
        type: this.getEnums().DOC_TYPE.CUSTOM,
        description: '',
        parentId: '',
        projectId: '',
        isShow: 1,
        orderIndex: this.getEnums().INIT_ORDER_INDEX
      },
      viewCustomDialogVisible: false,
      folders: [],
      rulesCustom: {
        name: [
          { required: true, message: '请输入标题', trigger: 'blur' },
          { min: 1, max: 100, message: '长度在 1 到 100 个字符', trigger: 'blur' }
        ]
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
      this.docInfo.description = content
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
      console.log(1)
      this.viewCustomDialogVisible = true
    },
    submitCustomDoc() {
      this.$refs.docFormCustom.validate((valid) => {
        if (valid) {
          const data = this.docInfo
          this.docInfo.type = this.getEnums().DOC_TYPE.CUSTOM
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
    }
  }
}
</script>
