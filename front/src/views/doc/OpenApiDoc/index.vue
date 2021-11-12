<template>
  <el-container>
    <el-header>
      <help-menu />
    </el-header>
    <el-container>
      <el-aside width="300px">
        <el-menu :default-openeds="openMenu" :default-active="defaultActive" class="normal-menu" @select="onMenuClick">
          <el-menu-item index="overview.md">
            <i class="el-icon-house"></i>
            使用前阅读
          </el-menu-item>
          <el-menu-item index="code.md">
            <i class="el-icon-tickets"></i>
            错误码
          </el-menu-item>
          <el-submenu v-for="(item) in apiModules" :key="item.name" :index="item.name">
            <template slot="title">
              <i class="el-icon-folder" />
              <span slot="title">{{ item.name }}</span>
            </template>
            <el-menu-item v-for="(child) in item.moduleItems" :key="child.nameVersion" :index="child.nameVersion" @click="onApiClick(child)">
              {{ child.description }}
            </el-menu-item>
          </el-submenu>
        </el-menu>
      </el-aside>
      <el-main>
        <div v-show="contentShow">
          <mavon-editor
            v-model="content"
            :box-shadow="false"
            :subfield="false"
            default-open="preview"
            :editable="false"
            :toolbars-flag="false"
          />
        </div>
        <div v-show="!contentShow && api.description">
          <h3>{{ api.description }}</h3>
          <div v-if="api.remark" class="api-remark">
            {{ api.remark }}
          </div>
          <div>
            接口名：{{ api.name }} 版本号：{{ api.version }}
          </div>
          <h4>HttpMethod</h4>
          POST
          <h3>公共请求参数</h3>
          <el-table
            :data="commonParams"
            border
            highlight-current-row
          >
            <el-table-column
              prop="name"
              label="名称"
              width="200"
            />
            <el-table-column
              prop="type"
              label="类型"
              width="100"
            />
            <el-table-column
              prop="required"
              label="必须"
              width="60"
            >
              <template slot-scope="scope">
                <span :class="scope.row.required ? 'danger' : ''">{{ scope.row.required ? '是' : '否' }}</span>
              </template>
            </el-table-column>
            <el-table-column
              prop="description"
              label="描述"
            >
              <template slot-scope="scope">
                {{ scope.row.description }}
                <span v-if="scope.row.name === 'sign'">
                  ，见 使用前阅读
                </span>
              </template>
            </el-table-column>
            <el-table-column
              prop="example"
              label="示例值"
            />
          </el-table>
          <h3>业务请求参数</h3>
          <api-param-table :data="api.requestParams" />
          <h4>请求示例</h4>
          <pre class="normal-text">{{ JSON.stringify(requestParamsExample, null, 4) }}</pre>
          <h3>返回参数</h3>
          <el-table
            :data="resultData"
            border
            highlight-current-row
          >
            <el-table-column label="名称" prop="name" width="100" />
            <el-table-column label="类型" prop="type" width="100">
              <template slot-scope="scope">
                <span v-if="scope.row.name === 'data'">
                  {{ dataNodeType }}
                </span>
                <span v-else>
                  {{ scope.row.type }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="描述" prop="description" width="300" />
            <el-table-column label="示例值" prop="example" width="200" />
          </el-table>
          <h4>data部分</h4>
          <api-param-table :data="api.responseParams" />
          <h4>返回示例</h4>
          <pre class="normal-text">{{ JSON.stringify(responseParamsExample, null, 4) }}</pre>
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>
<script>
import HelpMenu from '@/components/HelpMenu'
import ApiParamTable from '@/components/ApiParamTable'
import { mavonEditor } from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'

let idGen = 1

export default {
  name: 'OpenApiDoc',
  components: { HelpMenu, mavonEditor, ApiParamTable },
  data() {
    return {
      defaultActive: 'overview.md',
      activeIndex: '1',
      content: '',
      contentShow: false,
      openMenu: [],
      apiModules: [],
      dataNodeType: 'object',
      api: '',
      requestParamsExample: {},
      responseParamsExample: {},
      commonParams: [
        { 'name': 'name', 'type': 'string', 'required': 1, 'description': '接口名', 'example': 'doc.push' },
        { 'name': 'version', 'type': 'string', 'required': 1, 'description': '版本号', 'example': '1.0' },
        { 'name': 'data', 'type': 'string', 'required': 1, 'description': '业务请求参数，json格式并且urlencode', 'example': '' },
        { 'name': 'access_token', 'type': 'string', 'required': 1, 'description': '项目模块对应的token', 'example': 'c16931fa65983fb7a4e85340fcbfef' },
      ],
      resultData: [
        { name: 'code', type: 'string', description: '返回code，"0"表示成功，其它都是失败', example: '"0"' },
        { name: 'data', type: 'object', description: '返回数据，见下表data部分', example: '{}' },
        { name: 'msg', type: 'string', description: '错误信息', example: '' }
      ]
    }
  },
  created() {
    this.loadMenu()
    this.loadMarkdown(this.defaultActive)
  },
  methods: {
    loadMenu: function() {
      this.do_get('/api/json/doc', {}, response => {
        const resp = response.data
        this.apiModules = resp.apiModules
        this.$nextTick(() => {
          this.openMenu = this.apiModules.map(row => row.name)
        })
      })
    },
    onApiClick(child) {
      this.contentShow = false
      this.api = child
      Object.assign(this.api, {
        requestParams: this.buildTableData(child.paramDefinitions),
        responseParams: this.buildTableData(this.getResponseParams(child))
      })
      this.requestParamsExample = this.doCreateResponseExample(this.api.requestParams)
      const responseExample = this.doCreateResponseExample(this.api.responseParams)
      const dataVal = this.dataNodeType === 'array' ? [responseExample] : responseExample
      this.responseParamsExample = {
        code: '0',
        data: dataVal,
        msg: ''
      }
    },
    getResponseParams(child) {
      const params = child.resultDefinitions
      if (!child.customWrapper) {
        this.dataNodeType = 'object'
        return params
      }
      const rows = params.filter(row => row.name === 'data')
      if (rows.length > 0) {
        const row = rows[0]
        this.dataNodeType = row.dataType
        return row.elements
      } else {
        this.dataNodeType = 'object'
        return []
      }
    },
    buildTableData(definitions, pid) {
      const data = []
      definitions.forEach(row => {
        const item = {
          id: idGen++,
          name: row.name,
          type: row.dataType,
          required: row.required === 'true',
          maxLength: row.maxLength,
          description: row.description,
          example: row.example,
          parentId: pid
        }
        data.push(item)
        const children = row.elements
        if (children && children.length > 0) {
          item.children = this.buildTableData(children, item.id)
        }
      })
      return data
    },
    onMenuClick: function(index, path) {
      if (index.endsWith('.md')) {
        this.loadMarkdown(index)
      }
    },
    loadMarkdown: function(path) {
      this.getFile(`static/openapi/${path}?q=${new Date().getTime()}`, (content) => {
        this.content = content
        this.contentShow = true
      })
    }
  }
}
</script>
<style scoped>
.markdown-body table td, .markdown-body table th {
  padding: 2px 4px;
  font-size: 12px;
}
.markdown-body table th {
  background-color: #f3f3f3;
}
.markdown-body table tr:nth-child(2n) {
  background-color: #FFFFFF !important;
}
.el-header {
  color: #333;
  line-height: 60px;
}
.el-aside {
  color: #333;
}
.api-remark {
  margin: 10px 0;
  font-size: 13px;
  color: #606266;
}
</style>
