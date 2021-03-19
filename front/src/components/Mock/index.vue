<template>
  <div class="mock-content">
    <h3>
      Mock地址：<span class="doc-id">{{ mockUrl }}</span>
      <div style="float: right">
        <el-button
          type="primary"
          size="mini"
          style="margin-bottom: 10px"
          @click="onMockAdd"
        >
          添加配置
        </el-button>
      </div>
    </h3>
    <div v-show="mockConfigs.length > 0">
      <el-tabs
        v-model="activeMock"
        type="card"
        @tab-click="onTabSelect"
      >
        <el-tab-pane
          v-for="mock in mockConfigs"
          :key="mock.id"
          :label="mock.name"
          :name="mock.id"
        >
        </el-tab-pane>
      </el-tabs>
      <div class="doc-modify-info">
        <div v-show="isAdded()" class="left-div">
          {{ formData.creatorName }} 创建于 {{ formData.gmtCreate }}，{{ formData.modifierName }} 最后修改于 {{ formData.gmtModified }}
        </div>
        <div class="right-div">
          <el-tooltip v-show="isAdded()" content="复制当前配置" placement="top">
            <el-link type="primary" icon="el-icon-document-copy" @click="onCopy">复制</el-link>
          </el-tooltip>
          <el-popconfirm
            :title="`确定要当前配置删除吗？`"
            @onConfirm="onMockDelete(formData)"
          >
            <el-link slot="reference" type="danger" icon="el-icon-delete">删除</el-link>
          </el-popconfirm>
        </div>
      </div>
      <el-form
        ref="mockForm"
        :model="formData"
        :rules="formRules"
        size="mini"
        label-width="120px"
        style="margin-top: 40px;"
      >
        <el-form-item label="名称" prop="name">
          <el-input v-model="formData.name" maxlength="64" show-word-limit />
        </el-form-item>
        <el-form-item label="参数">
          <name-value-table :data="formData.queryData" />
        </el-form-item>
        <el-divider content-position="left">响应</el-divider>
        <el-form-item label="Http Status">
          <el-input-number v-model="formData.httpStatus" controls-position="right" />
        </el-form-item>
        <el-form-item label="延迟">
          <el-input-number v-model="formData.delayMills" :min="0" controls-position="right" /> ms
        </el-form-item>
        <el-form-item label="响应Header">
          <name-value-table :data="formData.responseHeaders" />
        </el-form-item>
        <el-form-item label="响应内容">
          <editor
            v-model="formData.responseBody"
            lang="json"
            theme="chrome"
            height="400"
            style="border: 1px #EBEEF5 solid"
            @init="editorInit"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="medium" @click="onSave">保存</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>
<script>
import NameValueTable from '@/components/NameValueTable'
export default {
  name: 'Mock',
  components: { NameValueTable, editor: require('vue2-ace-editor') },
  props: {
    item: {
      type: Object,
      default: () => {}
    }
  },
  data() {
    return {
      activeMock: null,
      mockUrl: '',
      responseSuccessExample: '',
      mockConfigs: [],
      formData: {
        name: '',
        queryData: [],
        httpStatus: 200,
        delayMills: 0,
        responseHeaders: [],
        responseBody: '',
        remark: '',
        creatorName: ''
      },
      formRules: {
        name: [
          { required: true, message: '请填写名称', trigger: ['blur', 'change'] }
        ]
      }
    }
  },
  watch: {
    item(newVal) {
      this.init(newVal)
    }
  },
  methods: {
    init(item) {
      this.mockUrl = `${this.getBaseUrl()}/mock/${item.id}`
      this.loadTab(item)
    },
    editorInit: function() {
      // language extension prerequsite...
      require('brace/ext/language_tools')
      require('brace/mode/json')
      // language
      require('brace/mode/javascript')
      require('brace/mode/less')
      require('brace/theme/chrome')
      // snippet
      require('brace/snippets/javascript')
    },
    reload() {
      this.loadTab(this.item)
    },
    loadTab(item) {
      this.get('/doc/mock/list', { docId: item.id }, resp => {
        this.mockConfigs = resp.data
        let find
        const active = this.activeMock
        if (active && active !== '0') {
          find = this.mockConfigs.filter(row => row.id === active)
        }
        if (!find || find.length === 0) {
          find = this.mockConfigs
        }
        if (find.length > 0) {
          this.selectTab(find[0])
        }
      })
    },
    onTabSelect(tab) {
      const index = parseInt(tab.index)
      const node = this.mockConfigs[index]
      this.selectTab(node)
    },
    selectTab(node) {
      this.formData = node
      this.activeMock = node.id
    },
    onMockAdd() {
      const respBody = this.doCreateResponseExample(this.item.responseParams)
      const node = {
        id: '' + this.nextId(),
        name: '新建配置',
        queryData: [],
        httpStatus: 200,
        delayMills: 0,
        responseHeaders: [],
        responseBody: this.formatJson(respBody),
        remark: '',
        creatorName: '',
        isNew: true
      }
      this.addMock(node)
    },
    onCopy() {
      const node = {}
      Object.assign(node, this.formData)
      node.id = '' + this.nextId()
      node.name = node.name + ' copy'
      this.addMock(node)
    },
    addMock(node) {
      this.mockConfigs.push(node)
      this.selectTab(node)
    },
    onMockDelete(data) {
      if (data.isNew) {
        this.removeLocal(data)
      } else {
        this.post('/doc/mock/delete', data, resp => {
          this.tipSuccess('删除成功')
          this.reload()
        })
      }
    },
    removeLocal(data) {
      const index = this.removeRow(this.mockConfigs, data.id)
      if (index !== undefined && this.mockConfigs.length > 0) {
        const nextIndex = index > 0 ? index - 1 : 0
        this.selectTab(this.mockConfigs[nextIndex])
      }
    },
    onSave() {
      this.$refs.mockForm.validate(valid => {
        if (valid) {
          this.filterData()
          this.formData.docId = this.item.id
          this.post('/doc/mock/save', this.formData, resp => {
            this.tipSuccess('保存成功')
            this.activeMock = resp.data.id
            this.reload()
          })
        } else {
          this.tipError('请完善表单')
        }
      })
    },
    isAdded() {
      return !this.formData.isNew
    },
    filterData() {
      this.formData.queryData = this.formData.queryData.filter(row => row.isDeleted === 0)
      this.formData.responseHeaders = this.formData.responseHeaders.filter(row => row.isDeleted === 0)
    }
  }
}
</script>
<style>
  .mock-content {
    margin: 10px;
    width: 800px;
  }
</style>
