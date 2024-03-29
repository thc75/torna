<template>
  <div class="app-container route-doc-edit">
    <h3>
      {{ docInfo.name || initTitle }} <span v-show="docInfo.id" class="doc-id">ID：{{ docInfo.id }}</span>
    </h3>
    <el-tabs v-model="activeName" tab-position="left">
      <el-tab-pane :label="$t('baseInfo')" name="info">
        <el-form
          ref="docForm"
          :model="docInfo"
          :rules="rules"
          label-width="110px"
          size="mini"
          style="width: 800px"
        >
          <el-form-item prop="name" :label="$t('docTitle')">
            <el-input v-model="docInfo.name" maxlength="100" show-word-limit />
          </el-form-item>
          <el-form-item prop="description" :label="$t('docDesc')">
            <rich-text-editor :value="docInfo.description" :placeholder="$t('inputContent')" :editable="true" @input="editorInput" />
          </el-form-item>
          <el-form-item prop="url" :label="$t('requestUrl')">
            <el-input v-model="docInfo.url" class="input-with-select" maxlength="200" show-word-limit @input="onUrlInput">
              <el-select slot="prepend" v-model="docInfo.httpMethod" :placeholder="$t('pleaseSelect')" style="width: 100px;">
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
              :name-label="$t('pathVariable')"
              :name-width="200"
              :text-columns="['name']"
              :hidden-columns="['required', 'maxLength', 'enum', 'opt']"
            />
          </el-form-item>
          <el-form-item prop="contentType" label="ContentType">
            <el-select v-model="docInfo.contentType" :clearable="true" :placeholder="$t('pleaseSelect')" style="width: 300px;">
              <el-option v-for="contentType in getEnums().CONTENT_TYPE" :key="contentType" :label="contentType" :value="contentType">
                {{ contentType }}
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item prop="parentId" :label="$t('sourceFolder')">
            <el-select v-model="docInfo.parentId" :placeholder="$t('pleaseSelect')" style="width: 300px;">
              <el-option label="无" :value="0">{{ $t('empty') }}</el-option>
              <el-option v-for="item in folders" :key="item.id" :label="item.name" :value="item.id">
                {{ item.name }}
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item :label="$t('maintainer')">
            <el-input v-model="docInfo.author" maxlength="64" show-word-limit />
          </el-form-item>
          <el-form-item :label="$t('isShow')">
            <el-switch
              v-model="docInfo.isShow"
              active-color="#13ce66"
              inactive-color="#ff4949"
              :active-value="1"
              :inactive-value="0"
            />
          </el-form-item>
          <el-form-item :label="$t('lockDoc')">
            <el-switch
              v-model="docInfo.isLocked"
              active-color="#13ce66"
              inactive-color="#ff4949"
              :active-value="1"
              :inactive-value="0"
            />
            <span class="info-tip">{{ $t('lockDocDesc') }}</span>
          </el-form-item>
          <el-form-item :label="$t('orderIndex')">
            <el-input-number v-model="docInfo.orderIndex" controls-position="right" />
          </el-form-item>
        </el-form>
      </el-tab-pane>
      <el-tab-pane :label="$t('requestHeader')" name="headerParam">
        <el-button type="text" icon="el-icon-plus" @click="onParamAdd(docInfo.headerParams)">{{ $t('newHeader') }}</el-button>
        <el-button type="text" icon="el-icon-bottom-right" @click="onImportHeaderParamAdd">{{ $t('importHeader') }}</el-button>
        <span class="split">|</span>
        <el-switch
          v-model="docInfo.isUseGlobalHeaders"
          :active-text="$t('useCommonHeader')"
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
      <!-- 请求参数 -->
      <el-tab-pane :label="$t('requestParams')" name="requestParam">
        <el-tabs v-model="paramsActive" type="card">
          <el-tab-pane label="Query Parameter" name="tabQueryParams">
            <div class="table-opt-btn">
              <el-button type="text" icon="el-icon-plus" @click="onParamAdd(docInfo.queryParams)">{{ $t('newQueryParam') }}</el-button>
              <el-button type="text" icon="el-icon-bottom-right" @click="onImportQueryParamAdd">{{ $t('importQueryParam') }}</el-button>
            </div>
            <edit-table ref="queryParamTable" :data="docInfo.queryParams" :module-id="moduleId" />
          </el-tab-pane>
          <el-tab-pane name="tabBodyParams">
            <span slot="label" class="tab-pane-label">
              <el-badge :is-dot="docInfo.requestParams.length > 0" type="danger">
                <span>Body Parameter</span>
              </el-badge>
            </span>
            <div class="table-opt-btn">
              <el-switch
                v-model="docInfo.isRequestArray"
                :active-text="$t('isRootArray')"
                inactive-text=""
                :active-value="1"
                :inactive-value="0"
                @change="(val) => onRootArraySwitch(val, 'requestParams')"
              />
              <div v-show="!isEnableRequestRootArray" style="display: inline-block">
                <span class="split">|</span>
                <el-button type="text" icon="el-icon-plus" @click="onParamAdd(docInfo.requestParams)">{{ $t('newBodyParam') }}</el-button>
                <el-button type="text" icon="el-icon-bottom-right" @click="onImportRequestParamAdd">{{ $t('importBodyParam') }}</el-button>
                <span class="split">|</span>
                <el-switch
                  v-model="docInfo.isUseGlobalParams"
                  :active-text="$t('useCommonParam')"
                  inactive-text=""
                  :active-value="1"
                  :inactive-value="0"
                />
              </div>
            </div>
            <root-array-table v-show="isEnableRequestRootArray" ref="requestArrayTable" :data="docInfo.requestParams" :el-type="docInfo.requestArrayType" :module-id="moduleId" />
            <edit-table v-show="!isEnableRequestRootArray" ref="requestParamTable" :data="docInfo.requestParams" :module-id="moduleId" />
          </el-tab-pane>
        </el-tabs>
      </el-tab-pane>
      <el-tab-pane :label="$t('responseParam')" name="responseParam">
        <div class="table-opt-btn">
          <el-switch
            v-model="docInfo.isResponseArray"
            :active-text="$t('isRootArray')"
            inactive-text=""
            :active-value="1"
            :inactive-value="0"
            @change="(val) => onRootArraySwitch(val, 'responseParams')"
          />
          <div v-show="!isEnableResponseRootArray" style="display: inline-block">
            <span class="split">|</span>
            <el-button type="text" icon="el-icon-plus" @click="onResponseParamAdd">{{ $t('newResponseParam') }}</el-button>
            <el-button type="text" icon="el-icon-bottom-right" @click="onImportResponseParamAdd">{{ $t('importResponseParam') }}</el-button>
            <span class="split">|</span>
            <el-switch
              v-model="docInfo.isUseGlobalReturns"
              :active-text="$t('useCommonResponse')"
              inactive-text=""
              :active-value="1"
              :inactive-value="0"
            />
          </div>
        </div>
        <root-array-table v-show="isEnableResponseRootArray" ref="responseArrayTable" :data="docInfo.responseParams" :el-type="docInfo.responseArrayType" :module-id="moduleId" />
        <edit-table v-show="!isEnableResponseRootArray" ref="responseParamTable" :data="docInfo.responseParams" :module-id="moduleId" :hidden-columns="responseHiddenColumns" />
      </el-tab-pane>
      <el-tab-pane :label="$t('errorCode')" name="errorCode">
        <el-button type="text" icon="el-icon-plus" @click="onErrorCodeAdd">{{ $t('newErrorCode') }}</el-button>
        <edit-table
          ref="errorCodeParamTable"
          :data="docInfo.errorCodeParams"
          :hidden-columns="['required', 'maxLength', 'type', 'enum']"
          :can-add-node="false"
          :name-label="$t('errorCode')"
          :description-label="$t('errorDesc')"
          :example-label="$t('solution')"
        />
      </el-tab-pane>
    </el-tabs>
    <div style="margin: 20px;">
      <h3>{{ $t('remark') }}： </h3>
      <rich-text-editor :value="docInfo.remark" :placeholder="$t('inputContent')" :editable="true" @input="remarkEditorInput" />
    </div>
    <div style="margin-top: 10px;">
      <el-button type="text" icon="el-icon-back" @click="goBack">{{ $t('back') }}</el-button>
      <el-button type="primary" icon="el-icon-finished" @click="submitForm">{{ $t('save') }}</el-button>
      <el-button type="success" icon="el-icon-view" @click="viewDoc">{{ $t('preview') }}</el-button>
    </div>
    <!-- dialog -->
    <el-dialog
      :title="$t('preview')"
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
        <el-radio v-show="paramsActive === 'tabBodyParams'" :label="2">{{ $t('importJson') }}</el-radio>
        <el-radio :label="0">{{ $t('importByQueryParam') }}</el-radio>
        <el-radio :label="1">{{ $t('importByBulk') }}</el-radio>
      </el-radio-group>
      <el-input v-model="importParamTemplateValue" type="textarea" :rows="4" :placeholder="getParamTemplatePlaceholder()" />
      <div slot="footer" class="dialog-footer">
        <el-button @click="importParamTemplateDlgShow = false">{{ $t('dlgCancel') }}</el-button>
        <el-button type="primary" @click="callImportParamHandler">{{ $t('dlgSave') }}</el-button>
      </div>
    </el-dialog>
    <el-dialog
      :title="$t('importResponseParam')"
      :visible.sync="paramResponseTemplateDlgShow"
    >
      <el-alert :title="$t('importResponseParamTip')" :closable="false" class="el-alert-tip" />
      <el-form ref="paramResponseTemplateForm" :model="paramResponseTemplateFormData">
        <el-form-item prop="value" :rules="[{ required: true, message: $t('pleaseInputJson'), trigger: 'blur' }]">
          <el-input v-model="paramResponseTemplateFormData.value" type="textarea" :rows="14" :placeholder="$t('jsonType')" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="paramResponseTemplateDlgShow = false">{{ $t('dlgCancel') }}</el-button>
        <el-button type="primary" @click="onImportResponseParamSave">{{ $t('dlgSave') }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<style scoped>
.input-with-select .el-input-group__prepend {
  background-color: #fff;
}
.table-opt-btn .el-button {
  padding: 0;
}
</style>
<script>
import DocView from '@/components/DocView'
import EditTable from '../EditTable'
import RootArrayTable from '../RootArrayTable'
import { init_docInfo_complete_view } from '@/utils/common'
import RichTextEditor from '@/components/RichTextEditor'

export default {
  components: { RichTextEditor, DocView, EditTable, RootArrayTable },
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
        author: '',
        httpMethod: 'GET',
        parentId: '',
        moduleId: '',
        projectId: '',
        isUseGlobalHeaders: 1,
        isUseGlobalParams: 1,
        isUseGlobalReturns: 1,
        isRequestArray: 0,
        isResponseArray: 0,
        requestArrayType: 'object',
        responseArrayType: 'object',
        isShow: 1,
        isLocked: 0,
        pathParams: [],
        headerParams: [],
        queryParams: [],
        requestParams: [],
        responseParams: [],
        errorCodeParams: [],
        orderIndex: this.getEnums().INIT_ORDER_INDEX,
        remark: ''
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
          { min: 1, max: 200, message: '长度在 1 到 200 个字符', trigger: 'blur' }
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
      importParamHandler: null,
      responseHiddenColumns: []
    }
  },
  computed: {
    isEnableRequestRootArray() {
      return this.docInfo.isRequestArray === 1
    },
    isEnableResponseRootArray() {
      return this.docInfo.isResponseArray === 1
    }
  },
  created() {
    this.initResponseHiddenColumns()
    if (this.$route.path.indexOf('new') > -1) {
      this.initTitle = $t('createDoc')
    }
    this.initData()
  },
  methods: {
    unwrapContent(content) {
      if (content === '<p><br></p>') {
        return ''
      }
      return content
    },
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
      } else {
        // init global
        this.get('/doc/globals', { moduleId: moduleId }, resp => {
          const globals = resp.data
          Object.assign(this.docInfo, globals)
        })
      }
      this.initOrderIndex()
    },
    initCopyData(docInfo) {
      docInfo.id = ''
      docInfo.name = `${this.docInfo.name} Copy`
    },
    initOrderIndex() {
      const order = this.$route.query.order
      if (order !== undefined) {
        this.docInfo.orderIndex = order
      }
    },
    initResponseHiddenColumns() {
      this.pmsConfig().then(config => {
        const responseHiddenColumnsConfig = config.responseHiddenColumns
        const responseHiddenColumns = []
        if (responseHiddenColumnsConfig) {
          const columnNames = responseHiddenColumnsConfig.split(',')
          for (const columnName of columnNames) {
            responseHiddenColumns.push(columnName.trim())
          }
        }
        this.responseHiddenColumns = responseHiddenColumns
      })
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
    onParamAdd: function(rows) {
      const item = this.getParamNewRow()
      this.pmsNextOrderIndex(rows).then(order => {
        item.orderIndex = order
      })
      rows.push(item)
    },
    onResponseParamAdd: function() {
      this.onParamAdd(this.docInfo.responseParams)
    },
    onErrorCodeAdd: function() {
      this.onParamAdd(this.docInfo.errorCodeParams)
    },
    // 修改文档内容
    submitForm() {
      this.$refs.docForm.validate((valid) => {
        let rootArrayValid = true
        if (this.isEnableRequestRootArray) {
          rootArrayValid = this.$refs.requestArrayTable.validate()
        }
        if (this.isEnableResponseRootArray) {
          rootArrayValid = this.$refs.responseArrayTable.validate()
        }
        if (valid && rootArrayValid) {
          const promiseHeaderArr = this.$refs.headerParamTable.validate()
          const promiseQueryArr = this.$refs.queryParamTable.validate()
          const promiseRequestArr = this.isEnableRequestRootArray ? [] : this.$refs.requestParamTable.validate()
          const promiseResponseArr = this.isEnableResponseRootArray ? [] : this.$refs.responseParamTable.validate()
          const promiseErrorCodeArr = this.$refs.errorCodeParamTable.validate()
          let promiseArr = promiseHeaderArr.concat(promiseQueryArr, promiseErrorCodeArr)
          if (promiseRequestArr.length > 0) {
            promiseArr = promiseArr.concat(promiseRequestArr)
          }
          if (promiseResponseArr.length > 0) {
            promiseArr = promiseArr.concat(promiseResponseArr)
          }
          Promise.all(promiseArr).then(validArr => {
            const data = {}
            Object.assign(data, this.docInfo)
            // 到这里来表示全部内容校验通过
            const headerParams = this.getHeaderParamsData()
            const queryParams = this.getQueryParamsData()
            const requestParams = this.getRequestParamsArray(data)
            const responseParams = this.getResponseParamsArray(data)
            const errorCodeParams = this.getErrorCodeParamsData()
            Object.assign(data, {
              headerParams: this.formatData(headerParams),
              queryParams: this.formatData(queryParams),
              requestParams: this.formatData(requestParams),
              responseParams: this.formatData(responseParams),
              errorCodeParams: this.formatData(errorCodeParams)
            })
            data.description = this.unwrapContent(data.description)
            data.remark = this.unwrapContent(data.remark)
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
            this.tipError($t('pleaseFinishForm'))
          }) // 加上这个控制台不会报Uncaught (in promise)
        } else {
          this.tipError($t('pleaseFinishForm'))
        }
      })
    },
    getRequestParamsArray(data) {
      let requestParams
      if (this.isEnableRequestRootArray) {
        const arrayData = this.getRootRequestBodyData()
        requestParams = arrayData.data
        data.requestArrayType = arrayData.type
      } else {
        requestParams = this.getRequestParamsData()
      }
      return requestParams
    },
    getResponseParamsArray(data) {
      let responseParams
      if (this.isEnableResponseRootArray) {
        const arrayData = this.getRootResponseBodyData()
        responseParams = arrayData.data
        data.responseArrayType = arrayData.type
      } else {
        responseParams = this.getResponseParamsData()
      }
      return responseParams
    },
    getRootRequestBodyData() {
      return this.$refs.requestArrayTable.getData()
    },
    getRootResponseBodyData() {
      return this.$refs.responseArrayTable.getData()
    },
    viewDoc: function() {
      this.viewDialogVisible = true
      this.$nextTick(() => {
        const viewData = this.deepCopy(this.docInfo)
        const requestParams = this.getRequestParamsArray(viewData)
        const responseParams = this.getResponseParamsArray(viewData)
        viewData.headerParams = this.deepCopy(this.getHeaderParamsData())
        viewData.queryParams = this.deepCopy(this.getQueryParamsData())
        viewData.requestParams = this.deepCopy(requestParams)
        viewData.responseParams = this.deepCopy(responseParams)
        viewData.errorCodeParams = this.deepCopy(this.getErrorCodeParamsData())
        init_docInfo_complete_view(viewData)
        this.docInfoString = JSON.stringify(viewData)
      })
    },
    onRootArraySwitch(val, key) {
      const delAndHide = (rows) => {
        rows.forEach(row => {
          row.isDeleted = 1
          row.hidden = true
          const children = row.children
          if (children && children.length > 0) {
            delAndHide(children)
          }
        })
      }
      delAndHide(this.docInfo[key])
      if (val === 1) {
        const root = this.getParamNewRow()
        root.type = key === 'requestParams' ? this.docInfo.requestArrayType : this.docInfo.responseArrayType
        this.docInfo[key].push(root)
      }
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
      const projectId = this.docInfo.projectId || this.getCurrentProject().id
      this.goProjectHome(projectId)
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
    },
    editorInput(content) {
      this.docInfo.description = content
    },
    remarkEditorInput(content) {
      this.docInfo.remark = content
    }
  }
}
</script>
