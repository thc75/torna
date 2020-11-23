<template>
  <div class="app-container route-doc-edit">
    <h3>{{ docInfo.name }}</h3>
    <el-tabs v-model="activeName" tab-position="left">
      <el-tab-pane label="基本信息" name="info">
        <el-form
          ref="docForm"
          :model="docInfo"
          :rules="rules"
          label-width="88px"
          size="mini"
          style="width: 700px"
        >
          <el-form-item prop="name" label="文档标题">
            <el-input v-model="docInfo.name" maxlength="100" show-word-limit placeholder="文档名称" />
          </el-form-item>
          <el-form-item prop="description" label="接口描述">
            <el-input v-model="docInfo.description" type="textarea" :rows="4" maxlength="200" show-word-limit placeholder="文档概述" />
          </el-form-item>
          <el-form-item prop="url" label="请求地址">
            <el-input v-model="docInfo.url" class="input-with-select" maxlength="100" show-word-limit placeholder="请求地址">
              <el-select slot="prepend" v-model="docInfo.httpMethod" placeholder="请选择" style="width: 100px;">
                <el-option v-for="method in allMethods" :key="method" :label="method" :value="method">
                  {{ method }}
                </el-option>
              </el-select>
            </el-input>
          </el-form-item>
          <el-form-item prop="parentId" label="分类">
            <el-select v-model="docInfo.parentId" placeholder="请选择" style="width: 100%;">
              <el-option label="不分类" :value="0">不分类</el-option>
              <el-option v-for="item in docInfo.folders" :key="item.id" :label="item.name" :value="item.id">
                {{ item.name }}
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="是否显示">
            <el-switch
              v-model="docInfo.isShow"
              active-color="#13ce66"
              inactive-color="#ff4949"
              :active-value="1"
              :inactive-value="0"
            />
          </el-form-item>
        </el-form>
      </el-tab-pane>
      <el-tab-pane label="请求头" name="headerParam">
        <el-button type="text" icon="el-icon-plus" @click="onParamAdd(docInfo.headerParams)">添加Header</el-button>
        <el-button type="text" icon="el-icon-bottom-right" @click="onImportHeaderParamAdd">导入Header</el-button>
        <param-table ref="headerParamTable" :data="docInfo.headerParams" :can-add-node="false" :hidden-columns="['type', 'maxLength']" />
      </el-tab-pane>
      <el-tab-pane label="请求参数" name="requestParam">
        <el-button type="text" icon="el-icon-plus" @click="onParamAdd(docInfo.requestParams)">添加请求参数</el-button>
        <el-button type="text" icon="el-icon-bottom-right" @click="onImportRequestParamAdd">导入请求参数</el-button>
        <param-table ref="requestParamTable" :data="docInfo.requestParams" />
      </el-tab-pane>
      <el-tab-pane label="响应参数" name="responseParam">
        <el-button type="text" icon="el-icon-plus" @click="onResponseParamAdd">添加响应参数</el-button>
        <el-button type="text" icon="el-icon-bottom-right" @click="onImportResponseParamAdd">导入响应参数</el-button>
        <param-table ref="responseParamTable" :data="docInfo.responseParams" :hidden-columns="['required', 'maxLength']" />
      </el-tab-pane>
      <el-tab-pane label="错误码" name="bizCode">
        <el-button type="text" icon="el-icon-plus" @click="onBizCodeAdd">添加错误码</el-button>
        <el-table
          :data="docInfo.bizCodes"
          border
          :cell-style="cellStyleSmall()"
          :header-cell-style="headCellStyleSmall()"
          class="param-table"
        >
          <el-table-column
            prop="subCode"
            label="sub_code（错误码）"
            width="300"
          >
            <template slot-scope="scope">
              <el-form :ref="'code_form_code_' + scope.row.id" :model="scope.row" :rules="codeRowRule" size="mini">
                <el-form-item
                  prop="subCode"
                  label-width="0"
                >
                  <el-input v-model="scope.row.subCode" placeholder="错误码" maxlength="64" show-word-limit />
                </el-form-item>
              </el-form>
            </template>
          </el-table-column>
          <el-table-column
            prop="subMsg"
            label="sub_msg（错误描述）"
          >
            <template slot-scope="scope">
              <el-form :ref="'code_form_msg_' + scope.row.id" :model="scope.row" :rules="codeRowRule" size="mini">
                <el-form-item
                  prop="subMsg"
                  label-width="0"
                >
                  <el-input v-model="scope.row.subMsg" placeholder="错误描述" maxlength="100" show-word-limit />
                </el-form-item>
              </el-form>
            </template>
          </el-table-column>
          <el-table-column
            prop="solution"
            label="解决方案"
          >
            <template slot-scope="scope">
              <el-form :ref="'code_form_solution_' + scope.row.id" :model="scope.row" size="mini">
                <el-form-item
                  prop="solution"
                  label-width="0"
                >
                  <el-input v-model="scope.row.solution" placeholder="解决方案" maxlength="100" show-word-limit />
                </el-form-item>
              </el-form>
            </template>
          </el-table-column>
          <el-table-column
            label="操作"
            width="170"
          >
            <template slot-scope="scope">
              <div>
                <div v-show="scope.row.isDeleted === 0">
                  <el-link type="danger" icon="el-icon-delete" @click="onBizCodeRemove(scope.row)">移除</el-link>
                </div>
                <div v-show="scope.row.isDeleted === 1">
                  <el-tooltip content="点击恢复" placement="top">
                    <el-link type="danger" icon="el-icon-remove-outline" @click="scope.row.isDeleted = 0"></el-link>
                  </el-tooltip>
                </div>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
    <div style="margin-top: 10px;">
      <el-button type="text" icon="el-icon-back" @click="goBack">返回</el-button>
      <el-button type="primary" icon="el-icon-finished" @click="submitForm">保存</el-button>
      <el-button type="success" icon="el-icon-view" @click="viewDoc">预览</el-button>
    </div>
    <!-- dialog -->
    <el-dialog
      title="预览"
      :visible.sync="viewDialogVisible"
      width="70%"
    >
      <doc-view :doc-info-string="docInfoString" />
    </el-dialog>
    <el-dialog
      :title="importParamTemplateTitle"
      :visible.sync="importParamTemplateDlgShow"
    >
      <el-radio-group v-model="importParamTemplateModel" style="margin-bottom: 20px;">
        <el-radio :label="0">Query参数导入</el-radio>
        <el-radio :label="1">Bulk模式导入（Postman Bulk Edit）</el-radio>
      </el-radio-group>
      <el-input v-model="importParamTemplateValue" type="textarea" :rows="4" :placeholder="getParamTemplatePlaceholder()" />
      <div slot="footer" class="dialog-footer">
        <el-button @click="importParamTemplateDlgShow = false">取 消</el-button>
        <el-button type="primary" @click="callImportParamHandler">保 存</el-button>
      </div>
    </el-dialog>
    <el-dialog
      title="导入响应参数"
      :visible.sync="paramResponseTemplateDlgShow"
    >
      <el-alert title="输入完整的响应结果，可填测试数据。【注意敏感信息，请勿导入线上数据】" :closable="false" class="el-alert-tip"/>
      <el-form ref="paramResponseTemplateForm" :model="paramResponseTemplateFormData">
        <el-form-item prop="value" :rules="[{ required: true, message: '请输入JSON', trigger: 'blur' }]">
          <el-input v-model="paramResponseTemplateFormData.value" type="textarea" :rows="14" placeholder="json格式" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="paramResponseTemplateDlgShow = false">取 消</el-button>
        <el-button type="primary" @click="onImportResponseParamSave">保 存</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<style>
