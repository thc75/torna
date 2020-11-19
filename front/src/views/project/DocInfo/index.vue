<template>
  <div>
    <el-container>
      <el-aside width="300px" style="border-right: 1px solid #eee;padding-right: 20px;">
        <el-dropdown trigger="click" style="margin-bottom: 10px;" @command="handleCommand">
          <el-button type="primary" size="mini" icon="el-icon-circle-plus-outline">
            新建 <i class="el-icon-arrow-down el-icon--right"></i>
          </el-button>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item icon="el-icon-document">新建接口</el-dropdown-item>
            <el-dropdown-item icon="el-icon-folder">新建分类</el-dropdown-item>
            <el-dropdown-item icon="el-icon-download" divided :command="onImportSwagger">导入Swagger文档</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
        <el-input
          v-show="treeData.length > 0"
          v-model="filterText"
          prefix-icon="el-icon-search"
          placeholder="搜索:文档名称、请求地址"
          style="margin-bottom: 10px;"
          size="mini"
          clearable
        />
        <el-tree
          ref="tree"
          :data="treeData"
          :props="defaultProps"
          :filter-node-method="filterNode"
          node-key="id"
          default-expand-all
          highlight-current
          empty-text="暂无文档"
          @current-change="onDocSelect"
        />
      </el-aside>
      <el-main style="padding-top: 0">
        <doc-view v-show="docId > 0" :id="docId"></doc-view>
      </el-main>
    </el-container>
    <!-- 导入json -->
    <el-dialog
      title="导入JSON"
      :visible.sync="importJsonDlgShow"
    >
      <el-form
        ref="importJsonForm"
        :model="importJsonFormData"
        :rules="importJsonRule"
        size="mini"
        label-width="100px"
      >
        <el-form-item label="URL" prop="url">
          <el-input
            v-model="importJsonFormData.url"
            placeholder="输入URL，如：http://xxx:8080/swagger/doc.json"
            show-word-limit
            maxlength="100"
          />
        </el-form-item>
        <el-form-item label="Basic认证">
          <el-col :span="12" style="padding-right: 10px;">
            <el-input v-model="importJsonFormData.basicAuthUsername" placeholder="选填，username" style="width: 100%;" />
          </el-col>
          <el-col :span="12">
            <el-input v-model="importJsonFormData.basicAuthPassword" placeholder="选填，password" style="width: 100%;" />
          </el-col>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="importJsonDlgShow = false">取 消</el-button>
        <el-button :loading="importJsonLoading" type="primary"  @click="onImportSwaggerSave">导 入</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import DocView from '../DocView'

export default {
  name: 'DocInfo',
  components: { DocView },
  props: {
    projectId: {
      type: Number,
      default: 0
    }
  },
  data() {
    return {
      treeData: [],
      filterText: '',
      defaultProps: {
        children: 'children',
        label: 'name'
      },
      docId: 0,
      viewDialogData: {
        routeId: 0
      },
      importJsonDlgShow: false,
      importJsonLoading: false,
      importJsonFormData: {
        projectId: 0,
        url: '',
        basicAuthUsername: '',
        basicAuthPassword: ''
      },
      importJsonRule: {
        url: [
          { required: true, message: '不能为空', trigger: 'blur' },
          { validator: (rule, value, callback) => {
            if (value && !/^http(s)?:\/\/.+$/i.test(value)) {
              callback(new Error('URL格式不正确'))
            } else {
              callback()
            }
          }, trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    filterText(val) {
      this.$refs.tree.filter(val)
    },
    projectId(id) {
      this.loadTree(id)
    }
  },
  created() {
  },
  methods: {
    loadTree: function(projectId) {
      if (projectId > 0) {
        this.get('/project/doc/list', { projectId: projectId }, function(resp) {
          const data = resp.data
          this.treeData = this.convertTree(data)
        })
      }
    },
    // 树搜索
    filterNode(value, data) {
      if (!value) return true
      value = value.toLowerCase()
      return (data.name && data.name.toLowerCase().indexOf(value) !== -1) || (data.url && data.url.toLowerCase().indexOf(value) > -1)
    },
    onDocSelect: function(currentNode, beforeNode) {
      this.showDoc(currentNode)
    },
    showDoc: function(node) {
      if (node.children.length === 0) {
        this.docId = node.id
      }
    },
    onImportSwagger() {
      this.importJsonLoading = false
      this.importJsonDlgShow = true
    },
    onImportSwaggerSave() {
      this.$refs.importJsonForm.validate((valid) => {
        if (valid) {
          this.importJsonLoading = true
          this.importJsonFormData.projectId = this.projectId
          this.formatHost(this.importJsonFormData)
          this.post('/project/doc/import/swagger', this.importJsonFormData, resp => {
            this.importJsonLoading = false
            this.importJsonDlgShow = false
            this.tipSuccess('导入成功')
            this.loadTree(this.projectId)
          }, () => {
            this.importJsonLoading = false
          })
        }
      })
    },
    formatHost(obj) {
      let url = obj.url
      if (url) {
        url = obj.url.trim()
        if (url.endsWith('/')) {
          url = url.substring(0, url.length - 1)
        }
        obj.url = url
      }
    }
  }
}
</script>
