<template>
  <div class="app-container route-doc-edit">
    <h3>
      {{ docInfo.name || initTitle }} <span v-show="docInfo.id" class="doc-id">ID：{{ docInfo.id }}</span>
    </h3>
    <el-tabs v-model="activeName" tab-position="left">
      <el-tab-pane :label="$ts('baseInfo')" name="info">
        <el-form
          ref="docForm"
          :model="docInfo"
          :rules="rules"
          label-width="110px"
          size="mini"
          style="width: 800px"
        >
          <el-form-item prop="name" :label="$ts('docTitle')">
            <el-input v-model="docInfo.name" maxlength="100" show-word-limit />
          </el-form-item>
          <el-form-item prop="description" :label="$ts('docDesc')">
            <el-input v-model="docInfo.description" type="textarea" :rows="4" maxlength="200" show-word-limit />
          </el-form-item>
          <el-form-item prop="url" :label="$ts('requestUrl')">
            <el-input v-model="docInfo.url" class="input-with-select" maxlength="100" show-word-limit @input="onUrlInput">
              <el-select slot="prepend" v-model="docInfo.httpMethod" :placeholder="$ts('pleaseSelect')" style="width: 100px;">
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
              :name-label="$ts('pathVariable')"
              :name-width="200"
              :text-columns="['name']"
              :hidden-columns="['required', 'maxLength', 'enum', 'opt']"
            />
          </el-form-item>
          <el-form-item prop="contentType" label="ContentType">
            <el-select v-model="docInfo.contentType" :clearable="true" :placeholder="$ts('pleaseSelect')" style="width: 300px;">
              <el-option v-for="contentType in getEnums().CONTENT_TYPE" :key="contentType" :label="contentType" :value="contentType">
                {{ contentType }}
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item prop="parentId" :label="$ts('sourceFolder')">
            <el-select v-model="docInfo.parentId" :placeholder="$ts('pleaseSelect')" style="width: 300px;">
              <el-option label="无" :value="0">{{ $ts('empty') }}</el-option>
              <el-option v-for="item in folders" :key="item.id" :label="item.name" :value="item.id">
                {{ item.name }}
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item :label="$ts('isShow')">
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
      <el-tab-pane :label="$ts('requestHeader')" name="headerParam">
        <el-button type="text" icon="el-icon-plus" @click="onParamAdd(docInfo.headerParams)">{{ $ts('newHeader') }}</el-button>
        <el-button type="text" icon="el-icon-bottom-right" @click="onImportHeaderParamAdd">{{ $ts('importHeader') }}</el-button>
        <span class="split">|</span>
        <el-switch
          v-model="docInfo.isUseGlobalHeaders"
          :active-text="$ts('useCommonHeader')"
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
      <el-tab-pane :label="$ts('requestParams')" name="requestParam">
        <el-tabs v-model="paramsActive" type="card">
          <el-tab-pane label="Query Parameter" name="tabQueryParams">
            <el-button type="text" icon="el-icon-plus" @click="onParamAdd(docInfo.queryParams)">{{ $ts('newQueryParam') }}</el-button>
            <el-button type="text" icon="el-icon-bottom-right" @click="onImportQueryParamAdd">{{ $ts('importQueryParam') }}</el-button>
            <edit-table ref="queryParamTable" :data="docInfo.queryParams" :module-id="moduleId" :hidden-columns="['enum']" />
          </el-tab-pane>
          <el-tab-pane label="Body Parameter" name="tabBodyParams">
            <el-button type="text" icon="el-icon-plus" @click="onParamAdd(docInfo.requestParams)">{{ $ts('newBodyParam') }}</el-button>
            <el-button type="text" icon="el-icon-bottom-right" @click="onImportRequestParamAdd">{{ $ts('importBodyParam') }}</el-button>
            <span class="split">|</span>
            <el-switch
              v-model="docInfo.isUseGlobalParams"
              :active-text="$ts('useCommonParam')"
              inactive-text=""
              :active-value="1"
              :inactive-value="0"
            />
            <edit-table ref="requestParamTable" :data="docInfo.requestParams" :module-id="moduleId" :hidden-columns="['enum']" />
          </el-tab-pane>
        </el-tabs>
      </el-tab-pane>
      <el-tab-pane :label="$ts('responseParam')" name="responseParam">
        <el-button type="text" icon="el-icon-plus" @click="onResponseParamAdd">{{ $ts('newResponseParam') }}</el-button>
        <el-button type="text" icon="el-icon-bottom-right" @click="onImportResponseParamAdd">{{ $ts('importResponseParam') }}</el-button>
        <span class="split">|</span>
        <el-switch
          v-model="docInfo.isUseGlobalReturns"
          :active-text="$ts('useCommonResponse')"
          inactive-text=""
          :active-value="1"
          :inactive-value="0"
        />
        <edit-table ref="responseParamTable" :data="docInfo.responseParams" :module-id="moduleId" :hidden-columns="['required', 'maxLength']" />
      </el-tab-pane>
      <el-tab-pane :label="$ts('errorCode')" name="errorCode">
        <el-button type="text" icon="el-icon-plus" @click="onErrorCodeAdd">{{ $ts('newErrorCode') }}</el-button>
        <edit-table
          ref="errorCodeParamTable"
          :data="docInfo.errorCodeParams"
          :hidden-columns="['required', 'maxLength', 'type', 'enum']"
          :can-add-node="false"
          :name-label="$ts('errorCode')"
          :description-label="$ts('errorDesc')"
          :example-label="$ts('solution')"
        />
      </el-tab-pane>
    </el-tabs>
    <div style="margin: 20px;">
      <el-input v-model="remark" size="mini" :placeholder="$ts('currentUpdateRemark')" show-word-limit maxlength="100" />
    </div>
    <div style="margin-top: 10px;">
      <el-button type="text" icon="el-icon-back" @click="goBack">{{ $ts('back') }}</el-button>
      <el-button type="primary" icon="el-icon-finished" @click="submitForm">{{ $ts('save') }}</el-button>
      <el-button type="success" icon="el-icon-view" @click="viewDoc">{{ $ts('preview') }}</el-button>
    </div>
    <!-- dialog -->
    <el-dialog
      :title="$ts('preview')"
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
        <el-radio v-show="paramsActive === 'tabBodyParams'" :label="2">{{ $ts('importJson') }}</el-radio>
        <el-radio :label="0">{{ $ts('importByQueryParam') }}</el-radio>
        <el-radio :label="1">{{ $ts('importByBulk') }}</el-radio>
      </el-radio-group>
      <el-input v-model="importParamTemplateValue" type="textarea" :rows="4" :placeholder="getParamTemplatePlaceholder()" />
      <div slot="footer" class="dialog-footer">
        <el-button @click="importParamTemplateDlgShow = false">{{ $ts('dlgCancel') }}</el-button>
        <el-button type="primary" @click="callImportParamHandler">{{ $ts('dlgSave') }}</el-button>
      </div>
    </el-dialog>
    <el-dialog
      :title="$ts('importResponseParam')"
      :visible.sync="paramResponseTemplateDlgShow"
    >
      <el-alert :title="$ts('importResponseParamTip')" :closable="false" class="el-alert-tip"/>
      <el-form ref="paramResponseTemplateForm" :model="paramResponseTemplateFormData">
        <el-form-item prop="value" :rules="[{ required: true, message: $ts('pleaseInputJson'), trigger: 'blur' }]">
          <el-input v-model="paramResponseTemplateFormData.value" type="textarea" :rows="14" :placeholder="$ts('jsonType')" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="paramResponseTemplateDlgShow = false">{{ $ts('dlgCancel') }}</el-button>
        <el-button type="primary" @click="onImportResponseParamSave">{{ $ts('dlgSave') }}</el-button>
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
        queryParams: [],
        requestParams: [],
        responseParams: [],
        errorCodeParams: []
      },
      paramsActive: 'tabQueryParams',
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
    },
    initCopyData(docInfo) {
      docInfo.id = ''
      docInfo.name = `${this.docInfo.name} Copy`
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
          const promiseQueryArr = this.$refs.queryParamTable.validate()
          const promiseRequestArr = this.$refs.requestParamTable.validate()
          const promiseResponseArr = this.$refs.responseParamTable.validate()
          const promiseErrorCodeArr = this.$refs.errorCodeParamTable.validate()
          const promiseArr = promiseHeaderArr.concat(promiseQueryArr, promiseRequestArr, promiseResponseArr, promiseErrorCodeArr)
          Promise.all(promiseArr).then(validArr => {
            const data = {}
            Object.assign(data, this.docInfo)
            // 到这里来表示全部内容校验通过
            const headerParams = this.getHeaderParamsData()
            const queryParams = this.getQueryParamsData()
            const requestParams = this.getRequestParamsData()
            const responseParams = this.getResponseParamsData()
            const errorCodeParams = this.getErrorCodeParamsData()
            Object.assign(data, {
              headerParams: this.formatData(headerParams),
              queryParams: this.formatData(queryParams),
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
        viewData.queryParams = this.deepCopy(this.getQueryParamsData())
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
    getQueryParamsData() {
      return this.$refs.queryParamTable.getData()
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
    onImportQueryParamAdd: function() {
      this.importParamTemplateModel = 0
      this.importParamTemplateDlgShow = true
      this.importParamTemplateValue = ''
      this.importParamTemplateTitle = '导入参数'
      const that = this
      this.importParamHandler = function() {
        that.onImportParamSave(that.docInfo.queryParams)
      }
    },
    onImportRequestParamAdd: function() {
      this.importParamTemplateModel = 2
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
        if (this.importParamTemplateModel === 2) {
          this.parseJSON(value, (json) => {
            this.doImportParam(params, json)
            this.importParamTemplateDlgShow = false
          }, () => this.tipError('JSON格式错误'))
          return
        }
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
            this.doImportParam(responseParams, json)
            this.paramResponseTemplateDlgShow = false
          }, () => this.tipError('JSON格式错误'))
        }
      })
    },
    /**
     * 导入参数
     * @param params 响应参数数组
     * @param json 当前json
     */
    doImportParam: function(params, json) {
      for (const name in json) {
        const value = json[name]
        let row = this.findRow(params, name)
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
          this.doImportParam(children, value)
          children.forEach(child => { child.parentId = row.id })
        } else if (this.isArray(value)) {
          row.type = 'array'
          row.example = ''
          const oneJson = value.length === 0 ? {} : value[0]
          const children = row.children
          this.doImportParam(children, oneJson)
          children.forEach(child => { child.parentId = row.id })
        }
        if (!isExist) {
          params.push(row)
        }
      }
    },
    findRow: function(params, name) {
      for (let i = 0; i < params.length; i++) {
        const r = params[i]
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
name3:value3`,
        '2': 'json'
      }
      return map[this.importParamTemplateModel + ''] || ''
    }
  }
}
</script>