.input-with-select .el-input-group__prepend {
  background-color: #fff;
}
</style>
<script>
import DocView from '../DocView'
import ParamTable from '../ParamTable'

let idGen = 0
export default {
  components: { DocView, ParamTable },
  data() {
    return {
      params: {},
      activeName: 'info',
      allMethods: ['GET', 'POST', 'PUT', 'PATCH', 'DELETE', 'OPTIONS', 'HEAD'],
      docInfo: {
        docId: 0,
        name: '',
        url: '',
        contentType: '',
        description: '',
        httpMethod: 'GET',
        parentId: 0,
        moduleId: 0,
        isShow: 1,
        headerParams: [],
        requestParams: [],
        responseParams: [],
        bizCodes: [],
        folders: []
      },
      rules: {
        name: [
          { required: true, message: '请输入标题', trigger: 'blur' },
          { min: 1, max: 100, message: '长度在 1 到 100 个字符', trigger: 'blur' }
        ],
        url: [
          { required: true, message: '请输入URL', trigger: 'blur' },
          { min: 1, max: 100, message: '长度在 1 到 100 个字符', trigger: 'blur' }
        ],
        httpMethod: [
          { required: true, message: '不能为空', trigger: 'blur' }
        ]
      },
      codeRowRule: {
        subCode: [
          { required: true, message: '请填写错误码', trigger: 'blur' }
        ],
        subMsg: [
          { required: true, message: '请填写错误描述', trigger: 'blur' }
        ]
      },
      templateList: [],
      viewDialogVisible: false,
      docInfoString: '',
      importParamTemplateTitle: '导入参数',
      importParamTemplateDlgShow: false,
      importParamTemplateValue: '',
      importParamTemplateModel: 0,

      paramResponseTemplateFormData: {
        value: ''
      },
      paramResponseTemplateDlgShow: false,
      importParamHandler: null
    }
  },
  created() {
    this.initData()
  },
  methods: {
    initData: function() {
      const docId = this.$route.params.docId
      this.docInfo.docId = docId
      if (docId > 0) {
        this.get('/doc/detail', { id: docId }, function(resp) {
          const data = resp.data
          data.requestParams = this.convertTree(data.requestParams, 0)
          data.responseParams = this.convertTree(data.responseParams, 0)
          Object.assign(this.docInfo, data)
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
    },
    onParamAdd: function(row) {
      row.push(this.getParamNewRow())
    },
    onParamNodeAdd: function(row) {
      const children = row.children || []
      children.push(this.getParamNewRow())
      children.hasChildren = true
      row.children = children
    },
    onResponseParamAdd: function() {
      this.docInfo.responseParams.push(this.getParamNewRow())
    },
    getParamNewRow: function(name, value) {
      return {
        id: new Date().getTime() + (idGen++),
        name: name || '',
        type: 'string',
        required: 1,
        description: '',
        maxLength: 64,
        example: value || '',
        isDeleted: 0,
        isNew: true,
        children: []
      }
    },
    getCodeNewRow: function() {
      return {
        id: new Date().getTime() + (idGen++),
        subCode: '',
        subMsg: '',
        solution: '',
        isDeleted: 0,
        isNew: true
      }
    },
    onBizCodeAdd: function() {
      this.docInfo.bizCodes.push(this.getCodeNewRow())
    },
    onBizCodeRemove: function(row) {
      if (row.isNew) {
        this.removeRow(this.docInfo.bizCodes, row.id)
      } else {
        row.isDeleted = 1
      }
    },
    // 修改文档内容
    submitForm() {
      this.$refs.docForm.validate((valid) => {
        if (valid) {
          const promiseHeaderArr = this.$refs.headerParamTable.validate()
          const promiseRequestArr = this.$refs.requestParamTable.validate()
          const promiseResponseArr = this.$refs.requestParamTable.validate()
          const promiseBizCodeArr = this.validateTable(this.docInfo.bizCodes, ['code_form_code_', 'code_form_msg_'])
          const promiseArr = promiseHeaderArr.concat(promiseRequestArr, promiseResponseArr, promiseBizCodeArr)
          Promise.all(promiseArr).then(validArr => {
            const data = {}
            Object.assign(data, this.docInfo)
            // 到这里来表示全部内容校验通过
            const headerParams = this.getHeaderParamsData()
            const requestParams = this.getRequestParamsData()
            const responseParams = this.getResponseParamsData()
            Object.assign(data, {
              headerParams: this.unConvertTree(headerParams),
              requestParams: this.unConvertTree(requestParams),
              responseParams: this.unConvertTree(responseParams)
            })
            this.post('/doc/save', data, function(resp) {
              this.tipSuccess('保存成功')
            })
          }).catch((e) => {
            this.tipError('请完善表单内容')
          }) // 加上这个控制台不会报Uncaught (in promise)
        } else {
          this.tipError('请完善表单内容')
        }
      })
    },
    viewDoc: function() {
      this.viewDialogVisible = true
      this.$nextTick(() => {
        const viewData = {}
        Object.assign(viewData, this.docInfo)
        viewData.headerParams = this.getHeaderParamsData()
        viewData.requestParams = this.getRequestParamsData()
        viewData.responseParams = this.getResponseParamsData()
        this.docInfoString = JSON.stringify(viewData)
      })
    },
    getHeaderParamsData() {
      return this.$refs.headerParamTable.getData()
    },
    getRequestParamsData() {
      return this.$refs.requestParamTable.getData()
    },
    getResponseParamsData() {
      return this.$refs.responseParamTable.getData()
    },
    goBack: function() {
      this.$router.go(-1)
    },
    onImportHeaderParamAdd: function() {
      this.importParamTemplateDlgShow = true
      this.importParamTemplateValue = ''
      this.importParamTemplateTitle = '导入Header'
      const that = this
      this.importParamHandler = function() {
        that.onImportParamSave(that.docInfo.headerParams)
      }
    },
    onImportRequestParamAdd: function() {
      this.importParamTemplateDlgShow = true
      this.importParamTemplateValue = ''
      this.importParamTemplateTitle = '导入参数'
      const that = this
      this.importParamHandler = function() {
        that.onImportParamSave(that.docInfo.requestParams)
      }
    },
    onImportResponseParamAdd: function() {
      this.paramResponseTemplateDlgShow = true
      this.paramResponseTemplateFormData.value = ''
    },
    callImportParamHandler() {
      this.importParamHandler && this.importParamHandler()
    },
    onImportParamSave: function(params) {
      console.log(params)
      const value = this.importParamTemplateValue
      let bigSplitChar = '&'
      let smallSplitChar = '='
      try {
        if (this.importParamTemplateModel === 1) {
          bigSplitChar = '\n'
          smallSplitChar = ':'
        }
        const valueArr = value.split(bigSplitChar)
        for (let i = 0; i < valueArr.length; i++) {
          const pair = valueArr[i]
          if (pair.indexOf(smallSplitChar) < 0) {
            throw new Error('格式错误')
          }
          const keyValue = pair.split(smallSplitChar)
          const value = decodeURIComponent(keyValue[1])
          this.addParamFormPair(params, keyValue[0], value)
        }
        this.importParamTemplateDlgShow = false
      } catch (e) {
        this.tipError('格式错误')
      }
    },
    onImportResponseParamSave: function() {
      this.$refs.paramResponseTemplateForm.validate(valid => {
        if (valid) {
          const value = this.paramResponseTemplateFormData.value
          const responseParams = this.docInfo.responseParams
          this.parseJSON(value, (json) => {
            this.doImportResponseParam(responseParams, json)
            this.paramResponseTemplateDlgShow = false
          }, () => this.tipError('JSON格式错误'))
        }
      })
    },
    /**
     * 导入返回参数
     * @param responseParams 响应参数数组
     * @param json 当前json
     */
    doImportResponseParam: function(responseParams, json) {
      for (const name in json) {
        const value = json[name]
        const existRow = this.findRow(responseParams, name)
        const row = this.getParamNewRow(name, value)
        // 如果有子节点
        if (this.isObject(value)) {
          row.type = 'object'
          row.example = ''
          const children = existRow ? existRow.children : row.children
          this.doImportResponseParam(children, value)
        } else if (this.isArray(value)) {
          row.type = 'array'
          row.example = ''
          const oneJson = value.length === 0 ? {} : value[0]
          const children = existRow ? existRow.children : row.children
          this.doImportResponseParam(children, oneJson)
        }
        if (existRow) {
          existRow.example = row.example
        }
        if (!existRow) {
          responseParams.push(row)
        }
      }
    },
    findRow: function(responseParams, name) {
      for (let i = 0; i < responseParams.length; i++) {
        const r = responseParams[i]
        if (r.name === name) {
          return r
        }
      }
      return null
    },
    addParamFormPair: function(params, name, value) {
      let isNew = true
      params.forEach(row => {
        // 如果参数名已存在
        if (row.name === name) {
          isNew = false
          row.example = value
        }
      })
      if (isNew) {
        params.push(this.getParamNewRow(name, value))
      }
    },
    getParamTemplatePlaceholder: function() {
      const map = {
        '0': 'name=jim&age=123&address=xxx',
        '1': `name:jim
age:123
address:xxx`
      }
      return map[this.importParamTemplateModel + ''] || ''
    }
  }
}
</script>
