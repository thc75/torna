<template>
  <div class="app-container route-doc-edit">
    <h3>
      {{ docInfo.name || initTitle }} <span v-show="docInfo.id" class="doc-id">ID：{{ docInfo.id }}</span>
    </h3>
    <el-tabs v-model="activeName" tab-position="left">
      <el-tab-pane label="基本信息" name="info">
        <el-form
          ref="docForm"
          :model="docInfo"
          :rules="rules"
          label-width="88px"
          size="mini"
          style="width: 800px"
        >
          <el-form-item prop="name" label="文档标题">
            <el-input v-model="docInfo.name" maxlength="100" show-word-limit placeholder="文档名称" />
          </el-form-item>
          <el-form-item prop="description" label="接口描述">
            <el-input v-model="docInfo.description" type="textarea" :rows="4" maxlength="200" show-word-limit placeholder="文档概述" />
          </el-form-item>
          <el-form-item prop="url" label="请求地址">
            <el-input v-model="docInfo.url" class="input-with-select" maxlength="100" show-word-limit placeholder="请求地址" @input="onUrlInput">
              <el-select slot="prepend" v-model="docInfo.httpMethod" placeholder="请选择" style="width: 100px;">
                <el-option v-for="method in allMethods" :key="method" :label="method" :value="method">
                  {{ method }}
                </el-option>
              </el-select>
            </el-input>
            <edit-table
              v-show="docInfo.url && docInfo.pathParams.length > 0"
              ref="pathParamTable"
              :data="docInfo.pathParams"
              :getter="(rows) => { return rows.filter(row => row.isDeleted === 0) }"
              :module-id="moduleId"
              name-label="Path参数"
              :name-width="200"
              :text-columns="['name']"
              :hidden-columns="['required', 'maxLength', 'enum', 'opt']"
            />
          </el-form-item>
          <el-form-item prop="contentType" label="ContentType">
            <el-select v-model="docInfo.contentType" :clearable="true" placeholder="请选择" style="width: 300px;">
              <el-option v-for="contentType in getEnums().CONTENT_TYPE" :key="contentType" :label="contentType" :value="contentType">
                {{ contentType }}
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item prop="parentId" label="所属分类">
            <el-select v-model="docInfo.parentId" placeholder="请选择" style="width: 300px;">
              <el-option label="无" :value="0">无</el-option>
              <el-option v-for="item in folders" :key="item.id" :label="item.name" :value="item.id">
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
        <span class="split">|</span>
        <el-switch
          v-model="docInfo.isUseGlobalHeaders"
          active-text="使用公共请求头"
          inactive-text=""
          :active-value="1"
          :inactive-value="0"
        />
        <edit-table
          ref="headerParamTable"
          :data="docInfo.headerParams"
          :can-add-node="false"
          :hidden-columns="['type', 'maxLength', 'enum']"
        />
      </el-tab-pane>
      <el-tab-pane label="请求参数" name="requestParam">
        <el-button type="text" icon="el-icon-plus" @click="onParamAdd(docInfo.requestParams)">添加请求参数</el-button>
        <el-button type="text" icon="el-icon-bottom-right" @click="onImportRequestParamAdd">导入请求参数</el-button>
        <span class="split">|</span>
        <el-switch
          v-model="docInfo.isUseGlobalParams"
          active-text="使用公共请求参数"
          inactive-text=""
          :active-value="1"
          :inactive-value="0"
        />
        <edit-table ref="requestParamTable" :data="docInfo.requestParams" :module-id="moduleId" :hidden-columns="['enum']" />
      </el-tab-pane>
      <el-tab-pane label="响应参数" name="responseParam">
        <el-button type="text" icon="el-icon-plus" @click="onResponseParamAdd">添加响应参数</el-button>
        <el-button type="text" icon="el-icon-bottom-right" @click="onImportResponseParamAdd">导入响应参数</el-button>
        <span class="split">|</span>
        <el-switch
          v-model="docInfo.isUseGlobalReturns"
          active-text="使用公共返回参数"
          inactive-text=""
          :active-value="1"
          :inactive-value="0"
        />
        <edit-table ref="responseParamTable" :data="docInfo.responseParams" :module-id="moduleId" :hidden-columns="['required', 'maxLength']" />
      </el-tab-pane>
      <el-tab-pane label="错误码" name="errorCode">
        <el-button type="text" icon="el-icon-plus" @click="onErrorCodeAdd">添加错误码</el-button>
        <edit-table
          ref="errorCodeParamTable"
          :data="docInfo.errorCodeParams"
          :hidden-columns="['required', 'maxLength', 'type', 'enum']"
          :can-add-node="false"
          name-label="错误码"
          description-label="错误描述"
          example-label="解决方案"
        />
      </el-tab-pane>
    </el-tabs>
    <div style="margin: 20px;">
      <el-input v-model="remark" size="mini" placeholder="本次修改备注" show-word-limit maxlength="100" />
    </div>
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
      <doc-view :doc-info-string="docInfoString" :show-opt-bar="false" />
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
import EditTable from '../EditTable'

export default {
  components: { DocView, EditTable },
  data() {
    return {
      params: {},
      initTitle: '',
      activeName: 'info',
      allMethods: ['GET', 'POST', 'PUT', 'PATCH', 'DELETE', 'OPTIONS', 'HEAD'],
      moduleId: '',
      docInfo: {
        docId: '',
        name: '',
        url: '',
        contentType: '',
        description: '',
        httpMethod: 'GET',
        parentId: '',
        moduleId: '',
        projectId: '',
        isUseGlobalHeaders: 1,
        isUseGlobalParams: 1,
        isUseGlobalReturns: 1,
        isShow: 1,
        pathParams: [],
        headerParams: [],
        requestParams: [],
        responseParams: [],
        errorCodeParams: []
      },
      remark: '',
      folders: [],
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
    if (this.$route.path.indexOf('new') > -1) {
      this.initTitle = '新建文档'
    }
    this.initData()
  },
  methods: {
    initFolders(moduleId) {
      if (moduleId) {
        this.get('/doc/folder/list', { moduleId: moduleId }, resp => {
          this.folders = resp.data
        })
      }
    },
    initData: function(id) {
      const docId = id || (this.$route.params.docId || '')
      const parentId = this.$route.params.parentId || ''
      const moduleId = this.$route.params.moduleId || ''
      this.moduleId = moduleId
      this.docInfo.docId = docId
      this.docInfo.parentId = parentId
      this.docInfo.moduleId = moduleId
      this.initFolders(moduleId)
      if (docId) {
        this.get('/doc/detail', { id: docId }, function(resp) {
          const data = resp.data
          this.initDocInfo(data)
          Object.assign(this.docInfo, data)
          this.$store.state.settings.projectId = data.projectId
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
    onUrlInput(url) {
      // 获取{}之间的字符
      const params = url.match(/[^{]+(?=})/g)
      const pathParams = this.docInfo.pathParams
      pathParams.forEach(row => {
        row.isDeleted = 1
      })
      if (params) {
        for (const paramName of params) {
          let add = false
          if (pathParams.length > 0) {
            for (const pathParam of pathParams) {
              // 已经存在
              if (pathParam.name === paramName) {
                pathParam.isDeleted = 0
                add = true
                break
              }
            }
          }
          // 不存在，添加新的
          if (!add) {
            const row = this.getParamNewRow()
            row.name = paramName
            this.docInfo.pathParams.push(row)
          }
        }
      }
    },
    onParamAdd: function(row) {
      row.push(this.getParamNewRow())
    },
    onResponseParamAdd: function() {
      this.docInfo.responseParams.push(this.getParamNewRow())
    },
    onErrorCodeAdd: function() {
      this.docInfo.errorCodeParams.push(this.getParamNewRow())
    },
    // 修改文档内容
    submitForm() {
      this.$refs.docForm.validate((valid) => {
        if (valid) {
          const promiseHeaderArr = this.$refs.headerParamTable.validate()
          const promiseRequestArr = this.$refs.requestParamTable.validate()
          const promiseResponseArr = this.$refs.responseParamTable.validate()
          const promiseErrorCodeArr = this.$refs.errorCodeParamTable.validate()
          const promiseArr = promiseHeaderArr.concat(promiseRequestArr, promiseResponseArr, promiseErrorCodeArr)
          Promise.all(promiseArr).then(validArr => {
            const data = {}
            Object.assign(data, this.docInfo)
            // 到这里来表示全部内容校验通过
            const headerParams = this.getHeaderParamsData()
            const requestParams = this.getRequestParamsData()
            const responseParams = this.getResponseParamsData()
            const errorCodeParams = this.getErrorCodeParamsData()
            Object.assign(data, {
              headerParams: this.formatData(headerParams),
              requestParams: this.formatData(requestParams),
              responseParams: this.formatData(responseParams),
              errorCodeParams: this.formatData(errorCodeParams),
              remark: this.remark
            })
            this.post('/doc/save', data, resp => {
              this.tipSuccess('保存成功')
              const id = resp.data.id
              if (id !== data.id) {
                this.goRoute(`/doc/edit/${data.moduleId}/${id}`)
              } else {
                this.initData()
              }
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
        const viewData = this.deepCopy(this.docInfo)
        viewData.headerParams = this.deepCopy(this.getHeaderParamsData())
        viewData.requestParams = this.deepCopy(this.getRequestParamsData())
        viewData.responseParams = this.deepCopy(this.getResponseParamsData())
        viewData.errorCodeParams = this.deepCopy(this.getErrorCodeParamsData())
        this.initDocInfoCompleteView(viewData)
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
    getErrorCodeParamsData() {
      return this.$refs.errorCodeParamTable.getData()
    },
    formatData(params) {
      const ret = []
      params.forEach(row => {
        const copyRow = Object.assign({}, row)
        if (copyRow.isNew) {
          copyRow.id = null
        }
        const children = copyRow.children
        if (children && children.length > 0) {
          copyRow.children = this.formatData(children)
        }
        ret.push(copyRow)
      })
      return ret
    },
    goBack: function() {
      const projectInfo = this.getCurrentProject()
      this.goProjectHome(projectInfo.id)
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
          const key = keyValue.shift()
          const value = decodeURIComponent(keyValue.join(''))
          this.addParamFormPair(params, key, value)
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
            json.id = this.nextId()
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
        let row = this.findRow(responseParams, name)
        const isExist = row !== null
        if (!isExist) {
          row = this.getParamNewRow(name, value)
        }
        row.example = value
        // 如果有子节点
        if (this.isObject(value)) {
          row.type = 'object'
          row.example = ''
          const children = row.children
          this.doImportResponseParam(children, value)
          children.forEach(child => { child.parentId = row.id })
        } else if (this.isArray(value)) {
          row.type = 'array'
          row.example = ''
          const oneJson = value.length === 0 ? {} : value[0]
          const children = row.children
          this.doImportResponseParam(children, oneJson)
          children.forEach(child => { child.parentId = row.id })
        }
        if (!isExist) {
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
        '0': 'name1=value1&name2=value2',
        '1': `name1:value1
name2:value2
name3:value3`
      }
      return map[this.importParamTemplateModel + ''] || ''
    }
  }
}
</script>
